package com.fooddonation.repository;

import com.fooddonation.entity.Donation;
import com.fooddonation.entity.DonationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {

    List<Donation> findByDonorId(Long donorId);

    List<Donation> findByStatus(DonationStatus status);

    @Modifying
    @Query("DELETE FROM Donation d WHERE d.donor.id = :donorId")
    void deleteByDonorId(@Param("donorId") Long donorId);
}
