package com.fooddonation.service;

import com.fooddonation.entity.Donation;
import com.fooddonation.entity.DonationStatus;
import com.fooddonation.entity.Role;
import com.fooddonation.entity.User;
import com.fooddonation.exception.ResourceNotFoundException;
import com.fooddonation.repository.DonationRepository;
import com.fooddonation.repository.DonationRequestRepository;
import com.fooddonation.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DonationService {

    private final DonationRepository donationRepository;
    private final DonationRequestRepository requestRepository;
    private final UserRepository userRepository;

    public DonationService(
            DonationRepository donationRepository,
            DonationRequestRepository requestRepository,
            UserRepository userRepository) {
        this.donationRepository = donationRepository;
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Donation createDonation(Donation donation, Long donorId) {
        if (donorId == null && donation.getDonor() != null) {
            donorId = donation.getDonor().getId();
        }

        if (donorId == null) {
            throw new IllegalArgumentException("Donor ID must be provided");
        }

        final Long resolvedDonorId = donorId;
        User donor = userRepository.findById(resolvedDonorId)
                .orElseThrow(() -> new ResourceNotFoundException("Donor not found with id: " + resolvedDonorId));

        if (donor.getRole() != Role.DONOR) {
            throw new IllegalArgumentException("Only users with role DONOR can create donations");
        }

        if (donation.getSafeUntil() == null || !donation.getSafeUntil().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Food safety deadline must be in the future");
        }

        donation.setDonor(donor);
        donation.setStatus(DonationStatus.AVAILABLE);

        return donationRepository.save(donation);
    }

    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }

    @Transactional
    public List<Donation> getAvailableDonations() {
        List<Donation> available = donationRepository.findByStatus(DonationStatus.AVAILABLE);
        LocalDateTime now = LocalDateTime.now();
        List<Donation> result = new ArrayList<>();

        for (Donation donation : available) {
            if (donation.getSafeUntil() != null && donation.getSafeUntil().isBefore(now)) {
                donation.setStatus(DonationStatus.EXPIRED);
                donationRepository.save(donation);
            } else {
                result.add(donation);
            }
        }
        return result;
    }

    public List<Donation> getDonationsByDonor(Long donorId) {
        return donationRepository.findByDonorId(donorId);
    }

    public Donation getDonationById(Long id) {
        return donationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donation not found with id: " + id));
    }

    @Transactional
    public Donation updateStatus(Long id, DonationStatus status) {
        Donation donation = getDonationById(id);
        donation.setStatus(status);
        return donationRepository.save(donation);
    }

    @Transactional
    public void deleteDonation(Long id) {
        Donation donation = getDonationById(id);
        // Clean up any referencing requests to prevent foreign key constraint violations
        requestRepository.deleteByDonationId(id);
        donationRepository.delete(donation);
    }
}
