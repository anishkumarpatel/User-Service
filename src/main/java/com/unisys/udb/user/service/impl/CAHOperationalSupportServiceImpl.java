package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.constants.MessageCodesConstants;
import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.request.BroadcastMessageContent;
import com.unisys.udb.user.dto.request.BroadcastMessageContentRequest;
import com.unisys.udb.user.dto.request.BroadcastMessageRequest;
import com.unisys.udb.user.dto.request.SaveBroadcastMsgRequest;
import com.unisys.udb.user.dto.request.UpdateBroadcastMessageRequest;
import com.unisys.udb.user.dto.request.WithdrawBroadcastMsgRequest;
import com.unisys.udb.user.dto.response.CAHBroadcastMessageDetailsResponse;
import com.unisys.udb.user.dto.response.CAHBroadcastResponse;
import com.unisys.udb.user.dto.response.ScheduleBroadcastMessageResponse;
import com.unisys.udb.user.dto.response.SearchBroadcastMessageResponse;
import com.unisys.udb.user.entity.cah.BroadcastMessage;
import com.unisys.udb.user.exception.cahoperationalsupportexception.BroadcastMessageNotFound;
import com.unisys.udb.user.exception.cahoperationalsupportexception.DatabaseException;
import com.unisys.udb.user.exception.cahoperationalsupportexception.InvalidBroadCastMessageStatusException;
import com.unisys.udb.user.exception.cahoperationalsupportexception.InvalidBroadcastMessageIdException;
import com.unisys.udb.user.exception.cahoperationalsupportexception.InvalidLocalCodeException;
import com.unisys.udb.user.repository.CAHBroadcastMessageRepository;
import com.unisys.udb.user.repository.DigitalLanguageRefRepository;
import com.unisys.udb.user.service.CAHOperationalSupportService;
import com.unisys.udb.user.service.client.BrandingServiceClient;
import com.unisys.udb.utility.constants.DateFormatType;
import com.unisys.udb.utility.linkmessages.dto.DynamicMessageResponse;
import com.unisys.udb.utility.util.DateUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.unisys.udb.user.constants.UdbConstants.BROADCAST_DELETE_STATUS_ID;
import static com.unisys.udb.user.constants.UdbConstants.BROADCAST_WITHDRAW_STATUS_ID;
import static com.unisys.udb.user.constants.UdbConstants.DATE_TIME_FORMAT;
import static com.unisys.udb.user.constants.UdbConstants.FIVE_CONSTANT;
import static com.unisys.udb.user.constants.UdbConstants.FOUR_CONSTANT;
import static com.unisys.udb.user.constants.UdbConstants.ONE_CONSTANT;
import static com.unisys.udb.user.constants.UdbConstants.SUCCESS;
import static com.unisys.udb.user.constants.UdbConstants.THREE_CONSTANT;
import static com.unisys.udb.user.constants.UdbConstants.TWO_CONSTANT;
import static com.unisys.udb.user.constants.UdbConstants.ZERO_CONSTANT;


@Service
@Slf4j
@RequiredArgsConstructor
public class CAHOperationalSupportServiceImpl implements CAHOperationalSupportService {

    private final CAHBroadcastMessageRepository cahBroadcastMessageRepository;
    private final BrandingServiceClient brandingServiceClient;
    private final DigitalLanguageRefRepository digitalLanguageRefRepository;

    @Override
    public List<CAHBroadcastResponse> getBroadCastMessagesInDraft() {
        log.debug("Inside getBroadCastMessagesInDraft from CAHOperationsSupportService");
        return Optional.ofNullable(cahBroadcastMessageRepository
                        .getListOfBroadcastMessages("draft"))
                .filter(list -> !list.isEmpty())
                .orElseThrow(() ->
                        new BroadcastMessageNotFound("No draft broadcast messages found"))
                .stream()
                .map(this::mapCahBroadcastResponse)
                .toList();
    }

