package com.fooddonation.controller;

import com.fooddonation.dto.DonationRequestDTO;
import com.fooddonation.entity.DonationRequest;
import com.fooddonation.entity.RequestStatus;
import com.fooddonation.service.DonationRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin(origins = "*")
public class DonationRequestController {

    private final DonationRequestService requestService;

    public DonationRequestController(DonationRequestService requestService) {
        this.requestService = requestService;
    }

    // NGO requests food - supports both JSON RequestBody and RequestParam
    @PostMapping
    public ResponseEntity<DonationRequest> createRequest(
            @RequestBody(required = false) DonationRequestDTO dto,
            @RequestParam(required = false) Long donationId,
            @RequestParam(required = false) Long receiverId,
            @RequestParam(required = false) String message) {

        Long finalDonationId = (dto != null && dto.getDonationId() != null) ? dto.getDonationId() : donationId;
        Long finalReceiverId = (dto != null && dto.getReceiverId() != null) ? dto.getReceiverId() : receiverId;
        String finalMessage = (dto != null && dto.getMessage() != null) ? dto.getMessage() : message;

        DonationRequest created = requestService.createRequest(finalDonationId, finalReceiverId, finalMessage);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Get request by ID
    @GetMapping("/{requestId}")
    public ResponseEntity<DonationRequest> getRequestById(@PathVariable Long requestId) {
        return ResponseEntity.ok(requestService.getRequestById(requestId));
    }

    // View requests made by an NGO
    @GetMapping("/ngo/{receiverId}")
    public ResponseEntity<List<DonationRequest>> getNGORequests(@PathVariable Long receiverId) {
        return ResponseEntity.ok(requestService.getRequestsByNGO(receiverId));
    }

    // View requests for a donation
    @GetMapping("/donation/{donationId}")
    public ResponseEntity<List<DonationRequest>> getDonationRequests(@PathVariable Long donationId) {
        return ResponseEntity.ok(requestService.getRequestsForDonation(donationId));
    }

    // Approve or reject a request
    @PatchMapping("/{requestId}/status")
    public ResponseEntity<DonationRequest> updateRequestStatus(
            @PathVariable Long requestId,
            @RequestParam RequestStatus status) {
        return ResponseEntity.ok(requestService.updateRequestStatus(requestId, status));
    }
}
