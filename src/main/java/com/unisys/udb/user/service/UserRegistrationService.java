package com.unisys.udb.user.service;

import com.unisys.udb.user.dto.request.BankingNotificationPreferenceRequest;
import com.unisys.udb.user.dto.request.DigitalCustomerProfileDTO;
import com.unisys.udb.user.dto.request.MarketingNotificationPreferenceRequest;
import com.unisys.udb.user.dto.request.PublicKeyUpdateRequest;
import com.unisys.udb.user.dto.request.UserDetailDto;
import com.unisys.udb.user.dto.request.UserPublicKeyRequest;
import com.unisys.udb.user.dto.response.BankingNotificationPreferenceResponse;
import com.unisys.udb.user.dto.response.BiometricPublicKeyResponse;
import com.unisys.udb.user.dto.response.CheckMfaStatusResponse;
import com.unisys.udb.user.dto.response.CheckPinStatusResponse;
import com.unisys.udb.user.dto.response.CoreCustomerProfileResponse;
import com.unisys.udb.user.dto.response.DigitalCookiePreferenceResponse;
import com.unisys.udb.user.dto.response.MarketingNotificationPreferenceResponse;
import com.unisys.udb.user.dto.response.MarketingPreferenceResponse;
import com.unisys.udb.user.dto.response.NotificationPreferenceResponse;
import com.unisys.udb.user.dto.response.UpdatePinStatusResponse;
import com.unisys.udb.user.dto.response.UserAPIBaseResponse;
import com.unisys.udb.user.dto.response.UserInfoResponse;
import com.unisys.udb.user.dto.response.UserNameResponse;
import com.unisys.udb.utility.linkmessages.dto.DynamicMessageResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface UserRegistrationService {

    Mono<UserInfoResponse> getUserInfo(UUID digitalCustomerProfileId, String digitalDeviceUdid);

    Mono<List<MarketingPreferenceResponse>> updateBankingNotificationPreference(
            UUID digitalCustomerProfileId,
            BankingNotificationPreferenceRequest request);

    Mono<DynamicMessageResponse> updateMarketingNotificationPreference(
            UUID digitalCustomerProfileId,
            MarketingNotificationPreferenceRequest request);

    Mono<BankingNotificationPreferenceResponse> getBankingPreference(UUID digitalCustomerProfileId);

    Mono<MarketingNotificationPreferenceResponse> getMarketingPreference(UUID digitalCustomerProfileId);

    Mono<DynamicMessageResponse> saveUserAndDeviceInfo(DigitalCustomerProfileDTO digitalCustomerProfileDTO);

    Mono<CoreCustomerProfileResponse> fetchUserInfo(UUID digitalCustomerProfileId);


    Mono<UserNameResponse> getUserNameInfoByCustomerDeviceId(Integer digitalCustomerDeviceId);

    Mono<DigitalCookiePreferenceResponse> getDigitalCookiePreference(UUID digitalCustomerProfileId);


    Mono<CheckPinStatusResponse> checkPinExistsBasedOnDigitalDeviceId(String digitalDeviceUdid);

    UserAPIBaseResponse saveUserPublicKeyForBioMetric(UUID digitalCustomerProfileId,
                                                      UserPublicKeyRequest userPublicKeyRequest);

    BiometricPublicKeyResponse getUserPublicKey(String digitalDeviceUdid, String biometricType);
    UserDetailDto buildUserDetailsDTO(UserInfoResponse userInfoResponse);

    void saveUserPublicKeyForPin(PublicKeyUpdateRequest publicKeyRequest);

    UserAPIBaseResponse saveDeviceInfo(DigitalCustomerProfileDTO digitalCustomerProfileDTO);

    Mono<CheckMfaStatusResponse> checkMfaStatusBasedOnDigitalDeviceId(String digitalDeviceUdid);

    String getUserPublicKeyForPin(String payloadDeviceId, String username);

    Mono<UpdatePinStatusResponse> updatePinStatus(UUID digitalCustomerProfileId, boolean pinCompletedStatus);

    Boolean checkUserPinStatus(UUID digitalCustomerProfileId);

    List<MarketingPreferenceResponse> getMarketingPreferences(UUID digitalCustomerProfileId);

    List<NotificationPreferenceResponse> getNotificationPreferences(UUID digitalCustomerProfileId);
}
