package com.unisys.udb.user.repository;

import com.unisys.udb.user.constants.QueryConstants;
import com.unisys.udb.user.entity.DigitalCustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserInfoRepository extends JpaRepository<DigitalCustomerProfile, UUID> {

    Optional<DigitalCustomerProfile> findByDigitalCustomerProfileId(UUID digitalBankingId);

    DigitalCustomerProfile findByDigitalUserName(String userName);

    Boolean existsByDigitalCustomerProfileId(UUID digitalBankingId);


    @Query(value = QueryConstants.GET_CUSTOMER_TRANSACTIONS, nativeQuery = true)
    List<Object[]> fetchCustomerByDigitalCustomerProfileId(UUID digitalCustomerProfileId);

    @Query(value = QueryConstants.GET_USER_NAME_INFO, nativeQuery = true)
    List<Object[]> getUserNameInfoByCustomerDeviceId(@Param("digitalCustomerDeviceId")
                                                      Integer digitalCustomerDeviceId);

    @Query(value = QueryConstants.CHECK_PIN_EXIST_BASED_ON_DIGITAL_DEVICE_UDID, nativeQuery = true)
    Optional<Boolean> checkPinExistsBasedOnDigitalDeviceUdid(String digitalDeviceUdid);

    @Query(value = QueryConstants.GET_CUSTOMER_STATUS_TYPE, nativeQuery = true)
    Optional<Object> getCustomerStatusTypeByDigitalUserName(String digitalUserName);

    @Query(value = QueryConstants.GET_CUSTOMER_DETAILS_FOR_SUPPORT, nativeQuery = true)
    List<Object[]> findCustomerDetailsBySearchTerm(@Param("searchTerm") String searchTerm);

    @Query(value = QueryConstants.CHECK_MFA_STATUS_BASED_ON_DIGITAL_DEVICE_UDID, nativeQuery = true)
    Optional<Boolean> checkMfaStatusBasedOnDigitalDeviceUdid(String digitalDeviceUdid);

    @Query(value = QueryConstants.GET_USERNAME_BY_DIGITAL_CUSTOMER_PROFILE_ID, nativeQuery = true)
    String findUserNameByDigitalCustomerProfileId(UUID digitalCustomerProfileId);

    @Query(value = QueryConstants.GET_BROADCAST_REFERENCE_ID, nativeQuery = true)
    List<String> getBroadCastReferenceId(@Param("digitalCustomerProfile")UUID digitalCustomerProfileId);

}

