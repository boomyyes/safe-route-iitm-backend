package com.saferoute.dto;

import com.saferoute.models.HazardSeverity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintRequestDTO {
    private double latitude;
    private double longitude;
    private String damageType;
    private HazardSeverity severity;
    private UUID roadId;
}
