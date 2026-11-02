package in.himalayanridecare.controller;

import in.himalayanridecare.model.*;
import in.himalayanridecare.repository.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
class CustomerController {
    private final CustomerRepository repo;
    CustomerController(CustomerRepository repo){this.repo=repo;}
    @GetMapping List<Customer> all(){return repo.findAll();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) Customer create(@Valid @RequestBody Customer input){return repo.save(input);}
}

@RestController
@RequestMapping("/api/motorcycles")
class MotorcycleController {
    private final MotorcycleRepository repo;
    MotorcycleController(MotorcycleRepository repo){this.repo=repo;}
    @GetMapping List<Motorcycle> all(){return repo.findAll();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) Motorcycle create(@Valid @RequestBody Motorcycle input){return repo.save(input);}
    @PatchMapping("/{id}/status") Motorcycle status(@PathVariable Long id,@RequestBody StatusRequest request){Motorcycle bike=repo.findById(id).orElseThrow(()->new NotFoundException("Motorcycle not found")); String s=request.status().toUpperCase(); if(!List.of("AVAILABLE","MAINTENANCE","INACTIVE").contains(s)) throw new IllegalArgumentException("Invalid motorcycle status"); bike.setStatus(s); return repo.save(bike);}
}

@RestController
@RequestMapping("/api/destinations")
class DestinationController {
    private final DestinationRepository repo;
    DestinationController(DestinationRepository repo){this.repo=repo;}
    @GetMapping List<Destination> all(){return repo.findAll();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) Destination create(@Valid @RequestBody Destination input){return repo.save(input);}
}

@RestController
@RequestMapping("/api/rentals")
class RentalController {
    private static final List<String> RESERVED=List.of("PENDING","CONFIRMED","ACTIVE");
    private final RentalBookingRepository bookings; private final CustomerRepository customers; private final MotorcycleRepository motorcycles; private final DestinationRepository destinations;
    RentalController(RentalBookingRepository b,CustomerRepository c,MotorcycleRepository m,DestinationRepository d){bookings=b;customers=c;motorcycles=m;destinations=d;}
    @GetMapping List<RentalBooking> all(){return bookings.findAll();}
    @GetMapping("/{id}") RentalBooking one(@PathVariable Long id){return bookings.findById(id).orElseThrow(()->new NotFoundException("Rental booking not found"));}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @Transactional RentalBooking create(@Valid @RequestBody RentalInput in){
        if(!in.endDate().isAfter(in.startDate())) throw new IllegalArgumentException("Return date must be after pickup date");
        Customer c=customers.findById(in.customerId()).orElseThrow(()->new NotFoundException("Customer not found"));
        Motorcycle m=motorcycles.findLockedById(in.motorcycleId()).orElseThrow(()->new NotFoundException("Motorcycle not found"));
        if(!m.getStatus().equals("AVAILABLE")) throw new IllegalArgumentException("Motorcycle is not available for rental");
        if(bookings.existsByMotorcycleIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(m.getId(),RESERVED,in.endDate(),in.startDate())) throw new IllegalArgumentException("Motorcycle already has an overlapping rental");
        Destination d=in.destinationId()==null?null:destinations.findById(in.destinationId()).orElseThrow(()->new NotFoundException("Destination not found"));
        return bookings.save(new RentalBooking(c,m,d,in.startDate(),in.endDate()));
    }
    @PatchMapping("/{id}/status") RentalBooking status(@PathVariable Long id,@RequestBody StatusRequest request){
        RentalBooking b=one(id); String next=request.status().toUpperCase();
        if(!List.of("CONFIRMED","CANCELLED").contains(next)) throw new IllegalArgumentException("Use the checkout or return action for those status changes");
        if(next.equals("CONFIRMED")&&!b.getStatus().equals("PENDING")) throw new IllegalArgumentException("Only a pending rental can be confirmed");
        if(next.equals("CANCELLED")&&!List.of("PENDING","CONFIRMED").contains(b.getStatus())) throw new IllegalArgumentException("Only a pending or confirmed rental can be cancelled");
        if(next.equals("CONFIRMED")&&!b.getMotorcycle().getStatus().equals("AVAILABLE")) throw new IllegalArgumentException("Motorcycle is not available for rental");
        if(next.equals("ACTIVE")&&!b.getStatus().equals("CONFIRMED")) throw new IllegalArgumentException("Only a confirmed rental can be checked out");
        b.setStatus(next); return bookings.save(b);
    }
    @PostMapping("/{id}/checkout") RentalBooking checkout(@PathVariable Long id,@Valid @RequestBody OdometerRequest input){RentalBooking b=one(id);if(!b.getStatus().equals("CONFIRMED"))throw new IllegalArgumentException("Confirm the rental before checkout");if(input.odometerKm()<b.getMotorcycle().getOdometerKm())throw new IllegalArgumentException("Pickup odometer cannot be below the motorcycle's current reading");b.setPickupOdometerKm(input.odometerKm());b.setStatus("ACTIVE");return bookings.save(b);}
    @PostMapping("/{id}/return") RentalBooking returned(@PathVariable Long id,@Valid @RequestBody OdometerRequest input){RentalBooking b=one(id);if(!b.getStatus().equals("ACTIVE"))throw new IllegalArgumentException("Only an active rental can be returned");if(input.odometerKm()<b.getPickupOdometerKm())throw new IllegalArgumentException("Return odometer cannot be below pickup reading");b.setReturnOdometerKm(input.odometerKm());b.setStatus("COMPLETED");b.getMotorcycle().setOdometerKm(input.odometerKm());motorcycles.save(b.getMotorcycle());return bookings.save(b);}
}

