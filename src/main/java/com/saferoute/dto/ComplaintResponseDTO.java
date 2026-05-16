package com.saferoute.dto;

import com.saferoute.models.ComplaintStatus;
import com.saferoute.models.HazardSeverity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintResponseDTO {
    private UUID id;
    private ComplaintStatus status;
    private HazardSeverity severity;
    private String damageType;
    private String imageUrl;
    
    // Flattened geometry [longitude, latitude]
    private List<Double> coordinates;
    
    private UUID roadId;
    private LocalDateTime createdAt;
}
