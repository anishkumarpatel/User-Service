package com.unisys.udb.user.repository;

import com.unisys.udb.user.constants.QueryConstants;
import com.unisys.udb.user.entity.DigitalCustomerActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface DigitalCustomerActivityRepository extends
        JpaRepository<DigitalCustomerActivityEntity, Integer> {

    @Query(value = QueryConstants.GET_CUSTOMER_LAST_LOGIN_ACTIVITY_TIME, nativeQuery = true)
    LocalDateTime getCustomerLastLoginActivityTime(UUID digitalCustomerProfileId);

    DigitalCustomerActivityEntity findTopByDigitalCustomerDeviceIdOrderByActivityTimeDesc(
            int deviceId);

    @Query(value = QueryConstants.FIND_CUSTOMER_RECENT_ACTIVITY_STATUS_BY_ACTIVITY_NAME, nativeQuery = true)
    String findCustomerRecentActivityStatusByActivityName(
            UUID digitalCustomerProfileId, String activityName, int reAuthDuration);

}
