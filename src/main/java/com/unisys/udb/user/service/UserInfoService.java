package com.unisys.udb.user.service;

import com.unisys.udb.user.dto.request.BiometricStatusDTO;
import com.unisys.udb.user.dto.request.DeviceTokenRequest;
import com.unisys.udb.user.dto.request.DigitalPwdRequest;
import com.unisys.udb.user.dto.request.TermsConditionsAndCookiesRequest;
import com.unisys.udb.user.dto.request.UpdateExpiryDTO;
import com.unisys.udb.user.dto.request.UserDetailDto;
import com.unisys.udb.user.dto.response.CustomerDetailsResponse;
import com.unisys.udb.user.dto.response.DeRegisterDevicesResponse;
import com.unisys.udb.user.dto.response.DeviceDataForRegisterDevice;
import com.unisys.udb.user.dto.response.DeviceInfoResponse;
import com.unisys.udb.user.dto.response.DigitalCustomerPwdResponse;
import com.unisys.udb.user.dto.response.GetTermsConditionAndCookiesInfoResponse;
import com.unisys.udb.user.dto.response.TermsConditionsAndCookieResponse;
import com.unisys.udb.user.dto.response.UpdateExpiryResponse;
import com.unisys.udb.user.dto.response.UserAPIBaseResponse;
import com.unisys.udb.user.dto.response.UserInfoResponse;
import com.unisys.udb.user.dto.response.UserLockResponse;
import com.unisys.udb.user.dto.response.UserStatusResponse;
import com.unisys.udb.user.dto.response.UserSuccessResponse;
import com.unisys.udb.user.entity.CountryValidation;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface UserInfoService {

    UserInfoResponse getUserInfoResponse(String digitalDeviceUdid);


    DeviceInfoResponse getUserDeviceInfo(UUID digitalCustomerProfileId);

    void updateFailureAttemptDetailsByUsername(UserDetailDto userDetailDto);

    Mono<UserStatusResponse> getUserStatus(String digitalUserName);

    Mono<UserAPIBaseResponse> updateDeviceToken(DeviceTokenRequest request);

    Mono<TermsConditionsAndCookieResponse> updateTermsConditionsAndCookies(String deviceId,
                                                                            String updateField,
                                                                            TermsConditionsAndCookiesRequest request);

    ResponseEntity<Mono<GetTermsConditionAndCookiesInfoResponse>> getTermsConditionAndCookiesInfoByDeviceId(
            String deviceId);

    CustomerDetailsResponse getCustomerDetailsBySearchTerm(String searchTerm);

    UserSuccessResponse validateUserName(String userName);

    DeRegisterDevicesResponse deRegisterDevices(UUID digitalCustomerProfileId, List<String> customerDeviceId);

    DigitalCustomerPwdResponse storeOldPassword(DigitalPwdRequest digitalPwdRequest);


    List<DeviceDataForRegisterDevice> getAllRegisterDevice(UUID digitalCustomerProfileId, boolean registered);

    CountryValidation getRules(CountryValidation request);

    String updateBiometricStatus(BiometricStatusDTO request, UUID customerProfileId);

    List<String>  getBroadCastReferenceId(UUID digitalCustmerProfileId);

    UpdateExpiryResponse updateExpiry(UpdateExpiryDTO updateExpiryDTO);

    UserLockResponse lockUserAccount(UUID digitalCustomerProfileId);
}
