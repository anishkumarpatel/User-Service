package com.unisys.udb.user.service;

import com.unisys.udb.user.dto.request.ReAuthenticateActivityRequest;

public interface ReAuthenticationService {

    String addReAuthenticateActivity(
            ReAuthenticateActivityRequest reAuthenticateActivityRequest);
}
