package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.request.CustomerDetailsRequest;
import com.unisys.udb.user.dto.request.MFARequest;
import com.unisys.udb.user.dto.response.UserInfoResponse;
import com.unisys.udb.user.exception.MFAConfigNotFoundException;
import com.unisys.udb.user.service.MFAService;
import com.unisys.udb.user.service.UserInfoService;
import com.unisys.udb.user.service.client.MFAServiceClient;
import com.unisys.udb.utility.linkmessages.dto.DynamicMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.unisys.udb.user.constants.UdbConstants.MFA_NONE;
import static com.unisys.udb.user.constants.UdbConstants.MOBILE;
import static com.unisys.udb.user.constants.UdbConstants.OTP;
import static com.unisys.udb.user.constants.UdbConstants.SUCCESS;
import static org.drools.drl.parser.lang.DroolsSoftKeywords.ACTIVE;

@Slf4j
@Service
@RequiredArgsConstructor
public class MFAServiceImpl implements MFAService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final MFAServiceClient mfaServiceClient;

    private final UserInfoService userInfoService;


    /*
    This method gets the mfa required flag and mfa type based on the mfa action and username
      from the redis cache
     */
    public String getMFADetails(MFARequest mfaRequest) {
        log.debug("Entering the MFA Details for the mfaAction::{} and userName:: {} ",
                mfaRequest.getMfaAction(), mfaRequest.getUserName());
        String mfaType;
        Map<String, Boolean> profileMap = (Map<String, Boolean>) redisTemplate.opsForHash()
                .get(UdbConstants.HASH_KEY, UdbConstants.UDB_MFA_CONFIG);
        if (profileMap == null) {
            throw new MFAConfigNotFoundException("MFA Config details Not found in redis cache");
        }
        if (Boolean.TRUE.equals(profileMap.get(mfaRequest.getMfaAction()))) {
            mfaType = Optional.ofNullable((Map<String, Object>) redisTemplate.opsForHash().
                            get(UdbConstants.HASH_KEY, mfaRequest.getUserName()))
                    .map(map -> (String) map.get(UdbConstants.MFA_TYPE)).orElse(null);
            log.debug("MFA required is true for the user::{} and mfa action ::{}", mfaRequest.getUserName(),
                    mfaRequest.getMfaAction());
            log.info("The MFA type of the user ::{} is ::{}", mfaRequest.getUserName(), mfaType);
            return mfaType;
        }
        return MFA_NONE;
    }

    /*
    This method sends OTP request to MFA Service if the mfa type is OTP else returns
     the MFA details
     */
    public DynamicMessageResponse getMFAResponse(String mfaType, String deviceId, String mfaAction) {
        log.debug("The mfaType for the user's device ::{} is ::{}", mfaType, deviceId);
        if (OTP.equalsIgnoreCase(mfaType)) {
            //Send the masked email and mobile after sending OTP for mfaType=OTP
            log.debug("Sending OTP for the  device id ::{}",
                    deviceId);
            UserInfoResponse userInfoResponse = userInfoService.getUserInfoResponse(deviceId);
            CustomerDetailsRequest customerDetailsRequest = CustomerDetailsRequest.builder().
                    coreCustomerProfileId(userInfoResponse.getCoreCustomerProfileId())
                    .otpEvent(mfaAction)
                    .channel(MOBILE)
                    .digitalDeviceUdId(deviceId).build();
            return mfaServiceClient.sendOTP(customerDetailsRequest);
        } else {
            //Send the response if the mfaType is not OTP
            List<DynamicMessageResponse.Message> messages = Collections.singletonList(
                    new DynamicMessageResponse.Message(UdbConstants.MFA_RECEIVED, Collections.emptyList())
            );
            return new DynamicMessageResponse(SUCCESS, ACTIVE, messages);
        }
    }
}
