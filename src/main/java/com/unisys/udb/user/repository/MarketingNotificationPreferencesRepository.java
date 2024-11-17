package com.unisys.udb.user.repository;

import com.unisys.udb.user.entity.DigitalMarketingNotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MarketingNotificationPreferencesRepository
        extends JpaRepository<DigitalMarketingNotificationPreference, Integer> {

    Optional<DigitalMarketingNotificationPreference> findByDigitalCustomerProfileId(UUID digitalBankingId);

    Boolean existsByDigitalCustomerProfileId(UUID digitalBankingId);
}
