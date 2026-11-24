package com.fooddonation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "donations")
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "donor_id", nullable = false)
    private User donor;

    @NotBlank(message = "Food name is required")
    @Column(name = "food_name", nullable = false)
    private String foodName;

    private String description;

    @NotBlank(message = "Quantity is required")
    @Column(nullable = false)
    private String quantity;

    @NotBlank(message = "Pickup location is required")
    @Column(name = "pickup_location", nullable = false)
    private String pickupLocation;

    @NotNull(message = "Safe until date is required")
    @Column(name = "safe_until", nullable = false)
    private LocalDateTime safeUntil;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationStatus status = DonationStatus.AVAILABLE;

    private LocalDateTime createdAt;

    public Donation() {
    }

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = DonationStatus.AVAILABLE;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getDonor() { return donor; }
    public void setDonor(User donor) { this.donor = donor; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }

    public LocalDateTime getSafeUntil() { return safeUntil; }
    public void setSafeUntil(LocalDateTime safeUntil) { this.safeUntil = safeUntil; }

    public DonationStatus getStatus() { return status; }
    public void setStatus(DonationStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
