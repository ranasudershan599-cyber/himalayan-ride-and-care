package in.himalayanridecare.config;

import in.himalayanridecare.repository.*;
import in.himalayanridecare.model.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;

@Configuration
public class DataLoader {
    @Bean CommandLineRunner seedCaseStudyData(CustomerRepository customers, MotorcycleRepository motorcycles, DestinationRepository destinations) {
        return args -> {
            if (customers.count() == 0) {
                customers.save(new Customer("Asha Sharma", "9876543210", "asha@example.com"));
                customers.save(new Customer("Rohan Mehta", "9876501234", "rohan@example.com"));
            }
            if (motorcycles.count() == 0) {
                motorcycles.save(new Motorcycle("HP-01-HR-2026", "Royal Enfield Himalayan 450", new BigDecimal("2200.00"), 12500, "AVAILABLE"));
                motorcycles.save(new Motorcycle("HP-02-HR-2026", "Royal Enfield Scram 411", new BigDecimal("1800.00"), 8900, "AVAILABLE"));
                motorcycles.save(new Motorcycle("HP-03-HR-2026", "Adventure Bike 390", new BigDecimal("2000.00"), 15300, "MAINTENANCE"));
            }
            if (destinations.count() == 0) {
                destinations.save(new Destination("Manali", "Mountain town and common Himalayan ride starting point"));
                destinations.save(new Destination("Spiti Valley", "High-altitude cold desert valley route"));
                destinations.save(new Destination("Leh", "Ladakh destination for long-distance motorcycle tours"));
            }
        };
    }
}
