package com.fooddonation.controller;

import com.fooddonation.entity.Donation;
import com.fooddonation.entity.DonationStatus;
import com.fooddonation.service.DonationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donations")
@CrossOrigin(origins = "*")
public class DonationController {

    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    // Add a food donation specifying donor ID in path
    @PostMapping("/{donorId}")
    public ResponseEntity<Donation> createDonationWithDonorId(
            @PathVariable Long donorId,
            @Valid @RequestBody Donation donation) {
        Donation created = donationService.createDonation(donation, donorId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Add a food donation with donor ID in body or query param
    @PostMapping
    public ResponseEntity<Donation> createDonation(
            @RequestParam(required = false) Long donorId,
            @Valid @RequestBody Donation donation) {
        Donation created = donationService.createDonation(donation, donorId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Display all donations
    @GetMapping
    public ResponseEntity<List<Donation>> getAllDonations() {
        return ResponseEntity.ok(donationService.getAllDonations());
    }

    // Display available food
    @GetMapping("/available")
    public ResponseEntity<List<Donation>> getAvailableDonations() {
        return ResponseEntity.ok(donationService.getAvailableDonations());
    }

    // Display donations by donor
    @GetMapping("/donor/{donorId}")
    public ResponseEntity<List<Donation>> getDonationsByDonor(@PathVariable Long donorId) {
        return ResponseEntity.ok(donationService.getDonationsByDonor(donorId));
    }

    // Get donation by ID
    @GetMapping("/{id}")
    public ResponseEntity<Donation> getDonationById(@PathVariable Long id) {
        return ResponseEntity.ok(donationService.getDonationById(id));
    }

    // Update donation status
    @PatchMapping("/{id}/status")
    public ResponseEntity<Donation> updateStatus(
            @PathVariable Long id,
            @RequestParam DonationStatus status) {
        return ResponseEntity.ok(donationService.updateStatus(id, status));
    }

    // Delete a donation
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDonation(@PathVariable Long id) {
        donationService.deleteDonation(id);
        return ResponseEntity.noContent().build();
    }
}
