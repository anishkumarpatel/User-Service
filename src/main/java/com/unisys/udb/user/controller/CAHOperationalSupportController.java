package com.unisys.udb.user.controller;

import com.unisys.udb.user.dto.request.SaveBroadcastMsgRequest;
import com.unisys.udb.user.dto.request.UpdateBroadcastMessageRequest;
import com.unisys.udb.user.dto.request.WithdrawBroadcastMsgRequest;
import com.unisys.udb.user.dto.response.CAHBroadcastMessageDetailsResponse;
import com.unisys.udb.user.dto.response.CAHBroadcastResponse;
import com.unisys.udb.user.dto.response.ScheduleBroadcastMessageResponse;
import com.unisys.udb.user.dto.response.SearchBroadcastMessageResponse;
import com.unisys.udb.user.exception.cahoperationalsupportexception.InvalidBroadCastMessageStatusException;
import com.unisys.udb.user.service.CAHOperationalSupportService;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/cahOperations")
public class CAHOperationalSupportController {

    private final CAHOperationalSupportService cahOperationalSupportService;

    @Operation(summary = "Fetch Broadcast Messages for CAH which are currently in draft",
            description = "This API endpoint fetches the Broadcast Messages for CAH which are currently in draft")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = CAHBroadcastResponse.class))),
            @ApiResponse(responseCode = "404", description = "Broadcast messages not found")
    })
    @GetMapping("/broadcastMessages/draft")
    public Mono<ResponseEntity<List<CAHBroadcastResponse>>> getAllBroadCastMessagesInDraft() {
        log.debug("Inside getAllBroadcastMessages of user service for fetching broadcast messages in draft");
        List<CAHBroadcastResponse> broadCastMessages = cahOperationalSupportService.getBroadCastMessagesInDraft();
        log.debug("Draft Broadcast Messages retrieved Successfully");
        return Mono.just(ResponseEntity.ok(broadCastMessages));
    }

    @Operation(summary = "Fetch Broadcast Messages for CAH which are withdrawn",
            description = "This API endpoint fetches the Broadcast Messages for CAH which are withdrawn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = CAHBroadcastResponse.class))),
            @ApiResponse(responseCode = "404", description = "Broadcast messages not found")
    })
    @GetMapping("/broadcastMessages/withdrawn")
    public Mono<ResponseEntity<List<CAHBroadcastResponse>>> getAllBroadCastMessagesInWithdrawn() {
        log.debug("Inside getAllBroadcastMessages of user service "
                + "for fetching broadcast messages with withdrawn status");
        List<CAHBroadcastResponse> broadCastMessages = cahOperationalSupportService
                .getBroadCastMessagesWithStatusWithdrawn();
        log.debug("Withdrawn Broadcast Messages fetched successfully");
        return Mono.just(ResponseEntity.ok(broadCastMessages));
    }

    @Operation(summary = "Fetch all completed broadcast messages",
            description = "Fetch all the completed broadcast messages in the CAH under "
                    + "marketing & communication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched all the completed message.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CAHBroadcastResponse.class))),
            @ApiResponse(responseCode = "404", description = "Broadcast messages not found")
    })
    @GetMapping("/broadcastMessages/completed")
    public ResponseEntity<List<CAHBroadcastResponse>> getCompletedBroadcastMessages() {
        log.debug("Inside getCompletedBroadcastMessages() method");
        return ResponseEntity.ok().body(cahOperationalSupportService.getCompletedBroadcastMessages());
    }

    @Operation(summary = "Withdraw live broadcast messages",
            description = "Withdraw single/multiple live broadcast messages in the CAH under "
                    + "marketing & communication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully withdraw live message based on messageId.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DynamicMessageResponse.class))),
            @ApiResponse(responseCode = "404", description = "messageId not found")
    })
    @PutMapping("/broadcastMessages/withdraw")
    public Mono<ResponseEntity<DynamicMessageResponse>> withdrawLiveBroadcastMessages(
            @RequestBody @Valid WithdrawBroadcastMsgRequest request) {
        log.debug("Inside withdrawLiveBroadcastMessages() method");
        return cahOperationalSupportService.withdrawLiveBroadcastMessages(request)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @Operation(summary = "Fetch Live and Upcoming Broadcast Messages",
            description = "This API endpoint fetches both Live and Upcoming Broadcast Messages")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched the broadcast messages",
                    content = @Content(schema = @Schema(implementation = CAHBroadcastResponse.class))),
            @ApiResponse(responseCode = "404", description = "Broadcast messages not found")
    })
    @GetMapping("/broadcastMessages/schedule")
    public Mono<ResponseEntity<ScheduleBroadcastMessageResponse>> getAllLiveAndUpcomingBroadcastMessages() {
        log.debug("Fetching live and upcoming broadcast messages");
        ScheduleBroadcastMessageResponse response = cahOperationalSupportService.getLiveAndUpcomingBroadcastMessages();
        return Mono.just(ResponseEntity.ok(response));
    }

    @Operation(summary = "Fetch specific Broadcast Message against message id",
            description = "This API endpoint fetches the specific Broadcast Message along with the details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = CAHBroadcastResponse.class))),
            @ApiResponse(responseCode = "404", description = "Broadcast messages not found")
    })
    @GetMapping("/broadcastMessage/{id}")
    public Mono<ResponseEntity<CAHBroadcastMessageDetailsResponse>> getBroadcastMessageDetailsById(
            @PathVariable Integer id) {
        log.debug("Fetching broadcast message by {}", id);
        CAHBroadcastMessageDetailsResponse response = cahOperationalSupportService
                .getBroadcastMessagesById(id);
        log.debug("Broadcast message fetched successfully");
        return Mono.just(ResponseEntity.ok(response));
    }

    /**
     * Updates the broadcast message for a given template status.
     *
     * @param messageStatus    The status of the template for which the broadcast message needs to be updated.
     * @param broadcastRequest The request containing the updated broadcast message details.
     * @return A ResponseEntity containing the DynamicMessageResponse with the updated broadcast message details.
     */
    @Operation(summary = "Update Broadcast Message",
            description = "Updates the content of a broadcast message based on the message code.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Broadcast message updated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DynamicMessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Broadcast message not found",
                    content = @Content)
    })
    @PutMapping("/broadcastMessage")
    public ResponseEntity<DynamicMessageResponse> updateBroadCastMessage(
            @RequestParam String messageStatus, @Valid @RequestBody UpdateBroadcastMessageRequest broadcastRequest) {
        log.debug("Received request to update broad cast message for message status: " + messageStatus);

        if (StringUtils.isBlank(messageStatus) || !(messageStatus.equalsIgnoreCase("SCHEDULE")
                || messageStatus.equalsIgnoreCase("DRAFT"))) {
            throw new InvalidBroadCastMessageStatusException("Broadcast message status must be either 'Schedule' "
                    + "or 'Draft', but found '" + messageStatus + "'");
        }

        DynamicMessageResponse response = cahOperationalSupportService
                .updateBroadCastMessage(messageStatus, broadcastRequest);
        log.debug("Updated broad cast message response: " + response.toString());
        return ResponseEntity.ok(response);
    }

    /**
     * Save the broadcast message for a given template status.
     *
     * @param messageStatus   The status of the template for which the broadcast message
     *                         needs to be updated e.g. schedule or draft.
     * @param request The request containing the broadcast message details.
     * @return A ResponseEntity containing the DynamicMessageResponse.
     */
    @Operation(summary = "Save Broadcast Message",
            description = "Save the broadcast message based on the message status")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Broadcast message saved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DynamicMessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = @Content)
    })
    @PostMapping("/broadcastMessage")
    public Mono<ResponseEntity<DynamicMessageResponse>> saveBroadcastMessage(
            @Parameter(description = "Broadcast message status", required = true)
            @RequestParam final String messageStatus,
            @RequestBody @Valid SaveBroadcastMsgRequest request) {
        log.debug("Received request to save broadcast message for message status: " + messageStatus);

        if (!("SCHEDULE".equalsIgnoreCase(messageStatus)
                || "DRAFT".equalsIgnoreCase(messageStatus))) {
            throw new InvalidBroadCastMessageStatusException("Broadcast message status must be either 'Schedule' "
                    + "or 'Draft', but found '" + messageStatus + "'");
        }
        return cahOperationalSupportService.saveBroadcastMessage(request, messageStatus)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    /**
     * Deletes single or multiple broadcast messages by ID.
     *
     * @param ids the code of the broadcast message to be deleted
     * @return Returns HTTP status 200 (OK) with a success message
     */
    @DeleteMapping("/broadcastMessage")
    @Operation(summary = "Delete broadcast messages",
            description = "Deletes broadcast messages identified by the given messageCodeID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the broadcast message"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "404", description = "messageId not found")
    })
    public Mono<ResponseEntity<DynamicMessageResponse>> deleteBroadCastMessages(
            @RequestParam @Valid String ids) {
        log.debug("Inside CAHOperationalSupportController -> deleteBroadCastMessages() method");
        return cahOperationalSupportService.deleteBroadcastMessages(ids)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    /**
     * Search broadcast message by either message ID or message Name.
     *
     * This API endpoint allows users to search for a broadcast message by passing a single parameter (`nameOrId`).
     * The system will search for a match in both the `digital_docdb_broadcast_msg_ref_id` (message ID) and
     * `broadcast_message_name` fields of the broadcast message record using the LIKE operator.
     * The search returns any broadcast messages where either the message ID or the message name contains the
     * provided `nameOrId` value.
     *
     * @param nameOrId The ID or name of the broadcast message to search for.
     * @return A ResponseEntity containing the list of broadcast messages that match the provided `nameOrId`.
     */
    @Operation(summary = "Search specific Broadcast Message against the passed message id/name",
            description = "This API endpoint searches the specific Broadcast Message along with the details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = CAHBroadcastResponse.class))),
            @ApiResponse(responseCode = "404", description = "Broadcast messages not found")
    })
    @GetMapping("/searchBroadcastMessage")
    public ResponseEntity<List<SearchBroadcastMessageResponse>> searchBroadcastMessage(
            @RequestParam String nameOrId) {

        log.debug("Received request to search broadcast message by ID or Name: {}", nameOrId);

        List<SearchBroadcastMessageResponse> responses =
                cahOperationalSupportService.searchBroadcastMessages(nameOrId);

        return ResponseEntity.ok(responses);
    }
}
