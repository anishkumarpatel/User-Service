package com.unisys.udb.user.repository;

import com.unisys.udb.user.constants.QueryConstants;
import com.unisys.udb.user.entity.DigitalCustomerPin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PinRepository extends JpaRepository<DigitalCustomerPin, Long> {
    @Query(value = QueryConstants.GET_CUSTOMER_PIN_HISTORY, nativeQuery = true)
    List<DigitalCustomerPin> getPinHistory(UUID digitalCustomerProfileId);

}