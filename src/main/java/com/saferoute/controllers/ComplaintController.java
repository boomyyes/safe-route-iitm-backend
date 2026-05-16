package com.saferoute.controllers;

import com.saferoute.dto.ComplaintRequestDTO;
import com.saferoute.dto.ComplaintResponseDTO;
import com.saferoute.models.ComplaintStatus;
import com.saferoute.services.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    @Autowired
    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    /**
     * Create a new complaint.
     * Consumes MULTIPART_FORM_DATA to accept an image file and JSON data side-by-side.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ComplaintResponseDTO> createComplaint(
            @RequestPart("image") MultipartFile image,
            @RequestPart("data") ComplaintRequestDTO complaintData) {

        ComplaintResponseDTO response = complaintService.createComplaint(image, complaintData);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update the status of an existing complaint.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ComplaintResponseDTO> updateComplaintStatus(
            @PathVariable UUID id,
            @RequestBody String statusString) {
        
        // Strip out any surrounding quotes that might come from JSON
        String cleanStatus = statusString.replace("\"", "").trim().toUpperCase();
        ComplaintStatus status = ComplaintStatus.valueOf(cleanStatus);
        
        ComplaintResponseDTO response = complaintService.updateComplaintStatus(id, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Perform a spatial search to find active complaints near a given coordinate.
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<ComplaintResponseDTO>> getNearbyComplaints(
            @RequestParam("lat") double lat,
            @RequestParam("lng") double lng,
            @RequestParam(value = "radius", defaultValue = "5000") double radiusMeters) {

        List<ComplaintResponseDTO> activeComplaints = complaintService.getActiveComplaintsNearLocation(lng, lat, radiusMeters);
        return ResponseEntity.ok(activeComplaints);
    }
}
