package com.unisys.udb.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.request.BankingNotificationPreferenceRequest;
import com.unisys.udb.user.dto.request.BiometricStatusDTO;
import com.unisys.udb.user.dto.request.DeviceTokenRequest;
import com.unisys.udb.user.dto.request.DigitalCookiesPreferenceRequest;
import com.unisys.udb.user.dto.request.DigitalCustomerProfileDTO;
import com.unisys.udb.user.dto.request.DigitalCustomerShortcutsRequest;
import com.unisys.udb.user.dto.request.DigitalPwdRequest;
import com.unisys.udb.user.dto.request.MarketingNotificationPreferenceRequest;
import com.unisys.udb.user.dto.request.PinChangeRequest;
import com.unisys.udb.user.dto.request.PublicKeyUpdateRequest;
import com.unisys.udb.user.dto.request.SessionHistoryFilterRequest;
import com.unisys.udb.user.dto.request.TermsConditionsAndCookiesRequest;
import com.unisys.udb.user.dto.request.UserDetailDto;
import com.unisys.udb.user.dto.request.UserPublicKeyRequest;
import com.unisys.udb.user.dto.response.BankingNotificationPreferenceResponse;
import com.unisys.udb.user.dto.response.BiometricPublicKeyResponse;
import com.unisys.udb.user.dto.response.BiometricStatusResponse;
import com.unisys.udb.user.dto.response.CheckMfaStatusResponse;
import com.unisys.udb.user.dto.response.CheckPinStatusResponse;
import com.unisys.udb.user.dto.response.CoreCustomerProfileResponse;
import com.unisys.udb.user.dto.response.CustomerDetailsResponse;
import com.unisys.udb.user.dto.response.CustomerSessionHistoryResponse;
import com.unisys.udb.user.dto.response.DeRegisterDevicesResponse;
import com.unisys.udb.user.dto.response.DeviceDataForRegisterDevice;
import com.unisys.udb.user.dto.response.DeviceInfoResponse;
import com.unisys.udb.user.dto.response.DigitalCookiePreferenceResponse;
import com.unisys.udb.user.dto.response.DigitalCookiesPreferenceResponse;
import com.unisys.udb.user.dto.response.DigitalCustomerPwdResponse;
import com.unisys.udb.user.dto.response.DigitalCustomerShortcutsResponse;
import com.unisys.udb.user.dto.response.GetTermsConditionAndCookiesInfoResponse;
import com.unisys.udb.user.dto.response.MarketingNotificationPreferenceResponse;
import com.unisys.udb.user.dto.response.MarketingPreferenceResponse;
import com.unisys.udb.user.dto.response.NotificationPreferenceResponse;
import com.unisys.udb.user.dto.response.PinChangeResponse;
import com.unisys.udb.user.dto.response.PromotionOfferDto;
import com.unisys.udb.user.dto.response.TermsConditionsAndCookieResponse;
import com.unisys.udb.user.dto.response.UpdatePinStatusResponse;
import com.unisys.udb.user.dto.response.UserAPIBaseResponse;
import com.unisys.udb.user.dto.response.UserInfoResponse;
import com.unisys.udb.user.dto.response.UserNameResponse;
import com.unisys.udb.user.dto.response.UserStatusResponse;
import com.unisys.udb.user.dto.response.UserSuccessResponse;
import com.unisys.udb.user.entity.CountryValidation;
import com.unisys.udb.user.exception.CustomerOldPwdException;
import com.unisys.udb.user.exception.DeviceIdParamNotFoundException;
import com.unisys.udb.user.exception.DigitalCookiePreferenceUpdateException;
import com.unisys.udb.user.exception.DigitalCustomerProfileIdNotFoundException;
import com.unisys.udb.user.exception.DigitalCustomerProfileIdNotNullException;
import com.unisys.udb.user.exception.DigitalCustomerShortcutRequestNotFound;
import com.unisys.udb.user.exception.DigitalCustomerShortcutUpdateException;
import com.unisys.udb.user.exception.InvalidRequestException;
import com.unisys.udb.user.exception.PinValidationException;
import com.unisys.udb.user.service.DigitalCookiesPreferenceService;
import com.unisys.udb.user.service.DigitalCustomerDeviceService;
import com.unisys.udb.user.service.DigitalCustomerShortcutsService;
import com.unisys.udb.user.service.PinService;
import com.unisys.udb.user.service.PromotionOffers;
import com.unisys.udb.user.service.UserInfoService;
import com.unisys.udb.user.service.UserRegistrationService;
import com.unisys.udb.user.utils.JsonUtils;
import com.unisys.udb.user.utils.dto.response.CommonUtil;
import com.unisys.udb.user.utils.masking.LogMasking;
import com.unisys.udb.utility.auditing.annotation.Auditing;
import com.unisys.udb.utility.auditing.annotation.CustomerAuditing;
import com.unisys.udb.utility.auditing.dto.AuditDigitalCustomerHolder;
import com.unisys.udb.utility.linkmessages.dto.DynamicMessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.unisys.udb.user.constants.UdbConstants.DIGITAL_PROFILE_ID_NOT_NULL;
import static com.unisys.udb.user.constants.UdbConstants.EXCEPTION;
import static com.unisys.udb.user.constants.UdbConstants.FAILURE;
import static com.unisys.udb.user.constants.UdbConstants.INCORRECT_FORMAT;
import static com.unisys.udb.user.constants.UdbConstants.NOT_ACCEPTABLE;
import static com.unisys.udb.user.constants.UdbConstants.NOT_FOUND_ERROR_CODE;
import static com.unisys.udb.user.constants.UdbConstants.NOT_FOUND_ERROR_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.SHORTCUTS_NOT_ACCEPTABLE_MESSAGE;

@RestController
@RequestMapping("api/v1/user")
@Slf4j
@CrossOrigin(origins = "${allowed.origins}")
@RequiredArgsConstructor
public class UserController {

