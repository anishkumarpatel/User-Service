package com.unisys.udb.user.controller;

import com.unisys.udb.user.dto.request.DigitalAlertRequest;
import com.unisys.udb.user.dto.response.DigitalAlertResponse;
import com.unisys.udb.user.dto.response.UserSuccessResponse;
import com.unisys.udb.user.service.DigitalCustomerAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/user")
@Slf4j
@CrossOrigin(origins = "${allowed.origins}")
@RequiredArgsConstructor
public class UserAlertController {
    private final DigitalCustomerAlertService digitalCustomerAlertService;

    /**
     * Retrieves unread digital customer alerts for a given profile ID.
     *
     * @param digitalCustomerProfileId The profile ID of the digital customer.
     * @return list of digital alerts.
     */

    @Operation(summary = "Get Digital Customer Alerts",
            description = "Fetches digital customer alerts for a given digital customer profile ID.",
            tags = {"Digital Customer Alerts"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation =
                            DigitalAlertResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid digital customer profile ID supplied"),
            @ApiResponse(responseCode = "404", description = "Digital customer profile not found")})
    @GetMapping("/alerts/{digitalCustomerProfileId}")
    public ResponseEntity<List<DigitalAlertResponse>> getDigitalCustomerAlerts(
            @NotNull @PathVariable("digitalCustomerProfileId") final UUID digitalCustomerProfileId) {
        log.debug("API Request: GET /alerts/{}", digitalCustomerProfileId);
        List<DigitalAlertResponse> customerAlerts = digitalCustomerAlertService.
                getDigitalCustomerAlerts(digitalCustomerProfileId);
        log.debug("API Response: 200 OK - Fetched {} digital customer alerts for profile id: {}", customerAlerts.size(),
                digitalCustomerProfileId);
        return ResponseEntity.ok().body(customerAlerts);
    }

    /**
     * Retrieves the count of unread digital customer alerts for a given profile ID.
     *
     * @param digitalCustomerProfileId The profile ID of the digital customer.
     * @return count of unread digital alerts.
     */
    @Operation(summary = "Get Unread Alert Counts",
            description = "Fetches the count of unread alerts for a given digital customer profile ID.",
            tags = {"Digital Customer Alerts"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "400", description = "Invalid digital customer profile ID supplied"),
            @ApiResponse(responseCode = "404", description = "Digital customer profile not found")})
    @GetMapping(value = "/alerts/count", produces = "application/json")
    public ResponseEntity<Integer> getUnreadAlertCounts(@RequestParam UUID digitalCustomerProfileId) {
        Integer count = digitalCustomerAlertService.countUnreadUserAlerts(digitalCustomerProfileId);
        log.debug("API Response: 200 OK - Fetched {} digital customer unread alerts for profile id: {}", count,
                digitalCustomerProfileId);
        return ResponseEntity.ok(count);
    }

    /**
     * Marks alerts as read based on the provided {@link DigitalAlertRequest}.
     *
     * @param alertRequest The request body containing the alert key and digital customer profile ID.
     *                     Must not be {@code null}.
     * @return A  {@link UserSuccessResponse} indicating the success of the operation.
     * The HTTP status code will be 200 (OK) if the operation is successful.
     */
    @Operation(summary = "Mark Alert as Read",
            description = "Marks a specific alert as read for a given digital customer profile ID.",
            tags = {"Digital Customer Alerts"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = UserSuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid digital customer profile ID or alert ID "
                    + "supplied"),
            @ApiResponse(responseCode = "404", description = "Digital customer profile or alert not found")})
    @PutMapping("/alerts")
    public ResponseEntity<UserSuccessResponse> markAlertAsRead(@Valid @RequestBody DigitalAlertRequest alertRequest) {
        log.debug("Received request to mark alert as read. AlertRequest: {}", alertRequest);
        return ResponseEntity.ok(digitalCustomerAlertService.markAlertAsRead(alertRequest));
    }

    /**
     * Endpoint to save a digital customer alert.
     *
     * @param alertRequest The request body containing the digital customer alert details.
     * @return ResponseEntity containing a success response if the alert is saved successfully.
     */
    @Operation(summary = "Save Digital Customer Alert",
            description = "Saves a digital customer alert based on the provided alert request.",
            tags = {"Digital Customer Alerts"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = UserSuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid digital customer profile ID or alert key "
                    + "supplied")})
    @PostMapping("/alerts")
    public ResponseEntity<UserSuccessResponse> saveDigitalCustomerAlert(
            @Valid @RequestBody DigitalAlertRequest alertRequest) {
        log.debug("Received request to save digital customer alert: {}", alertRequest);
        digitalCustomerAlertService.saveDigitalCustomerAlert(alertRequest);
        log.debug("Digital customer alert saved successfully");
        UserSuccessResponse successResponse = new UserSuccessResponse("Alert saved successfully");
        return ResponseEntity.ok(successResponse);
    }
}