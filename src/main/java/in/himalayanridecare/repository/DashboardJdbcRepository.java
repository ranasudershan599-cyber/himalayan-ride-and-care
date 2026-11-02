package in.himalayanridecare.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.Map;

@Repository
public class DashboardJdbcRepository {
    private final JdbcTemplate jdbc;
    public DashboardJdbcRepository(JdbcTemplate jdbc){this.jdbc=jdbc;}
    public Map<String,Object> summary(){
        return jdbc.queryForMap("SELECT (SELECT COUNT(*) FROM motorcycles WHERE status='AVAILABLE') AS availableMotorcycles, " +
                "(SELECT COUNT(*) FROM rental_bookings WHERE status IN ('CONFIRMED','ACTIVE')) AS currentRentals, " +
                "(SELECT COUNT(*) FROM assistance_requests WHERE status IN ('OPEN','ASSIGNED','IN_PROGRESS')) AS openAssistanceCases");
    }
}