    @Override
    public List<CAHBroadcastResponse> getCompletedBroadcastMessages() {
        log.debug("Inside getCompletedBroadcastMessages() from CAHOperationsSupportService");
        LocalDateTime dateTime = DateUtil.getTimeStampWithZone();

        return Optional.ofNullable(cahBroadcastMessageRepository
                        .fetchAllCompletedBroadcastMsgs(dateTime))
                .filter(list -> !list.isEmpty())
                .orElseThrow(() ->
                        new BroadcastMessageNotFound("No completed broadcast messages found."))
                .stream()
                .map(this::mapCahBroadcastResponse)
                .toList();
    }

    @Override
    public Mono<DynamicMessageResponse> withdrawLiveBroadcastMessages(WithdrawBroadcastMsgRequest request) {

        // validate the given message ID's of are live or not
        LocalDateTime dateTime = DateUtil.getTimeStampWithZone();
        try {
            Integer countOfLiveBroadcastMessages = cahBroadcastMessageRepository
                    .getCountOfLiveBroadcastMessages(dateTime, request.getMessageIds());

            if (countOfLiveBroadcastMessages == request.getMessageIds().size()) {

                // Update the live messages to withdraw status
                cahBroadcastMessageRepository.updateLiveMessagesStatusToWithdraw(BROADCAST_WITHDRAW_STATUS_ID,
                        request.getMessageIds(), DateUtil.getTimeStamp(), request.getModifiedBy());
                DynamicMessageResponse response = new DynamicMessageResponse(
                        SUCCESS,
                        "Message has been successfully withdrawn.",
                        List.of(new DynamicMessageResponse.Message(
                                MessageCodesConstants.BROADCAST_MESSAGE_WITHDRAW_SUCCESS_CODE,
                                Collections.emptyList())));

                return Mono.just(response);
            } else {
                throw new InvalidBroadcastMessageIdException("Invalid broadcast messageIds.");
            }
        } catch (DataAccessException exception) {
            throw new DatabaseException("Database error occurred while processing request.");
        }
    }

    @Override
    public List<CAHBroadcastResponse> getBroadCastMessagesWithStatusWithdrawn() {
        log.debug("Inside getBroadCastMessagesWithStatusWithdrawn from CAHOperationsSupportService");
        return Optional.ofNullable(cahBroadcastMessageRepository
                        .getListOfBroadcastMessages("withdrawn"))
                .filter(list -> !list.isEmpty())
                .orElseThrow(() ->
                        new BroadcastMessageNotFound("No broadcast messages found with status withdrawn"))
                .stream()
                .map(this::mapCahBroadcastResponse)
                .toList();
    }

    @Override
    public ScheduleBroadcastMessageResponse getLiveAndUpcomingBroadcastMessages() {
        log.debug("Fetching live and upcoming broadcast messages");
        LocalDateTime currentTime = DateUtil.getTimeStampWithZone();

        // Fetch live broadcast messages
        List<CAHBroadcastResponse> liveBroadcastMessages = Optional.ofNullable(
                        cahBroadcastMessageRepository.getLiveBroadcastMessages(currentTime))
                .orElse(Collections.emptyList())
                .stream()
                .map(this::mapCahBroadcastResponse)
                .toList();

        // Fetch upcoming broadcast messages
        List<CAHBroadcastResponse> upcomingBroadcastMessages = Optional.ofNullable(
                        cahBroadcastMessageRepository.getUpcomingBroadcastMessages(currentTime))
                .orElse(Collections.emptyList())
                .stream()
                .map(this::mapCahBroadcastResponse)
                .toList();

        if (liveBroadcastMessages.isEmpty() && upcomingBroadcastMessages.isEmpty()) {
            throw new BroadcastMessageNotFound("No live or upcoming broadcast messages found");
        }

        // Combine both live and upcoming messages into the DTO
        return ScheduleBroadcastMessageResponse.builder()
                .liveBroadcastMessages(liveBroadcastMessages)
                .upcomingBroadcastMessages(upcomingBroadcastMessages)
                .build();
    }

