package com.saferoute.services;

import com.saferoute.dto.ComplaintRequestDTO;
import com.saferoute.dto.ComplaintResponseDTO;
import com.saferoute.exceptions.ResourceNotFoundException;
import com.saferoute.models.Complaint;
import com.saferoute.models.ComplaintStatus;
import com.saferoute.models.Road;
import com.saferoute.repositories.ComplaintRepository;
import com.saferoute.repositories.RoadRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final RoadRepository roadRepository;
    private final ImageStorageService imageStorageService;
    private final GeometryFactory geometryFactory;

    @Autowired
    public ComplaintServiceImpl(ComplaintRepository complaintRepository, RoadRepository roadRepository, ImageStorageService imageStorageService) {
        this.complaintRepository = complaintRepository;
        this.roadRepository = roadRepository;
        this.imageStorageService = imageStorageService;
        this.geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    }

    @Override
    @Transactional
    public ComplaintResponseDTO createComplaint(MultipartFile image, ComplaintRequestDTO requestDTO) {
        // 1. Upload image to Cloudinary and get URL
        String imageUrl = imageStorageService.uploadImage(image);

        // 2. Lookup Road
        Road road = null;
        if (requestDTO.getRoadId() != null) {
            road = roadRepository.findById(requestDTO.getRoadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Road not found with ID: " + requestDTO.getRoadId()));
        }

        // 3. Create Point geometry (JTS Coordinate takes x=longitude, y=latitude)
        Point point = geometryFactory.createPoint(new Coordinate(requestDTO.getLongitude(), requestDTO.getLatitude()));

        // 4. Build and save Complaint
        Complaint complaint = new Complaint();
        complaint.setStatus(ComplaintStatus.REPORTED); // Initial state is always REPORTED
        complaint.setSeverity(requestDTO.getSeverity());
        complaint.setDamageType(requestDTO.getDamageType());
        complaint.setImageUrl(imageUrl);
        complaint.setGeom(point);
        complaint.setRoad(road);

        Complaint savedComplaint = complaintRepository.save(complaint);
        return convertToResponseDTO(savedComplaint);
    }

    @Override
    @Transactional
    public ComplaintResponseDTO updateComplaintStatus(UUID id, ComplaintStatus status) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with ID: " + id));
        complaint.setStatus(status);
        Complaint updatedComplaint = complaintRepository.save(complaint);
        return convertToResponseDTO(updatedComplaint);
    }

    @Override
    public List<ComplaintResponseDTO> getActiveComplaintsNearLocation(double longitude, double latitude, double radiusMeters) {
        List<Complaint> complaints = complaintRepository.findActiveComplaintsNearLocation(longitude, latitude, radiusMeters);
        return complaints.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Helper to convert Entity to ResponseDTO
    private ComplaintResponseDTO convertToResponseDTO(Complaint complaint) {
        ComplaintResponseDTO dto = new ComplaintResponseDTO();
        dto.setId(complaint.getId());
        dto.setStatus(complaint.getStatus());
        dto.setSeverity(complaint.getSeverity());
        dto.setDamageType(complaint.getDamageType());
        dto.setImageUrl(complaint.getImageUrl());
        
        // Return [longitude, latitude] array
        if (complaint.getGeom() != null) {
            dto.setCoordinates(Arrays.asList(complaint.getGeom().getX(), complaint.getGeom().getY()));
        }
        
        if (complaint.getRoad() != null) {
            dto.setRoadId(complaint.getRoad().getId());
        }
        
        dto.setCreatedAt(complaint.getCreatedAt());
        return dto;
    }
}
