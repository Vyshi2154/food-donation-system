package com.fooddonation.dto;

public class DonationRequestDTO {

    private Long donationId;
    private Long receiverId;
    private String message;

    public DonationRequestDTO() {
    }

    public DonationRequestDTO(Long donationId, Long receiverId, String message) {
        this.donationId = donationId;
        this.receiverId = receiverId;
        this.message = message;
    }

    public Long getDonationId() {
        return donationId;
    }

    public void setDonationId(Long donationId) {
        this.donationId = donationId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
