package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.config.UdbFileConfig;
import com.unisys.udb.user.dto.response.UdbFileConfigResponse;
import com.unisys.udb.user.service.UdbFileConfigurationService;
import org.springframework.stereotype.Service;

@Service
public class UdbFileConfigurationServiceImpl implements UdbFileConfigurationService {

    private final UdbFileConfig udbFileConfig;

    public UdbFileConfigurationServiceImpl(UdbFileConfig fileConfig) {
        this.udbFileConfig = fileConfig;
    }

    @Override
    public UdbFileConfigResponse getUdbFileConfig() {
        return udbFileConfig.getUdbFileConfig();
    }
}

