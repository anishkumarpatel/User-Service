package com.unisys.udb.user.repository;

import com.unisys.udb.user.constants.QueryConstants;
import com.unisys.udb.user.entity.DigitalCustomerAlert;
import com.unisys.udb.user.entity.DigitalDocdbAlertRef;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DigitalCustomerAlertRepository extends JpaRepository<DigitalCustomerAlert, Integer> {
    List<DigitalCustomerAlert> findByDigitalCustomerProfileIdAndAlertReadFlagOrderByAlertCreationDateDesc(
            UUID digitalCustomerProfileId, boolean alertReadFlag);

    @Query(value = QueryConstants.COUNT_UNREAD_ALERTS_BY_PROFILE_ID, nativeQuery = true)
    Integer countUnreadAlertsByProfileId(@Param("digitalCustomerProfileId") UUID digitalCustomerProfileId);

    Optional<DigitalCustomerAlert> findByDigitalCustomerAlertIdAndDigitalCustomerProfileId(
            Integer digitalCustomerAlertId, UUID digitalCustomerProfileId);

    Boolean existsByDigitalCustomerProfileIdAndDigitalDocdbAlertRefAndAlertReadFlag(
            UUID digitalCustomerProfileId, DigitalDocdbAlertRef digitalDocdbAlertRef, boolean alertReadFlag);

    @Query(value = QueryConstants.FIND_ALERT_IDS_BY_ALERT_KEY, nativeQuery = true)
    List<Integer> getDigitalCustomerAlertIdsByAlertKey(
            @Param("customerProfileId") UUID digitalCustomerProfileId,
            @Param("alertReadFlag") boolean alertReadFlag,
            @Param("digitalAlertKey") String digitalAlertKey);

    @Modifying
    @Transactional
    @Query(value = QueryConstants.UPDATE_ALERT_READ_FLAG_BY_IDS, nativeQuery = true)
    int updateAlertReadFlagByProfileIdAndAlertKey(
            @Param("customerProfileId") UUID digitalCustomerProfileId,
            @Param("alertReadFlag") boolean alertReadFlag,
            @Param("digitalAlertKey") String digitalAlertKey
    );
}
