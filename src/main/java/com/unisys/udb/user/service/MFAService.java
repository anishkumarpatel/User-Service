package com.unisys.udb.user.service;

import com.unisys.udb.user.dto.request.MFARequest;
import com.unisys.udb.utility.linkmessages.dto.DynamicMessageResponse;

public interface MFAService {

    String getMFADetails(MFARequest mfaRequest);
    DynamicMessageResponse getMFAResponse(String mfaType, String deviceId, String mfaAction);

}
