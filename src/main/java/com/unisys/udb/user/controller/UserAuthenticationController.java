package com.unisys.udb.user.controller;

import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.request.ComparePinRequest;
import com.unisys.udb.user.dto.request.MFARequest;
import com.unisys.udb.user.dto.request.ReAuthenticateActivityRequest;
import com.unisys.udb.user.dto.request.UpdateExpiryDTO;
import com.unisys.udb.user.dto.response.ComparePinResponse;
import com.unisys.udb.user.dto.response.CustomerInactivityPeriodResponse;
import com.unisys.udb.user.dto.response.DeviceInfoResponse;
import com.unisys.udb.user.dto.response.DeviceRegistrationResponseDTO;
import com.unisys.udb.user.dto.response.DigitalCustomerDeviceResponse;
import com.unisys.udb.user.dto.response.OldPasswordHistoryResponse;
import com.unisys.udb.user.dto.response.PinHistoryResponse;
import com.unisys.udb.user.dto.response.UpdateExpiryResponse;
import com.unisys.udb.user.dto.response.UserActivityStatusResponse;
import com.unisys.udb.user.dto.response.UserLockResponse;
import com.unisys.udb.user.service.DigitalCustomerActivityService;
import com.unisys.udb.user.service.DigitalCustomerDeviceService;
import com.unisys.udb.user.service.MFAService;
import com.unisys.udb.user.service.PasswordHistoryService;
import com.unisys.udb.user.service.PinHistoryService;
import com.unisys.udb.user.service.PinServiceImpl;
import com.unisys.udb.user.service.ReAuthenticationService;
import com.unisys.udb.user.service.UserInfoService;
import com.unisys.udb.utility.linkmessages.dto.DynamicMessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserAuthenticationController {
    private final PasswordHistoryService passwordHistoryService;
    private final PinServiceImpl pinServiceImpl;
    private final PinHistoryService pinHistoryService;
    private final DigitalCustomerDeviceService digitalDeviceService;
    private final DigitalCustomerActivityService digitalCustomerActivityService;

    private final UserInfoService userInfoService;
    private final ReAuthenticationService reAuthenticationService;

    private final MFAService mfaService;


    @GetMapping("password/history/{digitalCustomerProfileId}")
    public ResponseEntity<OldPasswordHistoryResponse> getOldPasswords(@PathVariable UUID digitalCustomerProfileId) {
        log.debug("Fetching old passwords for digitalCustomerProfileId: {}", digitalCustomerProfileId);
        OldPasswordHistoryResponse oldPasswords = passwordHistoryService.fetchOldPasswords(digitalCustomerProfileId);
        log.debug("Old passwords fetched successfully for digitalCustomerProfileId: {}", digitalCustomerProfileId);
        return new ResponseEntity<>(oldPasswords, HttpStatus.OK);
    }

    @GetMapping("pin/history/{digitalCustomerProfileId}")
    public ResponseEntity<PinHistoryResponse> getOldPins(
            @PathVariable UUID digitalCustomerProfileId) {
        log.debug("Fetching old pins for digitalCustomerProfileId: {}", digitalCustomerProfileId);
        PinHistoryResponse oldPins = pinHistoryService.fetchOldPins(digitalCustomerProfileId);
        log.debug("Old pins fetched successfully for digitalCustomerProfileId: {}", digitalCustomerProfileId);
        return new ResponseEntity<>(oldPins, HttpStatus.OK);
    }

    @PostMapping("/pin/compare")
    public ResponseEntity<ComparePinResponse> comparePin(@RequestBody ComparePinRequest request) {
        log.debug("Comparing pin for digitalCustomerProfileId: {}", request.getDigitalCustomerProfileId());
        ComparePinResponse response = pinServiceImpl.comparePin(
                request.getDigitalCustomerProfileId(), request.getNewPin());
        log.debug("Pin comparison result: {}", response.getPinMatchedResponse());
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Get Device Information",
            description = "Fetches the device information based on the provided device ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device information fetched successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeviceInfoResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid device ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Device not found",
                    content = @Content)})
    @GetMapping("device/info/{deviceId}")
    public Mono<ResponseEntity<DigitalCustomerDeviceResponse>> getDeviceInfo(
            @Parameter(description = "Device ID", required = true)
            @PathVariable final String deviceId) {
        log.debug("Received request to fetch device information for deviceId: " + deviceId);
        return digitalDeviceService.getDeviceInfo(deviceId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .onErrorReturn(ResponseEntity.badRequest().build());
    }


    @Operation(summary = "Get Broadcast Message Id",
            description = "Fetches the broadcast reference id based on the provided profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Broadcast Reference list fetched successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))})})
    @GetMapping("broadcast/reference")
    public ResponseEntity<List<String>> getBroadCastMessageReference(@RequestParam UUID digitalCustomerProfileId) {
        log.debug("Received request to fetch broadcast  message  for the digitalCustomerProfileID: {}",
                digitalCustomerProfileId);
        List<String> broadCastReferenceIdList = userInfoService.getBroadCastReferenceId(
                digitalCustomerProfileId);
        return ResponseEntity.ok(broadCastReferenceIdList);
    }

    @Operation(summary = "Check Device Registration",
            description = "Checks if the device is registered based on the provided customer profile ID and device"
                    + " UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device registration status fetched successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeviceRegistrationResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid customer profile ID or device UUID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer contact information not found",
                    content = @Content)})
    @GetMapping("/device-registration")
    public ResponseEntity<DeviceRegistrationResponseDTO> checkDeviceRegistration(
            @Parameter(description = "Digital Customer Profile ID", required = true)
            @RequestParam("digitalCustomerProfileID") UUID digitalCustomerProfileID,
            @Parameter(description = "Digital Device UUID", required = true)
            @RequestParam("digitalDeviceUUID") String digitalDeviceUUID) {

        DeviceRegistrationResponseDTO responseDTO = digitalDeviceService.checkDeviceRegistration(
                digitalCustomerProfileID, digitalDeviceUUID);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(
            summary = "Check Inactivity Period for a Customer",
            description = "Checks if the customer has exceeded the inactivity "
                    + "period based on the provided digital customer profile ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer inactivity checked successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomerInactivityPeriodResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/inactivity/check/{digitalCustomerProfileId}")
    public ResponseEntity<CustomerInactivityPeriodResponse> hasCustomerExceededInactivityPeriod(
            @PathVariable UUID digitalCustomerProfileId) {
        log.debug("Inside hasCustomerExceededInactivityPeriod() method");
        CustomerInactivityPeriodResponse customerInactivityPeriodResponse = digitalCustomerActivityService
                .checkCustomerInactivityPeriod(digitalCustomerProfileId);
        return ResponseEntity.ok(customerInactivityPeriodResponse);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer activity status fetched successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserActivityStatusResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/activity-status/re-authentication/{digitalCustomerProfileId}")
    public ResponseEntity<UserActivityStatusResponse> getUserRecentReAuthenticationActivityStatus(
            @PathVariable UUID digitalCustomerProfileId) {
        log.debug("Inside getUserRecentReAuthenticationActivityStatus() method");
        UserActivityStatusResponse userActivityStatusResponse = digitalCustomerActivityService
                .getUserRecentReAuthenticationActivityStatus(digitalCustomerProfileId);
        return ResponseEntity.ok(userActivityStatusResponse);

    }

    /**
     * Updates the expiry date for a digital customer profile.
     *
     * @param updateExpiryDTO the update expiry DTO containing the username and update type
     * @return a ResponseEntity containing the update expiry response
     */
    @Operation(summary = "Update expiry", description = "Updates the expiry date for a digital customer profile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expiry date updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateExpiryResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid or missing update expiry DTO supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found with the provided username",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)})
    @PutMapping("/update/expiry")
    public ResponseEntity<UpdateExpiryResponse> updateExpiry(@Valid @RequestBody UpdateExpiryDTO updateExpiryDTO) {
        log.debug("Inside updateExpiry() method");
        UpdateExpiryResponse result = userInfoService.updateExpiry(updateExpiryDTO);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "Insert user activity", description = "Inserting the activity of the user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity inserted successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateExpiryResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Missing reAuthenticateActivityRequest property",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Activity not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)})
    @PostMapping("/add/re-authenticate-activity")
    public ResponseEntity<String> addReAuthenticateActivity(
            @RequestBody ReAuthenticateActivityRequest reAuthenticateActivityRequest) {
        log.debug("Inside getNotificationPreferences() method");
        return ResponseEntity.ok(reAuthenticationService.addReAuthenticateActivity(reAuthenticateActivityRequest));
    }

    @PutMapping("account/lock")
    public ResponseEntity<UserLockResponse> lockUserAccount(
            @RequestParam("digitalCustomerProfileID") UUID digitalCustomerProfileID) {
        log.debug("Received request to lock account with ID: {}", digitalCustomerProfileID);
        UserLockResponse response = userInfoService.lockUserAccount(digitalCustomerProfileID);
        log.debug("Response for locking account {}: {}", digitalCustomerProfileID, response.getMessage());
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @GetMapping("mfa/type")
    public ResponseEntity<DynamicMessageResponse> getMFAType(
            @RequestBody MFARequest mfaRequest) {
        log.debug("Entering the getMFA type for the mfa action ::{} for the user::{}",
                mfaRequest.getMfaAction(), mfaRequest.getUserName());
        String mfaType = mfaService.getMFADetails(mfaRequest);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(UdbConstants.MFA_TYPE, mfaType);
        return new ResponseEntity<>(mfaService.getMFAResponse(mfaType, mfaRequest.getDeviceId(),
                mfaRequest.getMfaAction()), httpHeaders, HttpStatus.OK);
    }
}
