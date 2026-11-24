package com.fooddonation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "donation_requests",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"donation_id", "receiver_id"}
        )
)
public class DonationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "donation_id", nullable = false)
    private Donation donation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    private LocalDateTime createdAt;

    public DonationRequest() {
    }

    public DonationRequest(Donation donation, User receiver, String message) {
        this.donation = donation;
        this.receiver = receiver;
        this.message = message;
        this.status = RequestStatus.PENDING;
    }

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = RequestStatus.PENDING;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Donation getDonation() { return donation; }
    public void setDonation(Donation donation) { this.donation = donation; }

    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