    public static final String CUSTOMER = "customer";
    private final UserRegistrationService userRegistrationService;
    private final UserInfoService userInfoService;
    private final PinService pinService;
    private final DigitalCustomerShortcutsService digitalCustomerShortcutsService;
    private final DigitalCookiesPreferenceService digitalCookiesPreferenceService;
    private final AuditDigitalCustomerHolder auditDigitalCustomerHolder;
    private final PromotionOffers promotionOffers;
    private final DigitalCustomerDeviceService digitalCustomerDeviceService;
    private final JsonUtils jsonUtils;

    @Operation(summary = "Get user information", description = "Fetches user information based on the provided "
            + "digital customer profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User information fetched successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserInfoResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid digital customer profile ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @GetMapping(value = "/userInfo", produces = "application/json")
    public Mono<ResponseEntity<UserInfoResponse>> getUserInfo(
            @Parameter(description = "Digital customer profile ID", required = true)
            @Valid @RequestParam final UUID digitalCustomerProfileId,
            @Valid @RequestParam(required = false) final String digitalDeviceUdid) {

        log.debug("Entering getUserInfo method with digitalCustomerProfileId: {}", digitalCustomerProfileId);
        return userRegistrationService.getUserInfo(digitalCustomerProfileId, digitalDeviceUdid)
                .flatMap(response -> {
                    // Calling the updateFailureAttemptDetailsByUsername method asynchronously
                    UserDetailDto userDetailsDTO = userRegistrationService.buildUserDetailsDTO(response);
                    Mono<Void> updateFailureAttempt = Mono.fromRunnable(() ->
                            userInfoService.updateFailureAttemptDetailsByUsername(userDetailsDTO)
                    );
                    // Subscribe to the updateFailureAttempt Mono to execute it asynchronously
                    updateFailureAttempt.subscribe();
                    // Returning the original response
                    return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
                });
    }

    @Auditing(topic = "audit-topic", action = "Set Notification Preference")
    @Operation(summary = "Update banking notification preference", description = "Updates the banking notification "
            + "preference for the provided digital customer profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification preference updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserAPIBaseResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid digital customer profile ID or request body "
                    + "supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @PutMapping(path = "/update/banking-notification-preference/{digitalCustomerProfileId}")
    public Mono<ResponseEntity<List<MarketingPreferenceResponse>>> updateBankingNotificationPreference(
            @Parameter(description = "Digital customer profile ID", required = true)
            @PathVariable final UUID digitalCustomerProfileId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Banking notification preference "
                    + "request object", required = true,
                    content = @Content(schema = @Schema(implementation = BankingNotificationPreferenceRequest.class)))
            @RequestBody final BankingNotificationPreferenceRequest request) {
        auditDigitalCustomerHolder.setDigitalCustomerId(digitalCustomerProfileId);
        jsonUtils.logAndAuditAction(digitalCustomerProfileId, request, CUSTOMER);
        log.debug("Inside the update banking notification preference method of user service for the customer "
                + "profile id {}", digitalCustomerProfileId);

        return userRegistrationService.updateBankingNotificationPreference(digitalCustomerProfileId, request)
                .map(ResponseEntity::ok)  // Method reference
                .subscribeOn(Schedulers.boundedElastic());
    }

    @CustomerAuditing(topic = "customer-audit-topic", action = "updateMarketingNotificationPrerences")
    @Auditing(topic = "audit-topic", action = "Set Marketing Preference")
    @Operation(summary = "Update marketing notification preference", description = "Updates the marketing "
            + "notification preference for the provided digital customer profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification preference updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserAPIBaseResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid digital customer profile ID or request body "
                    + "supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @PutMapping(path = "/update/marketing-notification-preference/{digitalCustomerProfileId}")
    public Mono<ResponseEntity<DynamicMessageResponse>> updateMarketingNotificationPreference(
            @Parameter(description = "Digital customer profile ID", required = true)
            @PathVariable final UUID digitalCustomerProfileId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Marketing notification preference "
                    + "request object", required = true,
                    content = @Content(schema = @Schema(implementation = MarketingNotificationPreferenceRequest.class)))
            @RequestBody final MarketingNotificationPreferenceRequest request) throws JsonProcessingException {

        jsonUtils.logAndAuditAction(digitalCustomerProfileId, request, CUSTOMER);
        auditDigitalCustomerHolder.setDigitalCustomerId(digitalCustomerProfileId);
        log.debug("Inside the update marketing  notification preference method of user service for the customer "
                        + "profile id {}",
                digitalCustomerProfileId);
        return userRegistrationService.updateMarketingNotificationPreference(
                        digitalCustomerProfileId, request)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    // To be deleted in near future
    @Operation(summary = "Get banking preference", description = "Fetches the banking preference for the "
            + "provided digital customer profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Banking preference fetched successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BankingNotificationPreferenceResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid or missing digital customer profile ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @GetMapping("/banking-preference")
    public Mono<ResponseEntity<BankingNotificationPreferenceResponse>> getBankingPreference(
            @Parameter(description = "Digital customer profile ID", required = true)
            @RequestParam(required = false) final UUID digitalCustomerProfileId) {
        log.debug("Inside the  banking  preference  method of user service for the  customer profile id {}",
                digitalCustomerProfileId);
        Objects.requireNonNull(digitalCustomerProfileId, DIGITAL_PROFILE_ID_NOT_NULL);
        return userRegistrationService.getBankingPreference(digitalCustomerProfileId)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get marketing preference", description = "Fetches the marketing preference for "
            + "the provided digital customer profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marketing preference fetched successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MarketingNotificationPreferenceResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid or missing digital customer profile ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @GetMapping("/marketing-preference")
    public Mono<ResponseEntity<MarketingNotificationPreferenceResponse>> getMarketingPreference(
            @Parameter(description = "Digital customer profile ID", required = true)
            @RequestParam(required = false) final UUID digitalCustomerProfileId) {
        log.debug("Inside the  marketing  preference  method of user service for the  customer profile id {}",
                digitalCustomerProfileId);
        Objects.requireNonNull(digitalCustomerProfileId, DIGITAL_PROFILE_ID_NOT_NULL);
        log.info("Exiting user controller method of  marketingPreference");
        return userRegistrationService.getMarketingPreference(digitalCustomerProfileId)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Register user and device information", description = "Registers the user and device "
            + "information for the provided digital customer profile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User and device information registered successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request body supplied",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)})
    @PostMapping(value = "/createUserAndDeviceInfo")
    public Mono<ResponseEntity<DynamicMessageResponse>> registerUserAndDeviceInfo(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Digital customer profile "
                    + "data transfer object", required = true,
                    content = @Content(schema = @Schema(implementation = DigitalCustomerProfileDTO.class)))
            @RequestBody final DigitalCustomerProfileDTO digitalCustomerProfileDTO) {
        log.debug("UserService- Inside UserService registerUserAndDeviceInfo");
        return userRegistrationService.saveUserAndDeviceInfo(digitalCustomerProfileDTO)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK));

    }

    @Operation(summary = "Fetch user information", description = "Fetches the user information for the provided "
            + "digital customer profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User information fetched successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CoreCustomerProfileResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid digital customer profile ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @GetMapping(value = "/fetchUserInfo", produces = "application/json")
    public Mono<ResponseEntity<CoreCustomerProfileResponse>> fetchUserInfo(
            @Parameter(description = "Digital customer profile ID", required = true)
            @Valid @RequestParam final UUID digitalCustomerProfileId) {
        log.debug("UserService- Inside UserService fetchUserInfo");
        final String maskedDigitalProfileId = LogMasking.maskingDigitlProfileId(digitalCustomerProfileId);
        log.info("UserService- Inside UserService fetchUserInfo " + maskedDigitalProfileId);
        return userRegistrationService.fetchUserInfo(digitalCustomerProfileId)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @Operation(summary = "Get digital customer profile", description = "Fetches the digital customer profile "
            + "for the provided digital device UDID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Digital customer profile fetched successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserInfoResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid digital device UDID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @GetMapping(value = "/digitalCustomerUserInfo/{digitalDeviceUdid}")
    public Mono<ResponseEntity<UserInfoResponse>> getDigitalCustomerProfile(
            @Parameter(description = "Digital device UDID", required = true)
            @PathVariable("digitalDeviceUdid") final String digitalDeviceUdid) {
        log.info("Inside userController, getDigitalCustomerProfile, {} ", digitalDeviceUdid);
        UserInfoResponse userInfoResponse = userInfoService.getUserInfoResponse(digitalDeviceUdid);
        return Mono.just(new ResponseEntity<>(userInfoResponse, HttpStatus.OK));
    }

    @Operation(summary = "Update digital customer shortcut", description = "Updates the digital customer "
            + "shortcut for the provided digital customer profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Digital customer shortcut updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserAPIBaseResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid digital customer profile ID or request "
                    + "body supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @CustomerAuditing(topic = "customer-audit-topic", action = "updateShortcuts")
    @PutMapping("/digitalCustomerShortcut/{digitalCustomerProfileId}")
    public Mono<ResponseEntity<UserAPIBaseResponse>> updateDigitalCustomerShortcut(
            @Parameter(description = "Digital customer profile ID", required = true)
            @PathVariable final UUID digitalCustomerProfileId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Digital customer shortcuts "
                    + "request object", required = true,
                    content = @Content(schema = @Schema(implementation = DigitalCustomerShortcutsRequest.class)))
            @Valid @RequestBody final DigitalCustomerShortcutsRequest shortcutsRequest,
            @RequestParam String digitalCustomerName)
            throws DigitalCustomerShortcutUpdateException, JsonProcessingException {
        log.debug("Inside the  digitalCustomerShortcut  method of user service for the  customer profile id {}",
                digitalCustomerProfileId);
        UserAPIBaseResponse digitalCustomerShortcutsResponse = null;
        ResponseEntity<UserAPIBaseResponse> responseEntity = null;
        List<String> errorCode = new ArrayList<>();
        List<String> params = new ArrayList<>();

        params.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern(UdbConstants.DATE_FORMAT)));
        try {
            if (!(CommonUtil.validateMandatoryFields(shortcutsRequest))) {
                errorCode.add(NOT_ACCEPTABLE);
                throw new DigitalCustomerShortcutRequestNotFound(errorCode,
                        HttpStatus.NOT_ACCEPTABLE,
                        FAILURE,
                        SHORTCUTS_NOT_ACCEPTABLE_MESSAGE,
                        params);
            } else {
                digitalCustomerShortcutsResponse =
                        digitalCustomerShortcutsService.updateDigitalCustomerShortcut(
                                digitalCustomerProfileId, shortcutsRequest).block();
                responseEntity = ResponseEntity
                        .status(HttpStatus.OK)
                        .body(digitalCustomerShortcutsResponse);
                jsonUtils.logAndAuditAction(digitalCustomerProfileId, shortcutsRequest, digitalCustomerName);
            }
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException" + EXCEPTION, ExceptionUtils.getStackTrace(e));
            errorCode.add(NOT_ACCEPTABLE);
            throw new DigitalCustomerShortcutRequestNotFound(errorCode,
                    HttpStatus.NOT_ACCEPTABLE,
                    FAILURE,
                    INCORRECT_FORMAT,
                    params);
        } catch (DigitalCustomerShortcutRequestNotFound e) {
            log.error("DigitalCustomerShortcutRequestNotFound" + EXCEPTION, ExceptionUtils.getStackTrace(e));
            errorCode.add(NOT_ACCEPTABLE);
            throw new DigitalCustomerShortcutRequestNotFound(errorCode,
                    HttpStatus.NOT_ACCEPTABLE,
                    FAILURE,
                    SHORTCUTS_NOT_ACCEPTABLE_MESSAGE,
                    params);
        } catch (DigitalCustomerProfileIdNotFoundException e) {
            log.error("DigitalCustomerProfileIdNotFoundException" + EXCEPTION, ExceptionUtils.getStackTrace(e));
            errorCode.add(NOT_FOUND_ERROR_CODE);
            throw new DigitalCustomerProfileIdNotFoundException(errorCode,
                    HttpStatus.NOT_FOUND,
                    FAILURE,
                    NOT_FOUND_ERROR_MESSAGE + digitalCustomerProfileId,
                    params);
        } catch (Exception e) {
            log.error(EXCEPTION, ExceptionUtils.getStackTrace(e));
            throw new DigitalCustomerShortcutUpdateException("Error updating digital customer shortcut", e);
        }
        log.info("Exiting user controller method of digitalCustomerShortcut");
        return Mono.just(responseEntity);

    }

    @Operation(summary = "Get username by device ID", description = "Fetches the username for "
            + "the provided digital customer device ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Username fetched successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserNameResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid digital customer device ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @GetMapping(value = "/getUserNameByDeviceId", produces = "application/json")
    public Mono<ResponseEntity<UserNameResponse>> getUserNameByDeviceId(
            @Parameter(description = "Digital customer device ID", required = true)
            @Valid @RequestParam final Integer digitalCustomerDeviceId) {
        log.debug("Inside the userNameByDeviceId method of user service for the  customer profile id {}",
                digitalCustomerDeviceId);
        return userRegistrationService.getUserNameInfoByCustomerDeviceId(digitalCustomerDeviceId)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get digital customer shortcut", description = "Fetches the digital customer shortcut "
            + "for the provided digital customer profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Digital customer shortcut fetched successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DigitalCustomerShortcutsResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid digital customer profile ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @GetMapping(value = "/getDigitalCustomerShortcut/{digitalCustomerProfileId}")
    public Mono<ResponseEntity<DigitalCustomerShortcutsResponse>> getDigitalCustomerShortcut(
            @Parameter(description = "Digital customer profile ID", required = true)
            @PathVariable final UUID digitalCustomerProfileId) {
        log.debug("Inside the digitalCustomerShortcut method of user service for the  customer profile id {}",
                digitalCustomerProfileId);
        String maskedId = LogMasking.maskingDigitlProfileId(digitalCustomerProfileId);
        log.info("Inside the digitalCustomerShortcut method of user service for the customer profile id {}",
                maskedId);
        return digitalCustomerShortcutsService.getDigitalCustomerShortcut(digitalCustomerProfileId)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get customer session history", description = "Fetches the customer session history "
            + "for the provided digital customer profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer session history fetched successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomerSessionHistoryResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid digital customer profile ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @Auditing(topic = "audit-topic", action = "View Session History")
    @PostMapping(value = "/customerSessionHistory/{digitalCustomerProfileId}")
    public ResponseEntity<CustomerSessionHistoryResponse> getCustomerSessionHistory(
            @Parameter(description = "Digital customer profile ID", required = true)
            @PathVariable final UUID digitalCustomerProfileId,
            @Parameter(description = "Offset for pagination", required = false)
            @RequestParam(defaultValue = "${session.history.default.offset}") Integer offset,
            @Parameter(description = "Row count for pagination", required = false)
            @RequestParam(defaultValue = "${session.history.default.rowcount}") Integer rowCount,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Session history filter"
                    + "data transfer object", required = true,
                    content = @Content(schema = @Schema(implementation = SessionHistoryFilterRequest.class)))
            @RequestBody SessionHistoryFilterRequest sessionHistoryFilterRequest) {
        auditDigitalCustomerHolder.setDigitalCustomerId(digitalCustomerProfileId);
        log.debug("getCustomerSessionHistory Request :: Start for profile id {}",
                digitalCustomerProfileId);
        CustomerSessionHistoryResponse customerSessionHistoryResponse = digitalCustomerShortcutsService
                .getCustomerSessionHistoryResponse(
                        digitalCustomerProfileId, offset, rowCount, sessionHistoryFilterRequest);
        if (customerSessionHistoryResponse.getCustomerSessionHistory().isEmpty()) {
            log.warn("getCustomerSessionHistory Request with empty list :: End");
            return new ResponseEntity<>(
                    customerSessionHistoryResponse, HttpStatus.NOT_FOUND);
        } else {
            log.debug("getCustomerSessionHistory Request :: End");
            return ResponseEntity.ok(customerSessionHistoryResponse);
        }
    }

    @Operation(summary = "Get digital cookie preference", description = "Fetches the digital cookie preference for "
            + "the provided digital customer profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Digital cookie preference fetched successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DigitalCookiePreferenceResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid or missing digital customer profile ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @GetMapping(value = "/manage-cookies", produces = "application/json")
    public Mono<ResponseEntity<DigitalCookiePreferenceResponse>> getDigitalCookiePreference(
            @Parameter(description = "Digital customer profile ID", required = false)
            @RequestParam(required = false) final UUID digitalCustomerProfileId) {
        log.debug("Inside the manage-cookies method of user service for the  customer profile id {}",
                digitalCustomerProfileId);
        return Optional.ofNullable(digitalCustomerProfileId)
                .map(id -> userRegistrationService.getDigitalCookiePreference(id)
                        .map(response -> new ResponseEntity<>(response, HttpStatus.OK)))
                .orElseThrow(() -> new DigitalCustomerProfileIdNotNullException(DIGITAL_PROFILE_ID_NOT_NULL));
    }

    @Operation(summary = "Validate Pin History", description = "Validates the pin history for the provided "
            + "digital customer profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pin history validated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PinChangeResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request body supplied",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)})
    @PostMapping("/validatePinHistory")
    public ResponseEntity<PinChangeResponse> validatePinHistory(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Pin change request object",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PinChangeRequest.class)))
            @RequestBody PinChangeRequest request) throws PinValidationException {
        log.debug("validatePinHistory: {}", request.getDigitalCustomerProfileId());
        return new ResponseEntity<>(pinService.validatePinHistory(request), HttpStatus.OK);
    }

    @Operation(summary = "Save Old Pin", description = "Saves the old pin for the provided digital "
            + "customer profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Old pin saved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PinChangeResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request body supplied",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)})
    @CustomerAuditing(topic = "customer-audit-topic", action = "changePin")
    @PostMapping("/saveOldPin")
    public PinChangeResponse saveOldPin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Pin change request object",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PinChangeRequest.class)))
            @RequestBody PinChangeRequest request, @RequestParam String digitalCustomerName)
            throws PinValidationException, JsonProcessingException {
        log.info("saveOldPin: {}", request.getDigitalCustomerProfileId());
        log.info("PinChangeRequest: {}", request.toString());
        String oldPinStore = request.getOldPin();
        request.setOldPin("Old Pin");
        request.setNewPin("New Pin");
        jsonUtils.logAndAuditAction(request.getDigitalCustomerProfileId(), request, digitalCustomerName);
        request.setOldPin(oldPinStore);
        return pinService.saveOldPin(request);
    }

    @Operation(summary = "Save Old Password", description = "Saves the old password for the provided profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Old password saved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DigitalCustomerPwdResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request body supplied",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)})
    @CustomerAuditing(topic = "customer-audit-topic", action = "changePassword")
    @PostMapping("/saveOldPassword")
    public DigitalCustomerPwdResponse saveOldPassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Old password request object",
                    required = true,
                    content = @Content(schema = @Schema(implementation = DigitalPwdRequest.class)))
            @RequestBody DigitalPwdRequest request, @RequestParam String digitalUserName)
            throws CustomerOldPwdException {
        log.info("saveOldPassword: {}", request.getDigitalProfileId());
        log.info("saveOldPassword: {}", request.toString());
        jsonUtils.logAndAuditAction(request.getDigitalProfileId(), request, digitalUserName);
        return userInfoService.storeOldPassword(request);
    }

    @Operation(summary = "Save digital cookie preference", description = "Saves the digital cookie preference for "
            + "the provided digital customer profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Digital cookie preference saved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DigitalCookiesPreferenceResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid digital customer profile ID or request "
                    + "body supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @CustomerAuditing(topic = "customer-audit-topic", action = "updateCookiePreferences")
    @PutMapping(value = "/saveDigitalCookiePreference/{digitalCustomerProfileId}")
    public Mono<ResponseEntity<DigitalCookiesPreferenceResponse>> saveDigitalCookiePreference(
            @Parameter(description = "Digital customer profile ID", required = true)
            @PathVariable final UUID digitalCustomerProfileId,
            @RequestParam final String digitalUserName,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Digital cookies preference request "
                    + "object", required = true,
                    content = @Content(schema = @Schema(implementation = DigitalCookiesPreferenceRequest.class)))
            @Valid @RequestBody final DigitalCookiesPreferenceRequest cookiesPreferenceRequest
    ) throws DigitalCookiePreferenceUpdateException {
        log.debug("Inside user controller method of saveDigitalCookiePreference");

        DigitalCookiesPreferenceResponse response = null;
        ResponseEntity<DigitalCookiesPreferenceResponse> responseEntity = null;
        try {
            response = digitalCookiesPreferenceService.saveDigitalCookiesPreferences(digitalCustomerProfileId,
                    cookiesPreferenceRequest).block();
            responseEntity = ResponseEntity.status(HttpStatus.OK)
                    .body(response);
            jsonUtils.logAndAuditAction(digitalCustomerProfileId, cookiesPreferenceRequest, digitalUserName);

        } catch (DigitalCustomerProfileIdNotFoundException e) {
            log.error("DigitalCustomerProfileIdNotFoundException" + EXCEPTION, ExceptionUtils.getStackTrace(e));
            List<String> errorCode = new ArrayList<>();
            errorCode.add(NOT_FOUND_ERROR_CODE);
            List<String> params = new ArrayList<>();
            params.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern(UdbConstants.DATE_FORMAT)));
            throw new DigitalCustomerProfileIdNotFoundException(errorCode,
                    HttpStatus.NOT_FOUND,
                    FAILURE,
                    NOT_FOUND_ERROR_MESSAGE + digitalCustomerProfileId,
                    params);

        } catch (Exception e) {
            log.error(EXCEPTION, ExceptionUtils.getStackTrace(e));
            throw new DigitalCookiePreferenceUpdateException("Exception occured while save and update the data", e);
        }
        log.info("Exiting user controller method of saveDigitalCookiePreference");
        return Mono.just(responseEntity);
    }

    @Operation(summary = "Fetch device information", description = "Fetches the device information for the "
            + "provided digital customer profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device information fetched successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeviceInfoResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid digital customer profile ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @GetMapping(value = "/fetchDeviceInfo", produces = "application/json")
    public Mono<ResponseEntity<DeviceInfoResponse>> fetchDeviceInfo(
            @Parameter(description = "Digital customer profile ID", required = true)
            @Valid @RequestParam final UUID digitalCustomerProfileId) {
        log.debug("Inside the fetchDeviceInfo method of user service for the  customer profile id {}",
                digitalCustomerProfileId);
        DeviceInfoResponse deviceInfoResponse = userInfoService.getUserDeviceInfo(digitalCustomerProfileId);
        log.debug("Exiting user controller method of fetchDeviceInfo");
        return Mono.just(new ResponseEntity<>(deviceInfoResponse, HttpStatus.OK));
    }

    //TO be deleted
    @Operation(summary = "Check Pin Status", description = "Checks if a pin exists based on the provided digital "
            + "device UDID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pin status checked successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CheckPinStatusResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid or missing digital device UDID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @GetMapping(value = "/checkPinStatus", produces = "application/json")
    public Mono<ResponseEntity<CheckPinStatusResponse>> checkPinExistsBasedOnDigitalDeviceUdid(
            @Parameter(description = "Digital device UDID", required = false)
            @RequestParam(required = false) final String digitalDeviceUdid) {
        log.debug("Inside the checkPinStatus method of user service for the  customer profile id {}",
                digitalDeviceUdid);
        Optional.ofNullable(digitalDeviceUdid)
                .filter(udid -> !udid.trim().isEmpty())
                .ifPresentOrElse(udid -> {
                }, () -> {
                    throw new IllegalArgumentException("Digital device UDID cannot be null or empty");
                });
        return userRegistrationService.checkPinExistsBasedOnDigitalDeviceId(digitalDeviceUdid)
                .map(ResponseEntity::ok);
    }

    /**
     * Checks if a user has set a PIN based on their digital customer profile ID.
     *
     * @param digitalCustomerProfileId The unique identifier of the digital customer profile.
     * @return A ResponseEntity containing a boolean indicating if the user has a PIN (true) or not (false).
     */
    @GetMapping(value = "/pin-status")
    @Operation(summary = "Check Pin Status",
            description = "Checks if a pin exists based on the provided digital customer profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User PIN status retrieved successfully.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid or missing digital customer profile ID supplied",
                    content = @Content)})
    public ResponseEntity<Boolean> checkUserPinStatus(
            @NotNull @RequestParam UUID digitalCustomerProfileId) {
        log.debug("Received request to check pin status for profile ID: {}", digitalCustomerProfileId);
        Boolean hasPin = userRegistrationService.checkUserPinStatus(digitalCustomerProfileId);
        log.debug("Pin status for profile ID {} is: {}", digitalCustomerProfileId, hasPin);
        return new ResponseEntity<>(hasPin, HttpStatus.OK);
    }

    @Operation(summary = "Update Login Attempt Details", description = "Updates the login attempt details for a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login attempt details updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body supplied",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)})
    @PutMapping("/login/failure")
    public void updateLoginAttemptDetailsByUsername(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User detail data transfer object",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserDetailDto.class)))
            @RequestBody UserDetailDto userDetailDto) {
        log.debug("userDetailDto:{}", userDetailDto);
        userInfoService.updateFailureAttemptDetailsByUsername(userDetailDto);
    }

    @Operation(summary = "Update Device Token", description = "Updates the device token for the provided "
            + "digital customer profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device token updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserAPIBaseResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid digital customer profile ID or request "
                    + "body supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @PatchMapping(path = "/updateDeviceToken")
    public Mono<ResponseEntity<UserAPIBaseResponse>> updateDeviceToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Device token request object",
                    required = true,
                    content = @Content(schema = @Schema(implementation = DeviceTokenRequest.class)))
            @RequestBody final DeviceTokenRequest request) {
        log.debug("Inside the  update device token method of user service for the customer profile id {}",
                request.getDigitalDeviceUdId());
        return userInfoService.updateDeviceToken(request)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @Operation(summary = "Get User Account Status", description = "Fetches the status of the user account for the "
            + "provided digital username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User account status fetched successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserStatusResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid digital username supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @GetMapping("/userAccountStatus")
    public Mono<ResponseEntity<UserStatusResponse>> getStatus(
            @Parameter(description = "Digital username", required = true)
            @RequestParam final String digitalUserName
    ) {
        log.debug("Inside the get Status method of user service");
        return userInfoService.getUserStatus(digitalUserName)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @Operation(summary = "Update Terms, Conditions and Cookies", description = "Updates the terms, "
            + "conditions and cookies for the provided digital customer device ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Terms, conditions and cookies updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TermsConditionsAndCookieResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid digital customer device ID, update field or "
                    + "request body supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @PutMapping(value = "/updateTermsAndCookies", produces = "application/json")
    public Mono<ResponseEntity<TermsConditionsAndCookieResponse>> updateTermsConditionsAndCookies(
            @Parameter(description = "Digital customer device ID", required = false)
            @RequestParam(required = false) final String digitalCustomerDeviceId,
            @Parameter(description = "Update field", required = false)
            @RequestParam(required = false) final String updateField,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Terms, conditions and cookies "
                    + "request object", required = true,
                    content = @Content(schema = @Schema(implementation = TermsConditionsAndCookiesRequest.class)))
            @RequestBody @Valid TermsConditionsAndCookiesRequest request) {
        if (StringUtils.isBlank(updateField) || StringUtils.equalsIgnoreCase(updateField, "null")
                || (!updateField.equalsIgnoreCase(UdbConstants.UPDATE_TERMS_CONDITIONS)
                && !updateField.equalsIgnoreCase(UdbConstants.UPDATE_COOKIES))) {
            log.error("Missing required param 'UpdateField'");
            throw new InvalidRequestException(UdbConstants.UPDATE_FIELD_NULL);
        }
        if (StringUtils.isEmpty(digitalCustomerDeviceId)) {
            log.error("Missing required param 'DigitalCustomerDeviceId'");
            throw new DeviceIdParamNotFoundException(UdbConstants.DEVICE_FIELD_NULL);
        }
        return userInfoService.updateTermsConditionsAndCookies(digitalCustomerDeviceId, updateField, request)
                .map(termsConditionsAndCookieResponse -> new ResponseEntity<>(termsConditionsAndCookieResponse,
                        HttpStatus.OK));
    }

    @Operation(summary = "Save User Public Key", description = "Saves the user's public key for "
            + "biometric authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User public key saved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserAPIBaseResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid digital customer profile ID or "
                    + "request body supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @PostMapping("/saveUserPublicKey")
    public ResponseEntity<UserAPIBaseResponse> savePublicKeyBioMetric(
            @Parameter(description = "Digital customer profile ID", required = true)
            @RequestParam final UUID digitalCustomerProfileId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User public key request object",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserPublicKeyRequest.class)))
            @RequestBody @Valid UserPublicKeyRequest userPublicKeyRequest) {
        log.debug("Inside savePublicKeyBioMetric method: {}", userPublicKeyRequest.getDeviceUUID());
        return ResponseEntity.ok().body(userRegistrationService.saveUserPublicKeyForBioMetric(digitalCustomerProfileId,
                userPublicKeyRequest));
    }

    /**
     * Endpoint to save pin public key for a device.
     *
     * @param publicKeyRequest The request containing the public key and device id to be saved.
     * @return ResponseEntity with HTTP status 200 (OK) upon successful saving of public key.
     */

    @Operation(summary = "Save Public Key Pin", description = "Saves the public key pin for the provided device UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Public key pin saved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserSuccessResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request body supplied",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)})
    @PutMapping("/device/pin/publicKey")
    public ResponseEntity<UserSuccessResponse> savePublicKeyPin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Public key update request object",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PublicKeyUpdateRequest.class)))
            @Valid @RequestBody PublicKeyUpdateRequest publicKeyRequest) {
        log.debug("Received request to save public key for user with device udid: {}",
                publicKeyRequest.getDeviceUdid());
        userRegistrationService.saveUserPublicKeyForPin(publicKeyRequest);
        log.debug("Successfully saved public key for user with device udid: {}", publicKeyRequest.getDeviceUdid());
        UserSuccessResponse successResponse = new UserSuccessResponse("Public key saved successfully");
        return ResponseEntity.ok(successResponse);
    }

    @Operation(summary = "Get User Public Key", description = "Fetches the public key for the "
            + "provided digital device UDID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Public key fetched successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid digital device UDID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @GetMapping("/userPublicKey")
    public ResponseEntity<BiometricPublicKeyResponse> getUserPublicKey(
            @Parameter(description = "Digital device UDID", required = true)
            @RequestParam final String digitalDeviceUdid,
            @Parameter(description = "Biometric Type", required = true)
            @RequestParam final String biometricType) {
        log.debug("Inside the get Status method of user service");
        return ResponseEntity.ok().body(userRegistrationService.getUserPublicKey(digitalDeviceUdid, biometricType));
    }

    @Operation(summary = "Get terms, conditions and cookies info by device ID",
            description = "This API endpoint retrieves the terms, conditions and cookies information "
                    + "for a given device ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid device ID supplied"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    @GetMapping("/termsAndConditions")
    public ResponseEntity<Mono<GetTermsConditionAndCookiesInfoResponse>> getTermsConditionAndCookiesInfoByDeviceId(
            @Parameter(description = "ID of the device to get the terms, conditions and cookies info for.",
                    required = true)
            @RequestParam("deviceId") String deviceId
    ) {
        return userInfoService.getTermsConditionAndCookiesInfoByDeviceId(deviceId);
    }


    @Operation(summary = "Get promotion offers",
            description = "This API endpoint retrieves the promotion offers for a given customer profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid customer profile ID supplied"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/promotionOffers")
    public Mono<List<PromotionOfferDto>> getPromotionOffers(
            @Parameter(description = "ID of the customer profile to get the promotion offers for.", required = true)
            @RequestParam final UUID digitalCustomerProfileId) {
        return promotionOffers.getPromotionOffers(digitalCustomerProfileId);
    }


    @Operation(summary = "Search customer",
            description = "This API endpoint searches for a customer based on the provided search term.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid search term supplied"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping(value = "/searchCustomer", produces = "application/json")
    public Mono<ResponseEntity<CustomerDetailsResponse>> searchCustomer(
            @Parameter(description = "Search term to find the customer.", required = true)
            @RequestParam String searchTerm) {
        log.debug("Inside the searchCustomer method of user service for the  search query {}",
                searchTerm);
        CustomerDetailsResponse customerDetailsBySearchTerm = userInfoService
                .getCustomerDetailsBySearchTerm(searchTerm);
        return Mono.just(new ResponseEntity<>(customerDetailsBySearchTerm, HttpStatus.OK));
    }

    @Operation(summary = "Validate username",
            description = "This API endpoint validates a given username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid username supplied"),
            @ApiResponse(responseCode = "404", description = "Username not found")
    })
    @GetMapping("/username/valid")
    public ResponseEntity<UserSuccessResponse> validateUserName(
            @Parameter(description = "Username to validate.", required = true)
            @RequestParam String userName) {
        log.debug("Inside the validateUserName to validate username");
        return ResponseEntity.ok(userInfoService.validateUserName(userName));
    }

    @Operation(summary = "Check MFA status based on digital device UDID",
            description = "This API endpoint checks the MFA status for a given digital device UDID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid digital device UDID supplied"),
            @ApiResponse(responseCode = "404", description = "Digital device not found")
    })
    @GetMapping(value = "/checkMfaStatus", produces = "application/json")
    public Mono<ResponseEntity<CheckMfaStatusResponse>> checkMfaStatusBasedOnDigitalDeviceUdid(
            @Parameter(description = "UDID of the digital device to check the MFA status for.", required = true)
            @RequestParam(required = false) final String digitalDeviceUdid) {
        log.debug("Inside the checkMfaStatus method of user service for the  customer profile id {}",
                digitalDeviceUdid);
        if (StringUtils.isEmpty(digitalDeviceUdid) || StringUtils.isBlank(digitalDeviceUdid)) {
            throw new IllegalArgumentException("Digital device UDID cannot be null or empty");
        }
        return userRegistrationService.checkMfaStatusBasedOnDigitalDeviceId(digitalDeviceUdid)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @Operation(summary = "Fetch public key from the database for the username",
            description = "This API endpoint fetches the public key from the database for the username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid payloadId/username supplied"),
            @ApiResponse(responseCode = "404", description = "Username not found")
    })
    @GetMapping("/device/pin/publicKey")
    public ResponseEntity<String> getUserPublicKeyForPin(
            @Parameter(description = "payloadDeviceId to fetch the device public key", required = true)
            @RequestParam final String payloadDeviceId,
            @Parameter(description = "username to fetch the device public key", required = true)
            @RequestParam final String username) {
        log.debug("Inside the get Status method of the user service");
        return ResponseEntity.ok().body(userRegistrationService.getUserPublicKeyForPin(payloadDeviceId, username));
    }

    @Operation(summary = "De-Registered the Devices",
            description = "This API endpoint will de-registered devices for a specific "
                    + "digital customer profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @CustomerAuditing(topic = "customer-audit-topic", action = "deRegisterDevice")
    @PutMapping("/deRegisterDevice")
    public ResponseEntity<DeRegisterDevicesResponse> deRegisteredDevices(@RequestParam UUID digitalCustomerProfileId,
                                                                         @RequestBody List<String> customerDeviceId) {
        log.debug("Inside the deRegisteredDevice method of user service");
        jsonUtils.logAndAuditAction(digitalCustomerProfileId, customerDeviceId, CUSTOMER);
        return ResponseEntity.ok().body(userInfoService.deRegisterDevices(digitalCustomerProfileId, customerDeviceId));
    }

    @Operation(summary = "Fetch Registered and De-Registered Devices",
            description = "This API endpoint fetches all registered and de-registered devices for a specific "
                    + "digital customer profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping("/getDevices")
    public List<DeviceDataForRegisterDevice> getAllDevices(
            @RequestParam UUID digitalCustomerProfileId, @RequestParam boolean registered) {

        log.debug("Inside the getAllDevices method");
        return userInfoService.getAllRegisterDevice(digitalCustomerProfileId, registered);
    }

    @PutMapping(value = "/updatePinSetCompleted", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<UpdatePinStatusResponse>> updatePinStatus(
            @RequestParam(required = false) final UUID digitalCustomerProfileId) {

        log.debug("Inside the updatePinStatus() of user service for the digitalCustomerProfileId: {}",
                digitalCustomerProfileId);
        if (digitalCustomerProfileId == null) {
            throw new DigitalCustomerProfileIdNotNullException(DIGITAL_PROFILE_ID_NOT_NULL);
        }
        return userRegistrationService.updatePinStatus(digitalCustomerProfileId, true)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @PostMapping(value = "/fetchRules")
    public ResponseEntity<CountryValidation> fetchRules(@RequestBody CountryValidation request) {
        log.debug("fetchRules start {}", request);
        CountryValidation countryValidation = userInfoService.getRules(request);
        log.debug("fetchRules end {}", request);
        return new ResponseEntity<>(countryValidation, HttpStatus.OK);
    }

    @Operation(summary = "Get Biometric status",
            description = "Get the biometric status of a digital customer by profile Id and device udid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BiometricStatusResponse.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid or missing digital customer profile Id supplied or device udid",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Digital customer not found",
                    content = @Content)})
    @GetMapping(value = "get/biometric/status")
    public Mono<ResponseEntity<BiometricStatusResponse>> getBiometricStatusForDevice(
            @Parameter(description = "Digital customer profile Id", required = true)
            @Valid @RequestParam final UUID digitalCustomerProfileId,
            @Parameter(description = "Digital customer device UDID", required = true)
            @Valid @RequestParam final String digitalDeviceUDID) {
        log.debug("Inside the getBiometricStatusForDevice() method");
        BiometricStatusResponse response =
                digitalCustomerDeviceService.getBiometricStatusForDevice(digitalCustomerProfileId, digitalDeviceUDID);
        return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
    }

    @CustomerAuditing(topic = "customer-audit-topic", action = "updateBioMetricStatus")
    @PutMapping(value = "/biometric/status")
    public ResponseEntity<String> updateBiometricStatus(@RequestBody BiometricStatusDTO request,
                                                        @Valid @RequestParam final UUID digitalCustomerProfileId) {
        log.debug("Inside updateBiometricStatus method");
        jsonUtils.logAndAuditAction(digitalCustomerProfileId, request, CUSTOMER);
        return ResponseEntity.ok().body(
                userInfoService.updateBiometricStatus(request, digitalCustomerProfileId));
    }

    @Operation(summary = "Save device information", description = "Register device "
            + "information for the provided digital customer profile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "device information registered successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request body supplied",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)})
    @CustomerAuditing(topic = "customer-audit-topic", action = "registerDevice")
    @PostMapping(value = "/registerNewDevice")
    public UserAPIBaseResponse registerDeviceInfo(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Digital customer profile "
                    + "data transfer object", required = true,
                    content = @Content(schema = @Schema(implementation = DigitalCustomerProfileDTO.class)))
            @Valid @RequestBody final DigitalCustomerProfileDTO digitalCustomerProfileDTO) {
        log.debug("UserService- Inside UserService registerNewDevice:registerDeviceInfo()");
        jsonUtils.logAndAuditAction(UUID.fromString(
                digitalCustomerProfileDTO.getDigitalCustomerProfileId()), digitalCustomerProfileDTO,
                CUSTOMER);
        return userRegistrationService.saveDeviceInfo(digitalCustomerProfileDTO);
    }

    /**
     * Retrieves marketing preferences for a given digital customer profile ID.
     *
     * @param digitalCustomerProfileId the UUID of the digital customer profile
     * @return a ResponseEntity containing a list of marketing preference responses
     */
    @Operation(summary = "Get marketing preference", description = "Fetches the marketing preference for "
            + "the provided digital customer profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marketing preference fetched successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MarketingPreferenceResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid or missing digital customer profile ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)})
    @GetMapping("/marketing/preferences")
    public ResponseEntity<List<MarketingPreferenceResponse>> getMarketingPreferences(
            @NotNull @RequestParam UUID digitalCustomerProfileId) {
        return ResponseEntity.ok(userRegistrationService.getMarketingPreferences(digitalCustomerProfileId));
    }

    /**
     * Retrieves notification preferences for a given digital customer profile ID.
     *
     * @param digitalCustomerProfileId the UUID of the digital customer profile
     * @return a ResponseEntity containing a list of notification preference responses
     */
    @Operation(summary = "Get notification preference", description = "Fetches the notification preference for "
            + "the provided digital customer profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification preference fetched successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotificationPreferenceResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid or missing digital customer profile ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)})
    @GetMapping("/notification/preferences")
    public ResponseEntity<List<NotificationPreferenceResponse>> getNotificationPreferences(
            @NotNull @RequestParam UUID digitalCustomerProfileId) {
        log.debug("Inside getNotificationPreferences() method");
        return ResponseEntity.ok(userRegistrationService.getNotificationPreferences(digitalCustomerProfileId));
    }
}
