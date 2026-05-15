package com.saferoute.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity representing a road segment in the SafeRoute platform.
 * Includes geographic line data for spatial queries and rendering.
 *
 * @author Avanish Wankhede
 */
@Entity
@Table(name = "roads")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Road {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "road_type", nullable = false)
    private String roadType;

    @Column(name = "contractor_name")
    private String contractorName;

    @Column(name = "sanctioned_amount", precision = 15, scale = 2)
    private BigDecimal sanctionedAmount;

    @Column(name = "last_relayed_date")
    private LocalDate lastRelayedDate;

    @Column(name = "executive_engineer")
    private String executiveEngineer;

    @Column(name = "geom", columnDefinition = "geometry(LineString,4326)", nullable = false)
    private LineString geom;

}
