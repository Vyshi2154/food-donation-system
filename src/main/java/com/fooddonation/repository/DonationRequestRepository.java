package com.fooddonation.repository;

import com.fooddonation.entity.DonationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DonationRequestRepository
        extends JpaRepository<DonationRequest, Long> {

    List<DonationRequest> findByReceiverId(Long receiverId);

    List<DonationRequest> findByDonationId(Long donationId);

    boolean existsByDonationIdAndReceiverId(
            Long donationId, Long receiverId);

    @Modifying
    @Query("DELETE FROM DonationRequest r WHERE r.donation.id = :donationId")
    void deleteByDonationId(@Param("donationId") Long donationId);

    @Modifying
    @Query("DELETE FROM DonationRequest r WHERE r.receiver.id = :receiverId")
    void deleteByReceiverId(@Param("receiverId") Long receiverId);
}