@RestController
@RequestMapping("/api/payments")
class PaymentController {
    private final PaymentRecordRepository repo; private final RentalBookingRepository bookings;
    PaymentController(PaymentRecordRepository p,RentalBookingRepository b){repo=p;bookings=b;}
    @GetMapping List<PaymentRecord> all(){return repo.findAll();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) PaymentRecord create(@Valid @RequestBody PaymentInput in){RentalBooking b=bookings.findById(in.bookingId()).orElseThrow(()->new NotFoundException("Rental booking not found"));if(!List.of("CASH","CARD","BANK_TRANSFER","UPI").contains(in.method().toUpperCase()))throw new IllegalArgumentException("Unsupported payment method");return repo.save(new PaymentRecord(b,in.amount(),in.method().toUpperCase(),"RECORDED"));}
}

@RestController
@RequestMapping("/api/assistance")
class AssistanceController {
    private final AssistanceRequestRepository repo; private final CustomerRepository customers; private final RentalBookingRepository bookings;
    AssistanceController(AssistanceRequestRepository r,CustomerRepository c,RentalBookingRepository b){repo=r;customers=c;bookings=b;}
    @GetMapping List<AssistanceRequest> all(){return repo.findAll();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) AssistanceRequest create(@Valid @RequestBody AssistanceInput in){Customer c=customers.findById(in.customerId()).orElseThrow(()->new NotFoundException("Customer not found"));RentalBooking b=in.bookingId()==null?null:bookings.findById(in.bookingId()).orElseThrow(()->new NotFoundException("Rental booking not found"));String p=in.priority().toUpperCase();if(!List.of("LOW","NORMAL","HIGH","URGENT").contains(p))throw new IllegalArgumentException("Invalid priority");return repo.save(new AssistanceRequest(c,b,in.category(),p,in.description(),in.location()));}
    @PatchMapping("/{id}") AssistanceRequest update(@PathVariable Long id,@RequestBody AssistanceUpdate input){AssistanceRequest r=repo.findById(id).orElseThrow(()->new NotFoundException("Assistance case not found"));if(input.status()!=null){String s=input.status().toUpperCase();if(!List.of("OPEN","ASSIGNED","IN_PROGRESS","RESOLVED","CLOSED","CANCELLED").contains(s))throw new IllegalArgumentException("Invalid assistance status");r.setStatus(s);}if(input.assignedTo()!=null)r.setAssignedTo(input.assignedTo().trim());return repo.save(r);}
}

record StatusRequest(@NotBlank String status) {}
record OdometerRequest(@NotNull @PositiveOrZero Integer odometerKm) {}
record RentalInput(@NotNull @Positive Long customerId,@NotNull @Positive Long motorcycleId,Long destinationId,@NotNull @FutureOrPresent LocalDate startDate,@NotNull @Future LocalDate endDate) {}
record PaymentInput(@NotNull @Positive Long bookingId,@NotNull @Positive BigDecimal amount,@NotBlank String method) {}
record AssistanceInput(@NotNull @Positive Long customerId,Long bookingId,@NotBlank String category,@NotBlank String priority,@NotBlank @Size(max=1000) String description,@NotBlank @Size(max=200) String location) {}
record AssistanceUpdate(String status,String assignedTo) {}
