package com.saferoute.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for Road entity.
 * Simplifies geographic data for frontend consumption (e.g., Leaflet.js).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoadResponseDTO {
    private UUID id;
    private String roadType;
    private String contractorName;
    private BigDecimal sanctionedAmount;
    private LocalDate lastRelayedDate;
    private String executiveEngineer;
    
    // Simplifies the LineString into a list of [longitude, latitude] or [latitude, longitude] pairs.
    // For Leaflet.js, typical format is [latitude, longitude].
    private List<double[]> coordinates;
}
