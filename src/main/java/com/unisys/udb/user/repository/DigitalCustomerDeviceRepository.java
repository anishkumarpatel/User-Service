package com.unisys.udb.user.repository;

import com.unisys.udb.user.constants.QueryConstants;
import com.unisys.udb.user.entity.DigitalCustomerDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.unisys.udb.user.constants.QueryConstants.*;

@Repository
public interface DigitalCustomerDeviceRepository extends JpaRepository<DigitalCustomerDevice, Integer> {

    DigitalCustomerDevice findByDigitalCustomerDeviceId(Integer deviceId);

    Optional<DigitalCustomerDevice> findByDigitalDeviceUdid(String digitalDeviceUdid);

    @Query(value = QueryConstants.GET_CUSTOMER_DEVICE_DETAILS, nativeQuery = true)
    List<DigitalCustomerStatus> findByProfileIdAndStatus(@Param("profileId") UUID profileId);

    @Query(value = QueryConstants.GET_CUSTOMER_DEVICE_DETAIL, nativeQuery = true)
    Optional<DigitalCustomerDevice> findByProfileIdAndStatusSingle(@Param("profileId") UUID profileId);

    @Query(value = QueryConstants.GET_FACE_AUTH_PUBLIC_KEY, nativeQuery = true)
    String findByUserFaceAuthPublicKey(@Param("digitalDeviceUdid") String digitalDeviceUdid);
    @Query(value = QueryConstants.GET_TOUCH_AUTH_PUBLIC_KEY, nativeQuery = true)
    String findByUserTouchAuthPublicKey(@Param("digitalDeviceUdid") String digitalDeviceUdid);

    @Query(value = QueryConstants.FIND_DIGITAL_CUSTOMER_PROFILE_ID, nativeQuery = true)
    UUID findDigitalCustomerProfileId(@Param("digitaldeviceudid") String digitalDeviceUdid);

    @Query(value = QueryConstants.GET_MARKETING_REF_ID, nativeQuery = true)
    List<Object[]> findByPromotionOffers(@Param("digitalCustomerProfileId") UUID digitalCustomerProfileId);

    @Query(value = QueryConstants.FIND_BY_PROFILE_ID_AND_UDID, nativeQuery = true)
    DigitalCustomerDevice findByDigitalCustomerProfileIdAndDigitalDeviceUdid(
            UUID digitalCustomerProfileId, String digitalDeviceUdid);

    @Query(value = QueryConstants.GET_PUBLIC_KEY_FOR_PIN, nativeQuery = true)
    String findByUserPublicKeyForPin(@Param("payloadDeviceId") String payloadDeviceId,
                                     @Param("username") String username);

    @Query(value = QueryConstants.RETRIEVE_COOKIE_PREFERENCE_BY_DEVICE_UDID, nativeQuery = true)
    List<Object[]> retrieveCookiePreferenceByDeviceUdid(@Param("digitalDeviceUdid") String digitalDeviceUdid);

    @Query(value = QueryConstants.GET_REGISTERED_DEVICES, nativeQuery = true)
    List<Object[]> findRegisteredDevicesByDigitalCustomerProfileIdAndDeviceStatus(
            @Param("digitalCustomerProfileId") UUID digitalCustomerProfileId);

    @Query(value = QueryConstants.GET_DEREGISTERED_DEVICES, nativeQuery = true)
    List<Object[]> findDeRegisteredDevicesByDigitalCustomerProfileIdAndDeviceStatus(
            @Param("digitalCustomerProfileId") UUID digitalCustomerProfileId);


    @Query (value = FETCH_FROM_DIGITAL_CUSTOMER_DEVICE, nativeQuery = true)
    List<DigitalCustomerDevice> getListOfDevicesByDevicesIds(
            @Param("digitalDeviceIdsList") List<String> devicesIdsList);

    @Query(value = FETCH_DIGITAL_CUSTOMER_DEVICE_ID, nativeQuery = true)
    List<Integer> getListOfDeviceCustomerIds(@Param("digitalDeviceIdsList") List<String> deviceIdsList);
    @Query(value = UPDATE_BIOMETRIC_PUBLICKEY_NULL, nativeQuery = true)
    Integer updateBiometricPublicKeyNull(@Param("digitalDeviceIdsList") List<Integer> customerDeviceIds);

    @Query(value = GET_DIGITAL_CUSTOMER_DEVICE_ID, nativeQuery = true)
    List<Integer> findDigitalCustomerDeviceId(
            @Param("digitalCustomerProfileId") UUID digitalCustomerProfileId);

    @Query(value = GET_DIGITAL_CUSTOMER_DEVICE, nativeQuery = true)
    Optional<DigitalCustomerDevice> getDigitalCustomerDeviceByDeviceUUIDAndProfileID(
            @Param("digitalCustomerProfileID") String customerProfileID,
            @Param("digitalDeviceUUID") String customerDeviceUUID);

    @Query(value = GET_BIOMETRIC_STATUS_FOR_DEVICE, nativeQuery = true)
    Object[] getBiometricStatusForDevice(
            @Param("digitalCustomerProfileId") UUID digitalCustomerProfileId,
            @Param("digitalDeviceUDID") String digitalDeviceUDID);

    @Query(value = QueryConstants.GET_CUSTOMER_DEVICE_ID, nativeQuery = true)
    Integer findDeviceLinkByCustomerProfileIdAndDeviceUUID(@Param("digitalCustomerProfileID")
                                                           UUID digitalCustomerProfileID,
                                                           @Param("digitalDeviceUUID")
                                                           String digitalDeviceUUID);

    @Query(value = DEVICE_LINK_REGISTERED_FLAG_QUERY, nativeQuery = true)
    Boolean isDeviceRegisteredForDigitalCustomerProfile(
            @Param("digitalCustomerProfileID") UUID digitalCustomerProfileID,
            @Param("digitalDeviceUUID") String digitalDeviceUUID);


    @Query(value = GET_CUSTOMER_EMAIL_AND_MOBILE_NUMBER, nativeQuery = true)
    CustomerContactInfo findEmailAndMobileNumberByDigitalCustomerProfileId(@Param("digitalCustomerProfileID")
                                              UUID digitalCustomerProfileID);
}