package com.unisys.udb.user.repository;

import com.unisys.udb.user.entity.DigitalCustomerShortcuts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DigitalCustomerShortcutsRepository extends JpaRepository<DigitalCustomerShortcuts, Long> {

    Optional<DigitalCustomerShortcuts> findByDigitalCustomerProfileId(UUID digitalCustomerProfileId);

}
