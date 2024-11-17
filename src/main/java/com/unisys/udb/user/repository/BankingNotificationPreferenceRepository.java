package com.unisys.udb.user.repository;

import com.unisys.udb.user.entity.DigitalNotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankingNotificationPreferenceRepository extends JpaRepository<DigitalNotificationPreference, Integer> {

    Optional<DigitalNotificationPreference> findByDigitalCustomerProfileId(UUID digitalBankingId);

    Boolean existsByDigitalCustomerProfileId(UUID digitalBankingId);
}

