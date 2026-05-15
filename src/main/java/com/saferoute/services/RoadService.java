package com.saferoute.services;

import com.saferoute.dto.RoadRequestDTO;
import com.saferoute.dto.RoadResponseDTO;

import java.util.List;
import java.util.UUID;

public interface RoadService {
    
    RoadResponseDTO getRoadById(UUID id);
    
    List<RoadResponseDTO> findRoadsNearLocation(double longitude, double latitude, double radiusMeters);
    
    void bulkSaveRoads(List<RoadRequestDTO> roads);
}
