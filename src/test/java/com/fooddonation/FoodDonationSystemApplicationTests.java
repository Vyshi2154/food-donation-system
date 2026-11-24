package com.fooddonation;

import com.fooddonation.dto.DonationRequestDTO;
import com.fooddonation.entity.*;
import com.fooddonation.exception.ResourceNotFoundException;
import com.fooddonation.repository.DonationRepository;
import com.fooddonation.repository.DonationRequestRepository;
import com.fooddonation.repository.UserRepository;
import com.fooddonation.service.DonationRequestService;
import com.fooddonation.service.DonationService;
import com.fooddonation.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FoodDonationSystemApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private DonationService donationService;

    @Autowired
    private DonationRequestService donationRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private DonationRequestRepository requestRepository;

    @BeforeEach
    void cleanDatabase() {
        requestRepository.deleteAll();
        donationRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testUserRegistrationAndRoleCheck() {
        User donor = new User("John Donor", "donor@example.com", "secretPass123", Role.DONOR);
        User registered = userService.registerUser(donor);

        assertNotNull(registered.getId());
        assertEquals("donor@example.com", registered.getEmail());

        // Duplicate email must throw IllegalArgumentException
        User duplicate = new User("John Copy", "donor@example.com", "anotherPass", Role.DONOR);
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(duplicate));

        List<User> donors = userService.getUsersByRole(Role.DONOR);
        assertEquals(1, donors.size());
    }

    @Test
    void testDonationLifecycleAndRoleValidation() {
        User donor = userService.registerUser(new User("Alice", "alice@example.com", "pass123", Role.DONOR));
        User ngo = userService.registerUser(new User("Helping Hands", "ngo@example.com", "pass123", Role.NGO));

        Donation donation = new Donation();
        donation.setFoodName("Vegetable Curry & Rice");
        donation.setQuantity("30 meals");
        donation.setPickupLocation("Community Hall, City Center");
        donation.setSafeUntil(LocalDateTime.now().plusHours(5));

        // Non-donor user attempting to create donation should be rejected
        assertThrows(IllegalArgumentException.class, () ->
                donationService.createDonation(donation, ngo.getId()));

        // Valid donor creates donation
        Donation created = donationService.createDonation(donation, donor.getId());
        assertNotNull(created.getId());
        assertEquals(DonationStatus.AVAILABLE, created.getStatus());

        List<Donation> availableList = donationService.getAvailableDonations();
        assertEquals(1, availableList.size());
    }

    @Test
    void testExpiredDonationHandling() {
        User donor = userService.registerUser(new User("Bob", "bob@example.com", "pass123", Role.DONOR));

        Donation donation = new Donation();
        donation.setFoodName("Sandwiches");
        donation.setQuantity("15 boxes");
        donation.setPickupLocation("Baker Street");
        donation.setSafeUntil(LocalDateTime.now().minusMinutes(10)); // Expired

        // Should be rejected when attempting to create with a past deadline
        assertThrows(IllegalArgumentException.class, () ->
                donationService.createDonation(donation, donor.getId()));
    }

    @Test
    void testDonationRequestWorkflowAndCascadeDeletion() {
        User donor = userService.registerUser(new User("Sam", "sam@example.com", "pass123", Role.DONOR));
        User ngo = userService.registerUser(new User("Care Shelter", "care@example.com", "pass123", Role.NGO));

        Donation donation = new Donation();
        donation.setFoodName("Fresh Apples");
        donation.setQuantity("20 kg");
        donation.setPickupLocation("Orchard Park");
        donation.setSafeUntil(LocalDateTime.now().plusDays(2));
        Donation savedDonation = donationService.createDonation(donation, donor.getId());

        // NGO creates a request
        DonationRequest request = donationRequestService.createRequest(
                savedDonation.getId(), ngo.getId(), "For local homeless shelter");
        assertNotNull(request.getId());
        assertEquals(RequestStatus.PENDING, request.getStatus());

        // Donation status is now REQUESTED
        Donation refreshedDonation = donationService.getDonationById(savedDonation.getId());
        assertEquals(DonationStatus.REQUESTED, refreshedDonation.getStatus());

        // Donor approves request
        DonationRequest approved = donationRequestService.updateRequestStatus(request.getId(), RequestStatus.APPROVED);
        assertEquals(RequestStatus.APPROVED, approved.getStatus());

        Donation approvedDonation = donationService.getDonationById(savedDonation.getId());
        assertEquals(DonationStatus.APPROVED, approvedDonation.getStatus());

        // Delete donation: should cascade and remove related requests without foreign key errors
        assertDoesNotThrow(() -> donationService.deleteDonation(savedDonation.getId()));

        assertThrows(ResourceNotFoundException.class, () -> donationService.getDonationById(savedDonation.getId()));
        List<DonationRequest> remainingRequests = donationRequestService.getRequestsForDonation(savedDonation.getId());
        assertTrue(remainingRequests.isEmpty());
    }
}
