// Archivo: src/main/java/com/SIDC/backend/controllers/DashboardController.java
package com.SIDC.backend.controllers;

import com.SIDC.backend.dto.DashboardResponseDTO;
import com.SIDC.backend.services.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/resumen")
    public ResponseEntity<DashboardResponseDTO> obtenerResumen() {
        return ResponseEntity.ok(dashboardService.obtenerResumenDashboard());
    }
}