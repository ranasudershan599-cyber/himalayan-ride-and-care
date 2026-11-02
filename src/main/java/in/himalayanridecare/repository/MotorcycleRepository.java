package in.himalayanridecare.repository;
import in.himalayanridecare.model.Motorcycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface MotorcycleRepository extends JpaRepository<Motorcycle,Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Motorcycle> findLockedById(Long id);
}
