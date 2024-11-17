package com.unisys.udb.user.controller;

import com.google.gson.Gson;
import com.unisys.udb.user.dto.request.AuditDetailsDTO;
import com.unisys.udb.user.dto.request.SessionHistoryFilterRequest;
import com.unisys.udb.user.dto.request.UserStatusServiceRequest;
import com.unisys.udb.user.dto.response.CustomerSessionHistoryResponse;
import com.unisys.udb.user.dto.response.CustomerStatusUpdateReasonResponse;
import com.unisys.udb.user.dto.response.UserAPIBaseResponse;
import com.unisys.udb.user.entity.DigitalCustomerProfile;
import com.unisys.udb.user.service.CAHService;
import com.unisys.udb.utility.auditing.annotation.BankAudit;
import com.unisys.udb.utility.auditing.dto.BankAuditHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.unisys.udb.user.constants.UdbConstants.ACTIVE_STATUS;
import static com.unisys.udb.user.constants.UdbConstants.AUDIT_LOG;
import static com.unisys.udb.user.constants.UdbConstants.DEACTIVATED_STATUS;
import static com.unisys.udb.user.constants.UdbConstants.ID;
import static com.unisys.udb.user.constants.UdbConstants.IP_ADDRESS;
import static com.unisys.udb.user.constants.UdbConstants.SUSPENDED_STATUS;
import static com.unisys.udb.user.constants.UdbConstants.UNLOCK_PENDING_STATUS;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/cah")
public class CAHController {

