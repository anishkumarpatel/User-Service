package com.unisys.udb.user.repository;

import com.unisys.udb.user.entity.DigitalCustomerStatusTypeRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DigitalCustomerStatusTypeRefRepository extends JpaRepository<DigitalCustomerStatusTypeRef, Integer> {

    Optional<DigitalCustomerStatusTypeRef> findByCustomerStatusTypeIgnoreCase(String customerStatusType);
}
