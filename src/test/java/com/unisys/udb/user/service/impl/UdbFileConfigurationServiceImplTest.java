package com.unisys.udb.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.unisys.udb.user.config.UdbFileConfig;
import com.unisys.udb.user.dto.response.UdbFileConfigResponse;
import org.junit.jupiter.api.Test;

class UdbFileConfigurationServiceImplTest {

    @Test
    void testGetUdbFileConfig() {

        UdbFileConfigResponse actualUdbFileConfig = (new UdbFileConfigurationServiceImpl(new UdbFileConfig()))
                .getUdbFileConfig();
        assertNull(actualUdbFileConfig.getUploadFileFormat());
        assertEquals(0, actualUdbFileConfig.getUploadNumberOfFiles());
        assertEquals(0L, actualUdbFileConfig.getUploadFileSize());
    }
}
