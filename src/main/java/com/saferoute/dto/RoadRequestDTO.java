package com.saferoute.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoadRequestDTO {
    private String roadType;
    private String contractorName;
    private BigDecimal sanctionedAmount;
    private LocalDate lastRelayedDate;
    private String executiveEngineer;
    
    // Accepts coordinates in the same format as RoadResponseDTO to build the JTS LineString
    // Typical format is a list of [latitude, longitude] pairs.
    private List<double[]> coordinates;
}
