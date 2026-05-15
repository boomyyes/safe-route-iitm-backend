package com.saferoute.controllers;

import com.saferoute.dto.RoadRequestDTO;
import com.saferoute.dto.RoadResponseDTO;
import com.saferoute.services.RoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roads")
public class RoadController {

    private final RoadService roadService;

    @Autowired
    public RoadController(RoadService roadService) {
        this.roadService = roadService;
    }

    /**
     * Fetch a specific road by its UUID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoadResponseDTO> getRoadById(@PathVariable UUID id) {
        RoadResponseDTO road = roadService.getRoadById(id);
        return ResponseEntity.ok(road);
    }

    /**
     * Perform a spatial search to find roads near a given coordinate.
     * Uses PostGIS native ST_DWithin query.
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<RoadResponseDTO>> getNearbyRoads(
            @RequestParam("lat") double lat,
            @RequestParam("lng") double lng,
            @RequestParam(value = "radius", defaultValue = "5000") double radiusMeters) {
            
        List<RoadResponseDTO> nearbyRoads = roadService.findRoadsNearLocation(lng, lat, radiusMeters);
        return ResponseEntity.ok(nearbyRoads);
    }

    /**
     * Ingest a batch of clean roads from the Python ETL pipeline.
     */
    @PostMapping("/bulk-ingest")
    public ResponseEntity<String> bulkIngestRoads(@RequestBody List<RoadRequestDTO> roads) {
        roadService.bulkSaveRoads(roads);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Successfully ingested " + roads.size() + " road records.");
    }
}
