package com.unisys.udb.user.config;

import com.unisys.udb.user.dto.response.UdbFileConfigResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UdbFileConfig {

    @Value("${file.uploadFileFormat}")
    private String uploadFileFormat;

    @Value("${file.uploadFilesize}")
    private int uploadFileSize;

    @Value("${file.uploadNumberOfFiles}")
    private int uploadNumberOfFiles;


    @Bean
    public UdbFileConfigResponse getUdbFileConfig() {
        UdbFileConfigResponse udbFileConfigResponse = new UdbFileConfigResponse();
        udbFileConfigResponse.setUploadFileFormat(uploadFileFormat);
        udbFileConfigResponse.setUploadFileSize(uploadFileSize);
        udbFileConfigResponse.setUploadNumberOfFiles(uploadNumberOfFiles);
        return udbFileConfigResponse;
    }
}
