package com.unisys.udb.user.service;

import com.unisys.udb.user.dto.response.OldPasswordHistoryResponse;

import java.util.UUID;

public interface PasswordHistoryService {

    OldPasswordHistoryResponse fetchOldPasswords(UUID digitalCustomerProfileId);
}

