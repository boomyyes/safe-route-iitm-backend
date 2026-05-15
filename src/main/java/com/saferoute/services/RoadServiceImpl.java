package com.saferoute.services;

import com.saferoute.dto.RoadRequestDTO;
import com.saferoute.dto.RoadResponseDTO;
import com.saferoute.exceptions.ResourceNotFoundException;
import com.saferoute.models.Road;
import com.saferoute.repositories.RoadRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoadServiceImpl implements RoadService {

    private final RoadRepository roadRepository;
    private final GeometryFactory geometryFactory;

    @Autowired
    public RoadServiceImpl(RoadRepository roadRepository) {
        this.roadRepository = roadRepository;
        // PostGIS SRID 4326 (WGS 84)
        this.geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    }

    @Override
    public RoadResponseDTO getRoadById(UUID id) {
        Road road = roadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Road not found with ID: " + id));
        return convertToResponseDTO(road);
    }

    @Override
    public List<RoadResponseDTO> findRoadsNearLocation(double longitude, double latitude, double radiusMeters) {
        List<Road> roads = roadRepository.findRoadsNearLocation(longitude, latitude, radiusMeters);
        return roads.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void bulkSaveRoads(List<RoadRequestDTO> roadsDTO) {
        List<Road> roadsToSave = roadsDTO.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
        roadRepository.saveAll(roadsToSave);
    }

    // Helper to convert Entity to ResponseDTO
    private RoadResponseDTO convertToResponseDTO(Road road) {
        RoadResponseDTO dto = new RoadResponseDTO();
        dto.setId(road.getId());
        dto.setRoadType(road.getRoadType());
        dto.setContractorName(road.getContractorName());
        dto.setSanctionedAmount(road.getSanctionedAmount());
        dto.setLastRelayedDate(road.getLastRelayedDate());
        dto.setExecutiveEngineer(road.getExecutiveEngineer());

        // Convert JTS LineString to List<double[]> format: [latitude, longitude]
        List<double[]> coordsList = new ArrayList<>();
        if (road.getGeom() != null) {
            for (Coordinate coord : road.getGeom().getCoordinates()) {
                // Return as [latitude, longitude]
                coordsList.add(new double[]{coord.y, coord.x});
            }
        }
        dto.setCoordinates(coordsList);
        return dto;
    }

    // Helper to convert RequestDTO to Entity
    private Road convertToEntity(RoadRequestDTO dto) {
        Road road = new Road();
        road.setRoadType(dto.getRoadType());
        road.setContractorName(dto.getContractorName());
        road.setSanctionedAmount(dto.getSanctionedAmount());
        road.setLastRelayedDate(dto.getLastRelayedDate());
        road.setExecutiveEngineer(dto.getExecutiveEngineer());

        // Convert List<double[]> format to JTS LineString
        if (dto.getCoordinates() != null && dto.getCoordinates().size() > 1) {
            Coordinate[] jtsCoords = new Coordinate[dto.getCoordinates().size()];
            for (int i = 0; i < dto.getCoordinates().size(); i++) {
                double[] coord = dto.getCoordinates().get(i);
                // Assume input is [latitude, longitude], JTS Coordinate takes (x, y) = (longitude, latitude)
                jtsCoords[i] = new Coordinate(coord[1], coord[0]);
            }
            LineString lineString = geometryFactory.createLineString(jtsCoords);
            road.setGeom(lineString);
        } else {
            throw new IllegalArgumentException("A valid road must have at least 2 coordinate points.");
        }

        return road;
    }
}
