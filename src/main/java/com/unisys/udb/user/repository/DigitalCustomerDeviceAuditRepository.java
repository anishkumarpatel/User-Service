package com.unisys.udb.user.repository;

import com.unisys.udb.user.entity.DigitalCustomerDeviceAudit;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import static com.unisys.udb.user.constants.QueryConstants.INSERT_INTO_DEVICE_LINK_AUDIT;

@Repository
public interface DigitalCustomerDeviceAuditRepository extends JpaRepository<DigitalCustomerDeviceAudit, Integer> {

    @Transactional
    @Modifying
    @Query(value = INSERT_INTO_DEVICE_LINK_AUDIT, nativeQuery = true)
    void insertData(@Param("entity") DigitalCustomerDeviceAudit entity);


}
