package com.fooddonation.service;

import com.fooddonation.entity.*;
import com.fooddonation.exception.ResourceNotFoundException;
import com.fooddonation.repository.DonationRepository;
import com.fooddonation.repository.DonationRequestRepository;
import com.fooddonation.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DonationRequestService {

    private final DonationRequestRepository requestRepository;
    private final DonationRepository donationRepository;
    private final UserRepository userRepository;

    public DonationRequestService(
            DonationRequestRepository requestRepository,
            DonationRepository donationRepository,
            UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.donationRepository = donationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public DonationRequest createRequest(
            Long donationId, Long receiverId, String message) {

        if (donationId == null) {
            throw new IllegalArgumentException("Donation ID is required");
        }
        if (receiverId == null) {
            throw new IllegalArgumentException("Receiver (NGO) ID is required");
        }

        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new ResourceNotFoundException("Donation not found with id: " + donationId));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("NGO user not found with id: " + receiverId));

        if (receiver.getRole() != Role.NGO) {
            throw new IllegalArgumentException("Only users with role NGO can request food");
        }

        // Check food expiration
        if (donation.getSafeUntil() != null && donation.getSafeUntil().isBefore(LocalDateTime.now())) {
            donation.setStatus(DonationStatus.EXPIRED);
            donationRepository.save(donation);
            throw new IllegalArgumentException("Donation has expired and is no longer available");
        }

        if (donation.getStatus() != DonationStatus.AVAILABLE) {
            throw new IllegalArgumentException("Donation is not available (Current status: " + donation.getStatus() + ")");
        }

        if (requestRepository.existsByDonationIdAndReceiverId(donationId, receiverId)) {
            throw new IllegalArgumentException("A request for this donation from this NGO already exists");
        }

        DonationRequest request = new DonationRequest();
        request.setDonation(donation);
        request.setReceiver(receiver);
        request.setMessage(message);
        request.setStatus(RequestStatus.PENDING);

        donation.setStatus(DonationStatus.REQUESTED);
        donationRepository.save(donation);

        return requestRepository.save(request);
    }

    public List<DonationRequest> getRequestsByNGO(Long receiverId) {
        return requestRepository.findByReceiverId(receiverId);
    }

    public List<DonationRequest> getRequestsForDonation(Long donationId) {
        return requestRepository.findByDonationId(donationId);
    }

    public DonationRequest getRequestById(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));
    }

    @Transactional
    public DonationRequest updateRequestStatus(Long requestId, RequestStatus status) {
        DonationRequest request = getRequestById(requestId);

        if (status == RequestStatus.PENDING) {
            throw new IllegalArgumentException("Cannot change status to PENDING");
        }

        if (request.getStatus() == RequestStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot change status of an already completed request");
        }

        request.setStatus(status);

        Donation donation = request.getDonation();

        if (status == RequestStatus.APPROVED) {
            donation.setStatus(DonationStatus.APPROVED);
        } else if (status == RequestStatus.REJECTED) {
            donation.setStatus(DonationStatus.AVAILABLE);
        } else if (status == RequestStatus.COMPLETED) {
            donation.setStatus(DonationStatus.COMPLETED);
        }

        donationRepository.save(donation);
        return requestRepository.save(request);
    }
}
