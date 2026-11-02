package in.himalayanridecare.repository;
import in.himalayanridecare.model.RentalBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
public interface RentalBookingRepository extends JpaRepository<RentalBooking,Long> {
    boolean existsByMotorcycleIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long motorcycleId, java.util.Collection<String> statuses, LocalDate requestedEnd, LocalDate requestedStart);
    boolean existsByMotorcycleIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndIdNot(Long motorcycleId, java.util.Collection<String> statuses, LocalDate requestedEnd, LocalDate requestedStart, Long id);
}