    @Override
    public CAHBroadcastMessageDetailsResponse getBroadcastMessagesById(Integer id) {
        log.debug("Fetching broadcast messages by message Id {}", id);

        Optional<CAHBroadcastMessageDetailsResponse> messageDetails
                = Optional.ofNullable(cahBroadcastMessageRepository
                        .getBroadcastMessagesById(id))
                .orElseThrow(() -> new BroadcastMessageNotFound("Error Occurred while fetching broadcast message"
                        + " with the given id"))
                .stream()
                .map(this::mapCahBroadcastMessageDetailResponse).findFirst();

        if (messageDetails.isEmpty()) {
            throw new BroadcastMessageNotFound("No broadcast messages found with the given id");
        }
        return messageDetails.get();
    }

    @Override
    @Transactional
    public DynamicMessageResponse updateBroadCastMessage(
            String templateStatus, UpdateBroadcastMessageRequest requestDto) {
        log.debug("Finding existing message by ID...");
        BroadcastMessage existingMessage = cahBroadcastMessageRepository
                .findByMessageId(requestDto.getMessageId())
                .orElseThrow(() -> new BroadcastMessageNotFound("BroadcastMessage not found for ID: "
                        + requestDto.getMessageId()));
        log.debug("Existing message found: {}", existingMessage);

        log.debug("Finding template status Id by templateStatus");
        Integer templateStatusId = cahBroadcastMessageRepository.findIdByTemplateStatusName(templateStatus)
                .orElseThrow(() -> new InvalidBroadCastMessageStatusException("Invalid BroadcastMessage Status"));

        try {
            log.debug("Updating message fields...");
            existingMessage.setMessageName(requestDto.getMessageName());
            existingMessage.setAccountType(requestDto.getAccountType().equalsIgnoreCase("All"));
            existingMessage.setStartDateTime(LocalDateTime.parse(requestDto.getStartDateTime(),
                    DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
            existingMessage.setEndDateTime(LocalDateTime.parse(requestDto.getEndDateTime(),
                    DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
            existingMessage.setTemplateStatusRefId(templateStatusId);
            existingMessage.setModificationDate(LocalDateTime.now());
            existingMessage.setModifiedBy(requestDto.getUpdatedBy());

            log.debug("Saving updated message to database...");
            cahBroadcastMessageRepository.save(existingMessage);

            log.debug("Creating new request object for branding service client...");
            BroadcastMessageRequest broadcastMessageRequest = new BroadcastMessageRequest();
            broadcastMessageRequest.setBrodCastMessageContent(requestDto.getBrodCastMessageContent());
            broadcastMessageRequest.setUpdatedBy(requestDto.getUpdatedBy());

            log.info("Calling branding service client to update message content...");
            brandingServiceClient.updateBroadCastMessage(requestDto.getMessageId(), broadcastMessageRequest);

            log.debug("Returning success response...");

            String successMessageCode;
            if ("SCHEDULE".equalsIgnoreCase(templateStatus)) {
                successMessageCode = MessageCodesConstants.BROADCAST_MESSAGE_SCHEDULED_SUCCESS_CODE;
            } else {
                successMessageCode = MessageCodesConstants.BROADCAST_MESSAGE_DRAFT_SUCCESS_CODE;
            }

            return new DynamicMessageResponse(
                    UdbConstants.STATUS_MESSAGE_SUCCESSFUL,
                    "Broadcast message updated successfully",
                    List.of(new DynamicMessageResponse.Message(successMessageCode, Collections.emptyList())));
        } catch (WebClientResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DatabaseException("An error occurred during the update: " + ex.getMessage());
        }
    }

    private CAHBroadcastResponse mapCahBroadcastResponse(Object[] row) {
        Timestamp startDateAndTime = (Timestamp) row[TWO_CONSTANT];
        String startDateTime = DateUtil.formatDate(startDateAndTime, DateFormatType.DAY_MONTH_YEAR_TIME);

        Timestamp endDateAndTime = (Timestamp) row[THREE_CONSTANT];
        String endDateTime = DateUtil.formatDate(endDateAndTime, DateFormatType.DAY_MONTH_YEAR_TIME);

        Timestamp modificationDateAndTime = (Timestamp) row[FIVE_CONSTANT];
        String modificationDateTime = null;

        if (modificationDateAndTime != null) {
            modificationDateTime = DateUtil.formatDate(modificationDateAndTime, DateFormatType.DAY_MONTH_YEAR_TIME);
        }

        //For phase 1 we are setting all values to true-- so it will reflect all the Account type to all
        String accountType = "All";
        log.debug("Account type set to All, value fetched from DB = {}", (Boolean) row[FOUR_CONSTANT]);
        return CAHBroadcastResponse.builder()
                .messageId(String.valueOf(row[ZERO_CONSTANT]))
                .messageName(String.valueOf(row[ONE_CONSTANT]))
                .startDateAndTime(startDateTime)
                .endDateAndTime(endDateTime)
                .modificationDate(modificationDateTime)
                .accountType(accountType)
                .build();
    }

    private CAHBroadcastMessageDetailsResponse mapCahBroadcastMessageDetailResponse(Object[] row) {
        Timestamp startDateAndTime = (Timestamp) row[TWO_CONSTANT];
        String startDateTime = DateUtil.formatDate(startDateAndTime, DateFormatType.DAY_MONTH_YEAR_TIME);

        Timestamp endDateAndTime = (Timestamp) row[THREE_CONSTANT];
        String endDateTime = DateUtil.formatDate(endDateAndTime, DateFormatType.DAY_MONTH_YEAR_TIME);

        //For phase 1 we are setting all values to true-- so it will reflect all the Account type to all
        String accountType = "All";
        log.debug("Account type set to All, value fetched from DB = {}", (Boolean) row[FOUR_CONSTANT]);

        Object broadcastMessageDescription = brandingServiceClient
                .getBroadcastMessageDescription((Integer) row[ZERO_CONSTANT]);

        log.debug("Message details fetched succesfully from branding service");

        return CAHBroadcastMessageDetailsResponse.builder()
                .messageId(String.valueOf(row[ZERO_CONSTANT]))
                .messageName(String.valueOf(row[ONE_CONSTANT]))
                .startDateAndTime(startDateTime)
                .endDateAndTime(endDateTime)
                .accountType(accountType)
                .status(String.valueOf(row[FIVE_CONSTANT]))
                .messageDescription(broadcastMessageDescription)
                .build();
    }

    @Override
    @Transactional
    public Mono<DynamicMessageResponse> saveBroadcastMessage(final SaveBroadcastMsgRequest request,
                                                             final String messageStatus) {
        log.debug("Finding template status Id by templateStatus.");
        Integer templateStatusId = cahBroadcastMessageRepository.findIdByTemplateStatusName(messageStatus)
                .orElseThrow(() -> new InvalidBroadCastMessageStatusException(" Broadcast message status must be "
                        + "either 'Schedule' " + "or 'Draft', but found: " + messageStatus));

        // validate the localeCode - fetch all the localeCode from the d

        List<String> availableLocaleCodes = digitalLanguageRefRepository.fetchAllLocaleCodeByLanguageEnabledFlag();

        for (BroadcastMessageContent content : request.getBrodCastMessageContent()) {
            if (!availableLocaleCodes.contains(content.getLocaleCode())) {
                throw new InvalidLocalCodeException("Invalid localeCode or Translation not available: "
                        + content.getLocaleCode());
            }
        }

        try {
            Long messageId = null;
            String successMessageCode = null;
            String message = null;
            messageId = insertDigitalDocdbBroadcastMsg(request, templateStatusId);

            if (messageStatus.equalsIgnoreCase("schedule")) {
                message = "Message has been successfully created & scheduled.";
                successMessageCode = MessageCodesConstants.BROADCAST_MESSAGE_SCHEDULED_SUCCESS_CODE;
            } else {
                message = "Message has been successfully saved as a draft.";
                successMessageCode = MessageCodesConstants.BROADCAST_MESSAGE_DRAFT_SUCCESS_CODE;
            }
            BroadcastMessageContentRequest broadcastMessageRequest = BroadcastMessageContentRequest.builder()
                    .brodCastMessageContent(request.getBrodCastMessageContent())
                    .messageId(messageId)
                    .createdBy(request.getCreatedBy())
                    .build();
            log.info("Calling branding service client to save broadcast message...");

            brandingServiceClient.saveBroadcastMessage(broadcastMessageRequest);

            log.debug("Returning the success response.");
            return Mono.just(new DynamicMessageResponse(
                    SUCCESS,
                    message,
                    List.of(new DynamicMessageResponse.Message(successMessageCode, Collections.emptyList()))));
        } catch (WebClientResponseException ex) {
            throw ex;
        } catch (Exception exception) {
            throw new DatabaseException("Database error occurred while processing request.");
        }
    }

    public Long insertDigitalDocdbBroadcastMsg(final SaveBroadcastMsgRequest request,
                                               final int digitalTemplateStatusRefId) {
        log.info("Saving the broadcast message in the  database...");
        boolean broadcastToAll = request.getAccountType().equalsIgnoreCase("all");
        BroadcastMessage broadcastMessage = BroadcastMessage.builder()
                .messageName(request.getMessageName())
                .accountType(broadcastToAll)
                .startDateTime(LocalDateTime.parse(request.getStartDateTime(),
                        DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)))
                .endDateTime(LocalDateTime.parse(request.getEndDateTime(),
                        DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)))
                .createdBy(request.getCreatedBy())
                .creationDate(LocalDateTime.now())
                .templateStatusRefId(digitalTemplateStatusRefId)
                .build();
        BroadcastMessage savedMessageDetails = cahBroadcastMessageRepository.saveAndFlush(broadcastMessage);
        return savedMessageDetails.getMessageId();
    }

    @Override
    @Transactional
    public Mono<DynamicMessageResponse> deleteBroadcastMessages(String request) {
        log.info("Deleting the broadcast message in the  database...");

        List<Integer> idList = Arrays.stream(request.split(","))
                .map(String::trim)
                .map(Integer::valueOf)
                .toList();

        try {

            Integer countOfBroadcastMessages = Optional.ofNullable(cahBroadcastMessageRepository
                            .getCountOfBroadcastMessagesForIdList(idList))
                    .orElseThrow(() -> new BroadcastMessageNotFound("Broadcast message doesn't exist"));

            if ((countOfBroadcastMessages == idList.size())) {
                for (Integer id : idList) {
                    deleteAtClient(id);
                }
            } else {
                throw new InvalidBroadcastMessageIdException("Messages could not be deleted");
            }

            // Update the status of broadcast messages to delete status
            cahBroadcastMessageRepository.deleteBroadCastMessage(BROADCAST_DELETE_STATUS_ID,
                    idList, DateUtil.getTimeStamp(), "admin");

            DynamicMessageResponse response = new DynamicMessageResponse(
                    SUCCESS,
                    "All Messages have been successfully deleted.",
                    List.of(new DynamicMessageResponse.Message(
                            MessageCodesConstants.BROADCAST_MESSAGE_DELETE_SUCCESS_CODE,
                            Collections.emptyList())));

            return Mono.just(response);
        } catch (DatabaseException exception) {
            throw new DataAccessException("Database access error.") {
            };
        }
    }

    private void deleteAtClient(int id) {
        try {
            log.debug("Calling branding service client "
                    + "to delete broadcast message details for id: {}", id);
            brandingServiceClient.deleteBroadCastMessage(id);
        } catch (WebClientResponseException e) {
            log.error("Exception Occurred while deleting");
            throw new BroadcastMessageNotFound("Error occurred at server");
        }
    }

    @Override
    public List<SearchBroadcastMessageResponse> searchBroadcastMessages(String nameOrId) {
        log.debug("Searching for broadcast messages by ID or Name with input: {}", nameOrId);

        if (nameOrId == null || nameOrId.trim().isEmpty()) {
            throw new BroadcastMessageNotFound("The search parameter cannot be null or empty.");
        }

        return Optional.ofNullable(
                        cahBroadcastMessageRepository.getBroadcastMessagesByIdOrName("%" + nameOrId + "%"))
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new BroadcastMessageNotFound("No broadcast messages found"))
                .stream()
                .map(this::mapToSearchBroadcastMessageResponse)
                .toList();
    }

    private SearchBroadcastMessageResponse mapToSearchBroadcastMessageResponse(Object[] messageData) {
        return SearchBroadcastMessageResponse.builder()
                .messageId(String.valueOf(messageData[0]))
                .messageName((String) messageData[1])
                .status((String) messageData[2])
                .build();
    }
}
