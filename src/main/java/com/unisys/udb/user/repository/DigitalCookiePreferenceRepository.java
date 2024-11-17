package com.unisys.udb.user.repository;

import com.unisys.udb.user.entity.DigitalCookiePreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DigitalCookiePreferenceRepository extends JpaRepository<DigitalCookiePreference, Integer> {

    Optional<DigitalCookiePreference> findByDigitalCustomerProfileId(UUID digitalBankingId);

    Boolean existsByDigitalCustomerProfileId(UUID digitalBankingId);

}
