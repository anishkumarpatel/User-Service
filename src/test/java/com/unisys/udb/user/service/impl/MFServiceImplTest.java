package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.request.MFARequest;
import com.unisys.udb.user.dto.response.UserInfoResponse;
import com.unisys.udb.user.exception.MFAConfigNotFoundException;
import com.unisys.udb.user.service.UserInfoService;
import com.unisys.udb.user.service.client.MFAServiceClient;
import com.unisys.udb.utility.linkmessages.dto.DynamicMessageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.unisys.udb.user.constants.UdbConstants.MFA_NONE;
import static com.unisys.udb.user.constants.UdbConstants.SUCCESS;
import static org.drools.drl.parser.lang.DroolsSoftKeywords.ACTIVE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {MFAServiceImpl.class})
@ExtendWith(SpringExtension.class)
class MFServiceImplTest {

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private HashOperations hashOperations;

    @Autowired
    private MFAServiceImpl mfaServiceImpl;

    @MockBean
    private MFAServiceClient mfaServiceClient;

    @MockBean
    private UserInfoService userInfoService;

    @Test
    void testGetMFADetailsWithMFARequired() {
        MFARequest mfaRequest = MFARequest.builder().deviceId("123456").mfaAction("Change Password")
                .userName("user1").
                build();
        Map<String, Boolean> profileMap = new HashMap<>();
        profileMap.put("Change Password", true);
        Map<String, String> mfaMap = new HashMap<>();
        mfaMap.put(UdbConstants.MFA_TYPE, "OTP");
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(UdbConstants.HASH_KEY, UdbConstants.UDB_MFA_CONFIG)).thenReturn(profileMap);
        when(hashOperations.get(UdbConstants.HASH_KEY, "user1")).thenReturn(mfaMap);
        String mfa = mfaServiceImpl.getMFADetails(mfaRequest);
        assertEquals("OTP", mfa);
    }

    @Test
    void testGetMFADetailsWithNoMFA() {
        MFARequest mfaRequest = MFARequest.builder().deviceId("123456").mfaAction("Forgot Password")
                .userName("user1").
                build();
        Map<String, Boolean> profileMap = new HashMap<>();
        profileMap.put("Change Password", true);
        Map<String, String> mfaMap = new HashMap<>();
        mfaMap.put(UdbConstants.MFA_TYPE, "OTP");
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(UdbConstants.HASH_KEY, UdbConstants.UDB_MFA_CONFIG)).thenReturn(profileMap);
        when(hashOperations.get(UdbConstants.HASH_KEY, "user1")).thenReturn(mfaMap);
        String mfa = mfaServiceImpl.getMFADetails(mfaRequest);
        assertEquals(MFA_NONE, mfa);
    }

    @Test
    void testGetMFADetailsWithException() {
        MFARequest mfaRequest = MFARequest.builder().deviceId("123456").mfaAction("Change Password")
                .userName("user1").
                build();
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(UdbConstants.HASH_KEY, UdbConstants.UDB_MFA_CONFIG)).thenReturn(null);
        assertThrows(MFAConfigNotFoundException.class, () -> mfaServiceImpl.getMFADetails(mfaRequest));
    }

    @Test
    void testGetMFAResponseForOTP() {
      DynamicMessageResponse dynamicMessageResponse =
              new DynamicMessageResponse(SUCCESS, ACTIVE, new ArrayList<>());
        UUID uuid = UUID.randomUUID();
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setCoreCustomerProfileId(uuid);
        when(userInfoService.getUserInfoResponse(anyString())).thenReturn(userInfoResponse);
        when(mfaServiceClient.sendOTP(any())).thenReturn(dynamicMessageResponse);
        DynamicMessageResponse messageResponse =
                mfaServiceImpl.getMFAResponse("OTP", "12345678", "Change Password");
        assertNotNull(messageResponse);

    }
    @Test
    void testGetMFAResponseForBiometric() {
        DynamicMessageResponse dynamicMessageResponse =
                new DynamicMessageResponse(SUCCESS, ACTIVE, new ArrayList<>());
        UUID uuid = UUID.randomUUID();
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setCoreCustomerProfileId(uuid);
        when(userInfoService.getUserInfoResponse(anyString())).thenReturn(userInfoResponse);
        when(mfaServiceClient.sendOTP(any())).thenReturn(dynamicMessageResponse);
        DynamicMessageResponse messageResponse =
                mfaServiceImpl.getMFAResponse("Biometric", "12345678", "Change Password");
        assertNotNull(messageResponse);

    }


}
