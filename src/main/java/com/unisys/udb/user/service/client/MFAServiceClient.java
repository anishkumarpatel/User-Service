package com.unisys.udb.user.service.client;

import com.unisys.udb.user.dto.request.CustomerDetailsRequest;
import com.unisys.udb.utility.linkmessages.dto.DynamicMessageResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange
public interface MFAServiceClient {
    @PostExchange("api/v1/mfa/otp/send")
    DynamicMessageResponse sendOTP(@RequestBody CustomerDetailsRequest customerDetailsRequest);
}