    private final CAHService cahService;
    private final BankAuditHolder bankAuditHolder;
    @Operation(summary = "Unsuspend user digital banking access status",
            description = "This API endpoint unsuspend the digital banking access status for a given "
                    + "customer profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = UserAPIBaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid customer profile ID supplied"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @BankAudit(topic = "bank-customer-audit", action = "Unsuspend")
    @PutMapping(path = "/unSuspendDigitalBankingAccess")
    public Mono<ResponseEntity<UserAPIBaseResponse>> unSuspendDigitalBankingAccess(
            @Parameter(description = "ID of the customer profile to unsuspend the digital banking access status for.",
                    required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User status service request object "
                    + "that needs to be added to the user", required = true)
            @Valid @RequestBody final UserStatusServiceRequest request) {
            final UUID digitalCustomerProfileId = request.getDigitalCustomerProfileId();
            log.debug("Inside the unSuspendDigitalBankingAccess method of user service for the customer profile "
                + ID + "{}", digitalCustomerProfileId);
            DigitalCustomerProfile digitalCustomerProfile = cahService.getCustomerStatus(digitalCustomerProfileId);
            Gson gson = new Gson();
            AuditDetailsDTO oldData = AuditDetailsDTO.builder()
                    .digitalCustomerStatusTypeRefId(digitalCustomerProfile.getDigitalCustomerStatusTypeId())
                    .digitalAccountStatusReason((digitalCustomerProfile.getDigitalAccountStatusReason()))
                    .build();
            Integer statusTypeRefId = cahService.getDigitalCustomerStatusTypeRefId(ACTIVE_STATUS);
            AuditDetailsDTO newData = AuditDetailsDTO.builder()
                    .digitalCustomerStatusTypeRefId(statusTypeRefId)
                    .digitalAccountStatusReason(request.getReason())
                    .build();
            bankAuditHolder.setDigitalCustomerId(digitalCustomerProfileId);
            bankAuditHolder.setInternalUserProfileId(UUID.randomUUID());
            bankAuditHolder.setIpAddress(IP_ADDRESS);
            bankAuditHolder.setReason(request.getReason());
            bankAuditHolder.setAuditOldDetails(gson.toJson(oldData));
            bankAuditHolder.setAuditNewDetails(gson.toJson(newData));
            log.info(AUDIT_LOG, bankAuditHolder.getAuditNewDetails());
            return cahService.unSuspendDigitalBankingAccess(request, digitalCustomerProfile, statusTypeRefId)
                    .map(response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @Operation(summary = "Suspend user digital banking access status",
            description = "This API endpoint suspend the digital banking access status for a given "
                    + "customer profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = UserAPIBaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid customer profile ID supplied"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @BankAudit(topic = "bank-customer-audit", action = "Suspend")
    @PutMapping(path = "/suspendDigitalBankingAccess")
    public Mono<ResponseEntity<UserAPIBaseResponse>> suspendDigitalBankingAccess(
            @Parameter(description = "ID of the customer profile to suspend the digital banking access status for.",
                    required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User status service request object "
                    + "that needs to be added to the user", required = true)
            @Valid @RequestBody final UserStatusServiceRequest request) {
            final UUID digitalCustomerProfileId = request.getDigitalCustomerProfileId();
            log.debug("Inside the suspendDigitalBankingAccess method of user service for the customer profile "
                + ID + "{}", digitalCustomerProfileId);
            DigitalCustomerProfile digitalCustomerProfile = cahService.getCustomerStatus(digitalCustomerProfileId);
            Gson gson = new Gson();
            AuditDetailsDTO oldData = AuditDetailsDTO.builder()
                    .digitalCustomerStatusTypeRefId(digitalCustomerProfile.getDigitalCustomerStatusTypeId())
                    .digitalAccountStatusReason((digitalCustomerProfile.getDigitalAccountStatusReason()))
                    .build();
            Integer statusTypeRefId = cahService.getDigitalCustomerStatusTypeRefId(SUSPENDED_STATUS);
            AuditDetailsDTO newData = AuditDetailsDTO.builder()
                    .digitalCustomerStatusTypeRefId(statusTypeRefId)
                    .digitalAccountStatusReason(request.getReason())
                    .build();
            bankAuditHolder.setDigitalCustomerId(digitalCustomerProfileId);
            bankAuditHolder.setInternalUserProfileId(UUID.randomUUID());
            bankAuditHolder.setIpAddress(IP_ADDRESS);
            bankAuditHolder.setReason(request.getReason());
            bankAuditHolder.setAuditOldDetails(gson.toJson(oldData));
            bankAuditHolder.setAuditNewDetails(gson.toJson(newData));
            log.info(AUDIT_LOG, bankAuditHolder.getAuditNewDetails());
            return cahService.suspendDigitalBankingAccess(request, digitalCustomerProfile, statusTypeRefId)
                    .map(response -> new ResponseEntity<>(response, HttpStatus.OK));

    }

    @Operation(summary = "Delete user digital banking access status",
            description = "This API endpoint deletes the digital banking access status for a given "
                    + "customer profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = UserAPIBaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid customer profile ID supplied"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @BankAudit(topic = "bank-customer-audit", action = "Deactivate")
    @PutMapping(path = "/deactivateDigitalBankingAccess")
    public Mono<ResponseEntity<UserAPIBaseResponse>> deactivateDigitalBankingAccess(
            @Parameter(description = "ID of the customer profile to deactivate the digital banking access status for.",
                    required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User status service request object "
                    + "that needs to be added to the user", required = true)
            @Valid @RequestBody final UserStatusServiceRequest request) {
            final UUID digitalCustomerProfileId = request.getDigitalCustomerProfileId();
            log.debug("Inside the deactivateDigitalBankingAccess method of user service for the customer profile "
                + ID + "{}", digitalCustomerProfileId);
            DigitalCustomerProfile digitalCustomerProfile = cahService.getCustomerStatus(digitalCustomerProfileId);
            Gson gson = new Gson();
            AuditDetailsDTO oldData = AuditDetailsDTO.builder()
                    .digitalCustomerStatusTypeRefId(digitalCustomerProfile.getDigitalCustomerStatusTypeId())
                    .digitalAccountStatusReason((digitalCustomerProfile.getDigitalAccountStatusReason()))
                    .build();
            Integer statusTypeRefId = cahService.getDigitalCustomerStatusTypeRefId(DEACTIVATED_STATUS);
            AuditDetailsDTO newData = AuditDetailsDTO.builder()
                    .digitalCustomerStatusTypeRefId(statusTypeRefId)
                    .digitalAccountStatusReason(request.getReason())
                    .build();
            bankAuditHolder.setDigitalCustomerId(digitalCustomerProfileId);
            bankAuditHolder.setInternalUserProfileId(UUID.randomUUID());
            bankAuditHolder.setIpAddress(IP_ADDRESS);
            bankAuditHolder.setReason(request.getReason());
            bankAuditHolder.setAuditOldDetails(gson.toJson(oldData));
            bankAuditHolder.setAuditNewDetails(gson.toJson(newData));
            log.info(AUDIT_LOG, bankAuditHolder.getAuditNewDetails());
            return cahService.deactivateDigitalBankingAccess(request, digitalCustomerProfile, statusTypeRefId)
                    .map(response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @Operation(summary = "Unlock user digital banking access status",
            description = "This API endpoint unlock the digital banking access status for a given "
                    + "customer profile ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = UserAPIBaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid customer profile ID supplied"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @BankAudit(topic = "bank-customer-audit", action = "Unlock")
    @PutMapping(path = "/unlockDigitalBankingAccess")
    public Mono<ResponseEntity<UserAPIBaseResponse>> unlockDigitalBankingAccess(
            @Parameter(description = "ID of the customer profile to unlock the digital banking access status for.",
                    required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User status service request object "
                    + "that needs to be added to the user", required = true)
            @Valid @RequestBody final UserStatusServiceRequest request) {
            final UUID digitalCustomerProfileId = request.getDigitalCustomerProfileId();
            log.debug("Inside the unlockDigitalBankingAccess method of user service for the customer profile "
                + ID + "{}", digitalCustomerProfileId);
            DigitalCustomerProfile digitalCustomerProfile = cahService.getCustomerStatus(digitalCustomerProfileId);
            Gson gson = new Gson();
            AuditDetailsDTO oldData = AuditDetailsDTO.builder()
                    .digitalCustomerStatusTypeRefId(digitalCustomerProfile.getDigitalCustomerStatusTypeId())
                    .digitalAccountStatusReason((digitalCustomerProfile.getDigitalAccountStatusReason()))
                    .build();
            Integer statusTypeRefId = cahService.getDigitalCustomerStatusTypeRefId(UNLOCK_PENDING_STATUS);
            AuditDetailsDTO newData = AuditDetailsDTO.builder()
                    .digitalCustomerStatusTypeRefId(statusTypeRefId)
                    .digitalAccountStatusReason(request.getReason())
                    .build();
            bankAuditHolder.setDigitalCustomerId(digitalCustomerProfileId);
            bankAuditHolder.setInternalUserProfileId(UUID.randomUUID());
            bankAuditHolder.setIpAddress(IP_ADDRESS);
            bankAuditHolder.setReason(request.getReason());
            bankAuditHolder.setAuditOldDetails(gson.toJson(oldData));
            bankAuditHolder.setAuditNewDetails(gson.toJson(newData));
            log.info(AUDIT_LOG, bankAuditHolder.getAuditNewDetails());
            return cahService.unlockDigitalBankingAccess(request, digitalCustomerProfile, statusTypeRefId)
                    .map(response -> new ResponseEntity<>(response, HttpStatus.OK));
    }
    @PostMapping(path = "/customer/activityHistory/{coreCustomerProfileId}")
    public ResponseEntity<CustomerSessionHistoryResponse> getCustomerActivityHistory(
            @Parameter(description = "Core customer profile Id", required = true)
            @PathVariable final UUID coreCustomerProfileId,
            @Parameter(description = "Offset for pagination", required = false)
            @RequestParam(defaultValue = "${session.history.default.offset}") Integer offset,
            @Parameter(description = "Row count for pagination", required = false)
            @RequestParam(defaultValue = "${session.history.default.rowcount}") Integer rowCount,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Activity history filter"
                    + "data transfer object", required = true,
                    content = @Content(schema = @Schema(implementation = SessionHistoryFilterRequest.class)))
            @Valid @RequestBody SessionHistoryFilterRequest sessionHistoryFilterRequest) {
            log.debug("getCustomerActivityHistory Request :: Start for core profile id {}", coreCustomerProfileId);
            CustomerSessionHistoryResponse customerSessionHistoryResponse = cahService
                    .getCustomerActivityHistory(
                            coreCustomerProfileId, offset, rowCount, sessionHistoryFilterRequest);
            if (customerSessionHistoryResponse.getCustomerSessionHistory().isEmpty()) {
                log.warn("getCustomerActivityHistory Request with empty list :: End");
                return new ResponseEntity<>(
                        customerSessionHistoryResponse, HttpStatus.NOT_FOUND);
            } else {
                log.debug("getCustomerActivityHistory Request :: End");
                return ResponseEntity.ok(customerSessionHistoryResponse);
            }
    }
    @GetMapping(path = "/reasonList/{digitalCustomerStatus}")
    public ResponseEntity<CustomerStatusUpdateReasonResponse> getReasonForCustomerAccountStatusUpdate(
            @PathVariable String digitalCustomerStatus) {
        log.debug("getReasonForCustomerAccountStatusUpdate :: Start");
        CustomerStatusUpdateReasonResponse response = cahService.getReasonsForCustomerStatusUpdate(
                digitalCustomerStatus);
        return ResponseEntity.ok(response);
    }
}
