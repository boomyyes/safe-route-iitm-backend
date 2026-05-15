package com.saferoute.controllers;

import com.saferoute.dto.RoadResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roads")
public class RoadController {

    @GetMapping("/mock")
    public ResponseEntity<List<RoadResponseDTO>> getMockRoads() {
        // Mock data representing a realistic Indian road network (e.g., around Mumbai/Pune)
        RoadResponseDTO road1 = new RoadResponseDTO(
                UUID.randomUUID(),
                "NH-48",
                "L&T Infrastructure",
                new BigDecimal("450000000.00"),
                LocalDate.of(2025, 1, 15),
                "S.K. Sharma",
                Arrays.asList(
                        new double[]{18.5204, 73.8567}, // Pune
                        new double[]{18.6161, 73.7981}, // Pimpri-Chinchwad
                        new double[]{18.7516, 73.4024}, // Lonavala
                        new double[]{19.0760, 72.8777}  // Mumbai
                )
        );

        RoadResponseDTO road2 = new RoadResponseDTO(
                UUID.randomUUID(),
                "SH-15",
                "Afcons Infrastructure",
                new BigDecimal("120000000.00"),
                LocalDate.of(2024, 11, 20),
                "R.K. Patil",
                Arrays.asList(
                        new double[]{19.2183, 72.9781}, // Thane
                        new double[]{19.3130, 73.0543}, // Bhiwandi
                        new double[]{19.4975, 73.0163}  // Virar
                )
        );

        return ResponseEntity.ok(Arrays.asList(road1, road2));
    }
}
