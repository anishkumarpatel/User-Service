package com.unisys.udb.user.controller;

import com.unisys.udb.user.dto.response.UdbFileConfigResponse;
import com.unisys.udb.user.service.UdbFileConfigurationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/user")
public class UdbFileConfigController {


    private final UdbFileConfigurationService fileConfigurationService;

    public UdbFileConfigController(UdbFileConfigurationService fileConfigurationService) {
        this.fileConfigurationService = fileConfigurationService;
    }

    @GetMapping("/udbConfigurations")
    public Map<String, Object> getUdbFileConfig() {
        Map<String, Object> response = new HashMap<>();
        UdbFileConfigResponse fileParameter = fileConfigurationService.getUdbFileConfig();
        response.put("udbConfigParameters", fileParameter);
        return response;
    }

}
