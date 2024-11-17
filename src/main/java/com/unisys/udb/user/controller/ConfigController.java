package com.unisys.udb.user.controller;

import com.unisys.udb.user.config.AppConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

@RequestMapping("/api/config")
public class ConfigController {

    private AppConfig appConfig;

    @Autowired
    public ConfigController(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Operation(summary = "Update Configuration",
            description = "Fetches a key from the YAML configuration.",
            tags = {"Configuration Management"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @GetMapping("/")
    public String updateConfiguration() {
        return appConfig.getKeyFromYaml();
    }

    @Operation(summary = "Manage Configuration",
            description = "Fetches the login count from the configuration.",
            tags = {"Configuration Management"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @PutMapping("/")
    public Integer manageConfiguration() {
        return appConfig.getLoginCount();
    }
}
