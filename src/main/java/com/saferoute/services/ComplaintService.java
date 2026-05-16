package com.saferoute.services;

import com.saferoute.dto.ComplaintRequestDTO;
import com.saferoute.dto.ComplaintResponseDTO;
import com.saferoute.models.ComplaintStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ComplaintService {
    
    ComplaintResponseDTO createComplaint(MultipartFile image, ComplaintRequestDTO requestDTO);

    ComplaintResponseDTO updateComplaintStatus(UUID id, ComplaintStatus status);

    List<ComplaintResponseDTO> getActiveComplaintsNearLocation(double longitude, double latitude, double radiusMeters);
}
