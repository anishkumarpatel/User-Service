package com.unisys.udb.user.repository;

import com.unisys.udb.user.constants.QueryConstants;
import com.unisys.udb.user.entity.DigitalCustomerProfile;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.unisys.udb.user.constants.QueryConstants.FETCH_DIGITAL_DEVICE_UDID;

@Repository
public interface DigitalCustomerProfileRepository extends JpaRepository<DigitalCustomerProfile, UUID> {

    Boolean existsByDigitalCustomerProfileId(UUID digitalBankingId);

    Optional<DigitalCustomerProfile> findById(UUID digitalBankingId);

    Boolean existsByCoreCustomerProfileId(UUID coreCustomerProfileId);

    Boolean existsByDigitalUserName(String userName);

    @Modifying
    @Transactional
    @Query(value = QueryConstants.UPDATE_DIGITAL_CUSTOMER_STATUS_TYPE_REF_ID, nativeQuery = true)
    void updateDigitalCustomerStatus(@Param("digitalCustomerProfileId")UUID digitalCustomerProfileId,
                                     @Param("statusCode") Integer statusCode, @Param("reason")String reason,
                                     @Param("modifiedBy") String modifiedBy,
                                     @Param("modifiedTime") LocalDateTime modifiedTime);

    @Query(value = QueryConstants.FIND_PROFILE_ID_BY_USERNAME, nativeQuery = true)
    UUID findDigitalCustomerProfileIdByUserName(String digitalUserName);

    @Query(value = QueryConstants.FIND_PIN_SET_COMPLETED_BY_PROFILE_ID, nativeQuery = true)
    Boolean findPinSetCompletedByProfileId(UUID digitalCustomerProfileId);

    @Query(value = FETCH_DIGITAL_DEVICE_UDID, nativeQuery = true)
    List<String> findDigitalCustomerDeviceIdList(UUID digitalCustomerProfileId);
    Optional<DigitalCustomerProfile> findByCoreCustomerProfileId(UUID coreCustomerProfileId);

    @Modifying
    @Transactional
    @Query(value = QueryConstants.LOCK_USER_ACCOUNT, nativeQuery = true)
    int lockUserAccount(@Param("digitalCustomerProfileId") UUID digitalCustomerProfileId);

    @Query(value = QueryConstants.EXISTS_BY_PROFILE_ID_AND_STATUS_TYPE, nativeQuery = true)
    Boolean existsByDigitalCustomerProfileIdAndCustomerStatusType(
            UUID digitalCustomerProfileId, String customerStatusType);

}
