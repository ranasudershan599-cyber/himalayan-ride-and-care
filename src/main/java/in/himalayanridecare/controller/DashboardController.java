package in.himalayanridecare.controller;

import in.himalayanridecare.repository.DashboardJdbcRepository;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardJdbcRepository dashboard;
    public DashboardController(DashboardJdbcRepository dashboard){this.dashboard=dashboard;}
    @GetMapping public Map<String,Object> summary(){return dashboard.summary();}
}
