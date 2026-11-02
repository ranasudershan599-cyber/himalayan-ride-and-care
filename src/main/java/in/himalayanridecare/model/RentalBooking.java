package in.himalayanridecare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity @Table(name="rental_bookings")
public class RentalBooking {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false,fetch=FetchType.EAGER) @JoinColumn(name="customer_id",nullable=false) private Customer customer;
    @ManyToOne(optional=false,fetch=FetchType.EAGER) @JoinColumn(name="motorcycle_id",nullable=false) private Motorcycle motorcycle;
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="destination_id") private Destination destination;
    @NotNull @FutureOrPresent @Column(name="start_date",nullable=false) private LocalDate startDate;
    @NotNull @Future @Column(name="end_date",nullable=false) private LocalDate endDate;
    @NotBlank @Column(nullable=false,length=20) private String status="PENDING";
    @Column(name="pickup_odometer_km") private Integer pickupOdometerKm;
    @Column(name="return_odometer_km") private Integer returnOdometerKm;
    @Size(max=100) @Column(name="staff_name",length=100) private String staffName;
    protected RentalBooking() {}
    public RentalBooking(Customer c,Motorcycle m,Destination d,LocalDate s,LocalDate e){customer=c;motorcycle=m;destination=d;startDate=s;endDate=e;}
    public Long getId(){return id;} public Customer getCustomer(){return customer;} public void setCustomer(Customer v){customer=v;} public Motorcycle getMotorcycle(){return motorcycle;} public void setMotorcycle(Motorcycle v){motorcycle=v;}
    public Destination getDestination(){return destination;} public void setDestination(Destination v){destination=v;} public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate v){startDate=v;}
    public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate v){endDate=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public Integer getPickupOdometerKm(){return pickupOdometerKm;} public void setPickupOdometerKm(Integer v){pickupOdometerKm=v;} public Integer getReturnOdometerKm(){return returnOdometerKm;} public void setReturnOdometerKm(Integer v){returnOdometerKm=v;}
    public String getStaffName(){return staffName;} public void setStaffName(String v){staffName=v;}
}
