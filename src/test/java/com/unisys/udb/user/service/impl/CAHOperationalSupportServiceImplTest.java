package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.constants.MessageCodesConstants;
import com.unisys.udb.user.dto.request.BroadcastMessageContent;
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
import com.unisys.udb.user.service.client.BrandingServiceClient;
import com.unisys.udb.user.service.client.ConfigurationServiceClient;
import com.unisys.udb.utility.config.DateUtilConfig;
import com.unisys.udb.utility.linkmessages.dto.DynamicMessageResponse;
import com.unisys.udb.utility.util.DateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.unisys.udb.user.constants.UdbConstants.ONE_CONSTANT;
import static com.unisys.udb.user.constants.UdbConstants.SUCCESS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CAHOperationalSupportServiceImplTest {

    private static final int TEN_LAKH = 1000000;
    private static final int FIVE_LAKH = 500000;
    private static final int FIFTEEN_LAKH = 1500000;
    private SaveBroadcastMsgRequest saveBroadcastMsgRequest;

    @InjectMocks
    private CAHOperationalSupportServiceImpl cahOperationsService;

    @Mock
    private DateUtilConfig configMock;

    @Mock
    private CAHBroadcastMessageRepository cahBroadcastMessageRepository;

    @Mock
    private DigitalLanguageRefRepository digitalLanguageRefRepository;

    @Mock
    private BrandingServiceClient brandingServiceClient;

    @Mock
    private ConfigurationServiceClient configurationServiceClient;

    private UpdateBroadcastMessageRequest requestDto;
    private BroadcastMessage existingMessage;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up request DTO
        requestDto = new UpdateBroadcastMessageRequest();
        requestDto.setMessageId(1);
        requestDto.setMessageName("Test Message");
        requestDto.setAccountType("All");
        requestDto.setStartDateTime("12-12-2023 10:00:00");
        requestDto.setEndDateTime("12-12-2023 12:00:00");
        requestDto.setUpdatedBy("admin");

        // Set up existing message
        existingMessage = BroadcastMessage.builder()
                .messageId(1L)
                .messageName("Old Message")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusHours(2))
                .accountType(true)
                .build();

        saveBroadcastMsgRequest = SaveBroadcastMsgRequest.builder()
                .accountType("all")
                .messageName("Test Message")
                .startDateTime("27-09-2022 14:30:00")
                .endDateTime("27-09-2022 14:30:00")
                .createdBy("User")
                .brodCastMessageContent(Collections.emptyList())
                .build();

        configMock = mock(DateUtilConfig.class);
        when(configMock.getZoneId()).thenReturn("en_US");
        when(configMock.getDayMonthYear()).thenReturn("dd/MM/yyyy");
        when(configMock.getDayMonthTime()).thenReturn("dd MMM hh:mm a");
        when(configMock.getDayMonthYearShort()).thenReturn("dd/MM/yy");
        when(configMock.getDayMonthYearTime()).thenReturn("dd/MM/yyyy hh:mm a");
        DateUtil.init(configMock);

    }


    @Test
    void testShouldReturnBroadcastMessagesWhenStatusIsValid() {
        Object[] row1 = {"1", "Downtime",
                Timestamp.valueOf("2024-06-27 01:37:37.233"),
                Timestamp.valueOf("2024-07-08 01:37:37.233"), true,
                Timestamp.valueOf("2024-08-27 01:37:37.233")};
        List<Object[]> broadcastMessages = new ArrayList<>();
        broadcastMessages.add(row1);

        when(cahBroadcastMessageRepository.getListOfBroadcastMessages("draft")).thenReturn(broadcastMessages);

        List<CAHBroadcastResponse> result = cahOperationsService.getBroadCastMessagesInDraft();
        assertThat(result).isNotNull();
        assertThat(result.get(0).getMessageName()).isEqualTo("Downtime");

    }

    @Test
    void testShouldThrowCahBroadcastMessageNotFoundWhenListIsEmptyOrNull() {
        // Initialize DateUtil with the mock DateUtilConfig
        when(cahBroadcastMessageRepository.getListOfBroadcastMessages("draft")).thenReturn(new ArrayList<>());
        assertThrows(BroadcastMessageNotFound.class, () -> cahOperationsService.getBroadCastMessagesInDraft());
    }

    @Test
    void testGetCompletedBroadcastMessagesShouldReturnListOfCompletedMessages() {
        LocalDateTime currentTime = LocalDateTime.now();
        try (MockedStatic<DateUtil> mockedTimeUtility = mockStatic(DateUtil.class)) {
            mockedTimeUtility.when(DateUtil::getTimeStampWithZone).thenReturn(currentTime);
            Object[] row1 = {"1", "schedule",
                    Timestamp.valueOf("2024-06-27 01:37:37.233"),
                    Timestamp.valueOf("2024-08-27 01:37:37.233"), true,
                    Timestamp.valueOf("2024-08-27 01:37:37.233")};
            List<Object[]> broadcastMessages = new ArrayList<>();
            broadcastMessages.add(row1);
            when(cahBroadcastMessageRepository.fetchAllCompletedBroadcastMsgs(currentTime))
                    .thenReturn(broadcastMessages);

            List<CAHBroadcastResponse> result = cahOperationsService.getCompletedBroadcastMessages();

            assertThat(result).isNotNull();
            CAHBroadcastResponse response = result.get(0);
            assertThat(response).isNotNull();
            assertThat(response.getMessageId()).isEqualTo("1");
            assertThat(response.getMessageName()).isEqualTo("schedule");
        }
    }

    @Test
    void testGetCompletedBroadcastMessagesWhenListIsEmptyOrNullShouldThrowCompletedMessageNotFoundException() {
        LocalDateTime currentTime = LocalDateTime.now();
        try (MockedStatic<DateUtil> mockedTimeUtility = mockStatic(DateUtil.class)) {
            mockedTimeUtility.when(DateUtil::getTimeStampWithZone).thenReturn(currentTime);
            when(cahBroadcastMessageRepository.fetchAllCompletedBroadcastMsgs(currentTime))
                    .thenReturn(new ArrayList<>());
            assertThrows(BroadcastMessageNotFound.class, () ->
                    cahOperationsService.getCompletedBroadcastMessages());
        }
    }

    @Test
    void testShouldReturnBroadcastMessagesWhenStatusIsWithdrawn() {
        Object[] row1 = {"1", "Downtime",
                Timestamp.valueOf("2024-06-27 01:37:37.233"),
                Timestamp.valueOf("2024-07-08 01:37:37.233"), true,
                Timestamp.valueOf("2024-08-27 01:37:37.233")};
        List<Object[]> broadcastMessages = new ArrayList<>();
        broadcastMessages.add(row1);

        when(cahBroadcastMessageRepository.getListOfBroadcastMessages("withdrawn")).thenReturn(broadcastMessages);

        List<CAHBroadcastResponse> result = cahOperationsService.getBroadCastMessagesWithStatusWithdrawn();
        assertThat(result).isNotNull();
        assertThat(result.get(0).getMessageName()).isEqualTo("Downtime");

    }

    @Test
    void testShouldThrowBroadcastMessageNotFoundWithWithdrawnStatusWhenListIsEmptyOrNull() {
        // Initialize DateUtil with the mock DateUtilConfig
        when(cahBroadcastMessageRepository.getListOfBroadcastMessages("withdrawn"))
                .thenReturn(new ArrayList<>());
        assertThrows(BroadcastMessageNotFound.class, () -> cahOperationsService
                .getBroadCastMessagesWithStatusWithdrawn());
    }

    @Test
    void testWithdrawLiveBroadcastMessagesShouldReturnDynamicMessageResponse() {

        LocalDateTime currentTime = LocalDateTime.now();
        try (MockedStatic<DateUtil> mockedTimeUtility = Mockito.mockStatic(DateUtil.class)) {
            mockedTimeUtility.when(DateUtil::getTimeStampWithZone).thenReturn(currentTime);
            WithdrawBroadcastMsgRequest request = WithdrawBroadcastMsgRequest.builder()
                    .messageIds(List.of(1, 2))
                    .build();

            when(cahBroadcastMessageRepository.getCountOfLiveBroadcastMessages(currentTime, request.getMessageIds()))
                    .thenReturn(2);
            doNothing().when(cahBroadcastMessageRepository)
                    .updateLiveMessagesStatusToWithdraw(anyString(), anyList(), any(Timestamp.class), anyString());

            Mono<DynamicMessageResponse> responseMono = cahOperationsService.withdrawLiveBroadcastMessages(request);

            StepVerifier.create(responseMono)
                    .assertNext(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.getResponseType()).isEqualTo(SUCCESS);
                        assertThat(response.getStatusMessage()).isEqualTo("Message has been successfully withdrawn.");
                        assertThat(response.getMessages()).isNotNull();

                        DynamicMessageResponse.Message message = response.getMessages().get(0);
                        assertThat(message.getMessageCode()).isEqualTo(
                                MessageCodesConstants.BROADCAST_MESSAGE_WITHDRAW_SUCCESS_CODE);
                    })
                    .verifyComplete();
        }
    }

    @Test
    void testWithdrawLiveBroadcastMessagesWhenInvalidMessageIdsShouldThrowInvalidMessageIdException() {

        LocalDateTime currentTime = LocalDateTime.now();
        try (MockedStatic<DateUtil> mockedTimeUtility = Mockito.mockStatic(DateUtil.class)) {
            mockedTimeUtility.when(DateUtil::getTimeStampWithZone).thenReturn(currentTime);
            WithdrawBroadcastMsgRequest request = WithdrawBroadcastMsgRequest.builder()
                    .messageIds(List.of(1, 2))
                    .build();

            when(cahBroadcastMessageRepository.getCountOfLiveBroadcastMessages(currentTime,
                    request.getMessageIds()))
                    .thenReturn(1); // Simulate mismatch

            assertThrows(InvalidBroadcastMessageIdException.class, () ->
                    cahOperationsService.withdrawLiveBroadcastMessages(request));
        }
    }

    @Test
    void testWithdrawLiveBroadcastMessagesWhenDataAccessExceptionShouldThrowDatabaseException() {
        // Arrange
        LocalDateTime currentTime = LocalDateTime.now();
        try (MockedStatic<DateUtil> mockedTimeUtility = Mockito.mockStatic(DateUtil.class)) {
            mockedTimeUtility.when(DateUtil::getTimeStampWithZone).thenReturn(currentTime);

            WithdrawBroadcastMsgRequest request = WithdrawBroadcastMsgRequest.builder()
                    .messageIds(List.of(1, 2))
                    .build();

            when(cahBroadcastMessageRepository.getCountOfLiveBroadcastMessages(any(), anyList()))
                    .thenThrow(new DataAccessException("Database error") {
                    });

            DatabaseException thrownException = assertThrows(DatabaseException.class, () -> {
                cahOperationsService.withdrawLiveBroadcastMessages(request);
            });
            assertTrue(thrownException.getMessage()
                    .contains("Database error occurred while processing request."));
        }
    }

    @Test
    void testShouldReturnLiveAndUpcomingBroadcastMessagesSuccessfully() {
        // Mock the current timestamp using mockStatic for static methods
        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            LocalDateTime currentTime = LocalDateTime.now();
            mockedDateUtil.when(DateUtil::getTimeStampWithZone).thenReturn(currentTime);

            // Mock live and upcoming broadcast messages
            List<Object[]> liveMessages = mockLiveBroadcastData();
            List<Object[]> upcomingMessages = mockUpcomingBroadcastData();

            // Mock repository calls
            when(cahBroadcastMessageRepository.getLiveBroadcastMessages(currentTime)).thenReturn(liveMessages);
            when(cahBroadcastMessageRepository.getUpcomingBroadcastMessages(currentTime)).thenReturn(upcomingMessages);

            // Call the service method
            ScheduleBroadcastMessageResponse result = cahOperationsService.getLiveAndUpcomingBroadcastMessages();

            // Assert that both live and upcoming messages are returned correctly
            assertNotNull(result);
            assertFalse(result.getLiveBroadcastMessages().isEmpty());
            assertFalse(result.getUpcomingBroadcastMessages().isEmpty());

            assertEquals("1", result.getLiveBroadcastMessages().get(0).getMessageId());
            assertEquals("Live Event", result.getLiveBroadcastMessages().get(0).getMessageName());

            assertEquals("2", result.getUpcomingBroadcastMessages().get(0).getMessageId());
            assertEquals("Upcoming Event", result.getUpcomingBroadcastMessages().get(0).getMessageName());
        }
    }

    @Test
    void testShouldThrowBroadcastMessageNotFoundWhenNoLiveMessagesAreFound() {
        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            LocalDateTime currentTime = LocalDateTime.now();
            mockedDateUtil.when(DateUtil::getTimeStampWithZone).thenReturn(currentTime);

            // Mock no live messages
            when(cahBroadcastMessageRepository.getLiveBroadcastMessages(currentTime))
                    .thenReturn(Collections.emptyList());

            // Expect exception when no live messages are found
            assertThrows(BroadcastMessageNotFound.class, () -> cahOperationsService
                    .getLiveAndUpcomingBroadcastMessages());
        }
    }

    @Test
    void testShouldReturnBroadcastMessageByIdSuccess() {
        Object messageDescription = new Object();
        Object[] row1 = {1, "Downtime",
                Timestamp.valueOf("2024-06-27 01:37:37.233"),
                Timestamp.valueOf("2024-07-08 01:37:37.233"), true, "draft", messageDescription};

        List<Object[]> broadcastMessages = new ArrayList<>();
        broadcastMessages.add(row1);

        when(cahBroadcastMessageRepository.getBroadcastMessagesById(ONE_CONSTANT)).thenReturn(broadcastMessages);
        when(brandingServiceClient.getBroadcastMessageDescription(ONE_CONSTANT)).thenReturn(new Object());

        CAHBroadcastMessageDetailsResponse result = cahOperationsService
                .getBroadcastMessagesById(ONE_CONSTANT);
        assertThat(result).isNotNull();
        assertThat(result.getMessageName()).isEqualTo("Downtime");

    }

    @Test
    void testBroadcastMessageByIdShouldThrowNotFoundWhenMessagesAreNotFound() {

        when(cahBroadcastMessageRepository.getBroadcastMessagesById(ONE_CONSTANT)).thenReturn(null);
        when(brandingServiceClient.getBroadcastMessageDescription(ONE_CONSTANT)).thenReturn(new Object());

        assertThrows(BroadcastMessageNotFound.class, () -> cahOperationsService
                .getBroadcastMessagesById(ONE_CONSTANT));

    }

    @Test
    void testBroadcastMessageByIdShouldThrowNotFoundWhenMessagesAreEmpty() {
        when(cahBroadcastMessageRepository.getBroadcastMessagesById(ONE_CONSTANT)).thenReturn(new ArrayList<>());
        when(brandingServiceClient.getBroadcastMessageDescription(ONE_CONSTANT)).thenReturn(new Object());

        assertThrows(BroadcastMessageNotFound.class, () -> cahOperationsService
                .getBroadcastMessagesById(ONE_CONSTANT));
    }

    private List<Object[]> mockLiveBroadcastData() {
        List<Object[]> liveBroadcastList = new ArrayList<>();
        Object[] liveEventData = new Object[]{
                "1", // Message ID
                "Live Event", // Message Name
                new Timestamp(System.currentTimeMillis()), // Start Date and Time
                new Timestamp(System.currentTimeMillis() + TEN_LAKH), // End Date and Time
                true, // Account type
                new Timestamp(System.currentTimeMillis() + TEN_LAKH), // Modification Date and Time

        };
        liveBroadcastList.add(liveEventData);
        return liveBroadcastList;
    }

    private List<Object[]> mockUpcomingBroadcastData() {
        List<Object[]> upcomingBroadcastList = new ArrayList<>();
        Object[] upcomingEventData = new Object[]{
                "2", // Message ID
                "Upcoming Event", // Message Name
                new Timestamp(System.currentTimeMillis() + FIVE_LAKH), // Start Date and Time
                new Timestamp(System.currentTimeMillis() + FIFTEEN_LAKH), // End Date and Time
                true, // Account type
                new Timestamp(System.currentTimeMillis() + TEN_LAKH), // Modification  Date and Time
        };
        upcomingBroadcastList.add(upcomingEventData);
        return upcomingBroadcastList;
    }

    /**
     * Method under test:
     * {@link CAHOperationalSupportServiceImpl#updateBroadCastMessage(String, UpdateBroadcastMessageRequest)}
     */
    @Test
    void updateBroadCastMessageWithValidInputAndScheduleStatusShouldUpdateSuccessfully() {
        String templateStatus = "SCHEDULE";
        when(cahBroadcastMessageRepository.findByMessageId(requestDto.getMessageId()))
                .thenReturn(Optional.of(existingMessage));
        when(cahBroadcastMessageRepository.findIdByTemplateStatusName(templateStatus)).thenReturn(Optional.of(1));

        DynamicMessageResponse response = cahOperationsService.updateBroadCastMessage(templateStatus, requestDto);

        assertNotNull(response);
        assertEquals("Broadcast message updated successfully", response.getStatusMessage());
        assertEquals(1, response.getMessages().size());

        verify(cahBroadcastMessageRepository).findByMessageId(requestDto.getMessageId());
        verify(cahBroadcastMessageRepository).findIdByTemplateStatusName(templateStatus);
        verify(cahBroadcastMessageRepository).save(existingMessage);
    }

    /**
     * Method under test:
     * {@link CAHOperationalSupportServiceImpl#updateBroadCastMessage(String, UpdateBroadcastMessageRequest)}
     */
    @Test
    void updateBroadCastMessageWithNonExistentMessageIdShouldThrowBroadcastMessageNotFound() {
        String templateStatus = "SCHEDULE";
        when(cahBroadcastMessageRepository.findByMessageId(requestDto.getMessageId())).thenReturn(Optional.empty());

        assertThrows(BroadcastMessageNotFound.class, () ->
                cahOperationsService.updateBroadCastMessage(templateStatus, requestDto)
        );
        verify(cahBroadcastMessageRepository).findByMessageId(requestDto.getMessageId());
    }

    /**
     * Method under test:
     * {@link CAHOperationalSupportServiceImpl#updateBroadCastMessage(String, UpdateBroadcastMessageRequest)}
     */
    @Test
    void updateBroadCastMessageWhenBrandingServiceClientThrowsWebClientResponseException() {
        String templateStatus = "SCHEDULE";
        when(cahBroadcastMessageRepository.findByMessageId(requestDto.getMessageId()))
                .thenReturn(Optional.of(existingMessage));
        when(cahBroadcastMessageRepository.findIdByTemplateStatusName(templateStatus)).thenReturn(Optional.of(1));
        doThrow(WebClientResponseException.class).when(brandingServiceClient)
                .updateBroadCastMessage(anyInt(), any());

        assertThrows(WebClientResponseException.class, () ->
                cahOperationsService.updateBroadCastMessage(templateStatus, requestDto)
        );
    }

    /**
     * Method under test:
     * {@link CAHOperationalSupportServiceImpl#updateBroadCastMessage(String, UpdateBroadcastMessageRequest)}
     */
    @Test
    void updateBroadCastMessageWithInvalidStatusShouldThrowInvalidBroadCastMessageStatusException() {
        String templateStatus = "Complete";
        when(cahBroadcastMessageRepository.findByMessageId(requestDto.getMessageId()))
                .thenReturn(Optional.of(existingMessage));
        doThrow(InvalidBroadCastMessageStatusException.class).when(cahBroadcastMessageRepository)
                .findIdByTemplateStatusName(templateStatus);

        assertThrows(InvalidBroadCastMessageStatusException.class, () ->
                cahOperationsService.updateBroadCastMessage(templateStatus, requestDto)
        );
    }

    /**
     * Method under test:
     * {@link CAHOperationalSupportServiceImpl#updateBroadCastMessage(String, UpdateBroadcastMessageRequest)}
     */
    @Test
    void updateBroadCastMessageWhenDatabaseOperationFailsShouldThrowDatabaseException() {
        String templateStatus = "SCHEDULE";
        when(cahBroadcastMessageRepository.findByMessageId(requestDto.getMessageId()))
                .thenReturn(Optional.of(existingMessage));
        when(cahBroadcastMessageRepository.findIdByTemplateStatusName(templateStatus))
                .thenReturn(Optional.of(1));
        when(cahBroadcastMessageRepository.save(any()))
                .thenThrow(new RuntimeException("Database error"));

        DatabaseException exception = assertThrows(DatabaseException.class, () ->
                cahOperationsService.updateBroadCastMessage(templateStatus, requestDto)
        );
        assertTrue(exception.getMessage().contains("An error occurred during the update"));

        verify(cahBroadcastMessageRepository).findByMessageId(requestDto.getMessageId());
        verify(cahBroadcastMessageRepository).findIdByTemplateStatusName(templateStatus);
    }

    /**
     * Method under test:
     * {@link CAHOperationalSupportServiceImpl#updateBroadCastMessage(String, UpdateBroadcastMessageRequest)}
     */
    @Test
    void updateBroadCastMessageWithDraftStatusShouldUpdateSuccessfullyWithDraftCode() {
        String templateStatus = "DRAFT";
        when(cahBroadcastMessageRepository.findByMessageId(requestDto.getMessageId()))
                .thenReturn(Optional.of(existingMessage));
        when(cahBroadcastMessageRepository.findIdByTemplateStatusName(templateStatus))
                .thenReturn(Optional.of(2));

        DynamicMessageResponse response = cahOperationsService.updateBroadCastMessage(templateStatus, requestDto);

        assertNotNull(response);
        assertEquals("Broadcast message updated successfully", response.getStatusMessage());
        assertEquals(1, response.getMessages().size());

        verify(cahBroadcastMessageRepository).findByMessageId(requestDto.getMessageId());
        verify(cahBroadcastMessageRepository).findIdByTemplateStatusName(templateStatus);
        verify(cahBroadcastMessageRepository).save(existingMessage);
    }

    /**
     * Method under test:
     * {@link CAHOperationalSupportServiceImpl#saveBroadcastMessage(SaveBroadcastMsgRequest, String)}
     */
    @Test
    void testSaveBroadcastMessageWithScheduleStatusShouldReturnDynamicResponse() {

        String templateStatus = "schedule";
        when(cahBroadcastMessageRepository.findIdByTemplateStatusName(templateStatus))
                .thenReturn(Optional.of(2));

        // Mocking the behavior of the repository and service client
        when(cahBroadcastMessageRepository.saveAndFlush(any(BroadcastMessage.class)))
                .thenReturn(BroadcastMessage.builder().messageId(1L).build());

        List<String> localeCode = new ArrayList<>();
        localeCode.add("en_US");
        localeCode.add("es_ES");

        // Mocking the behavior of the repository and service client

        when(digitalLanguageRefRepository.fetchAllLocaleCodeByLanguageEnabledFlag()).thenReturn(localeCode);
        // Call the method
        Mono<DynamicMessageResponse> responseMono = cahOperationsService
                .saveBroadcastMessage(saveBroadcastMsgRequest, templateStatus);

        // Verify interactions
        verify(brandingServiceClient).saveBroadcastMessage(any());
        verify(cahBroadcastMessageRepository).saveAndFlush(any());

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getResponseType()).isEqualTo(SUCCESS);
                    assertThat(response.getStatusMessage())
                            .isEqualTo("Message has been successfully created & scheduled.");
                    assertThat(response.getMessages()).isNotNull();

                    DynamicMessageResponse.Message message = response.getMessages().get(0);
                    assertThat(message.getMessageCode()).isEqualTo(
                            MessageCodesConstants.BROADCAST_MESSAGE_SCHEDULED_SUCCESS_CODE);
                })
                .verifyComplete();
    }

    /**
     * Method under test:
     * {@link CAHOperationalSupportServiceImpl#saveBroadcastMessage(SaveBroadcastMsgRequest, String)}
     */
    @Test
    void testSaveBroadcastMessageWithDraftStatusShouldReturnDynamicResponse() {
        String templateStatus = "draft";
        when(cahBroadcastMessageRepository.findIdByTemplateStatusName(templateStatus))
                .thenReturn(Optional.of(1));

        List<String> localeCode = new ArrayList<>();
        localeCode.add("en_US");
        localeCode.add("es_ES");

        // Mocking the behavior of the repository and service client

        when(digitalLanguageRefRepository.fetchAllLocaleCodeByLanguageEnabledFlag()).thenReturn(localeCode);

        when(cahBroadcastMessageRepository.saveAndFlush(any(BroadcastMessage.class)))
                .thenReturn(BroadcastMessage.builder().messageId(1L).build());

        // Call the method
        Mono<DynamicMessageResponse> responseMono = cahOperationsService
                .saveBroadcastMessage(saveBroadcastMsgRequest, templateStatus);

        // Verify interactions
        verify(brandingServiceClient).saveBroadcastMessage(any());
        verify(cahBroadcastMessageRepository).saveAndFlush(any());

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getResponseType()).isEqualTo(SUCCESS);
                    assertThat(response.getStatusMessage())
                            .isEqualTo("Message has been successfully saved as a draft.");
                    assertThat(response.getMessages()).isNotNull();

                    DynamicMessageResponse.Message message = response.getMessages().get(0);
                    assertThat(message.getMessageCode()).isEqualTo(
                            MessageCodesConstants.BROADCAST_MESSAGE_DRAFT_SUCCESS_CODE);
                })
                .verifyComplete();
    }

    @Test
    void testSaveBroadcastMessageWithInvalidStatusShouldThrowInvalidBroadcastMessageIdException() {
        String templateStatus = "invalid_status";
        when(cahBroadcastMessageRepository.findIdByTemplateStatusName(templateStatus))
                .thenThrow(new InvalidBroadcastMessageIdException("Invalid broadcast message status."));
        // Test for invalid message status
        InvalidBroadcastMessageIdException exception = assertThrows(InvalidBroadcastMessageIdException.class, () -> {
            cahOperationsService.saveBroadcastMessage(saveBroadcastMsgRequest, templateStatus);
        });
        assertEquals("Invalid broadcast message status.", exception.getMessage());
    }

    /**
     * Method under test:
     * {@link CAHOperationalSupportServiceImpl#saveBroadcastMessage(SaveBroadcastMsgRequest, String)}
     */
    @Test
    void testSaveBroadcastMessageWhenDatabaseOperationFailsShouldThrowDatabaseException() {
        String templateStatus = "draft";
        when(cahBroadcastMessageRepository.findIdByTemplateStatusName(templateStatus))
                .thenReturn(Optional.of(2));
        when(cahBroadcastMessageRepository.saveAndFlush(any()))
                .thenThrow(new RuntimeException("Database error"));

        List<String> localeCode = new ArrayList<>();
        localeCode.add("en_US");
        localeCode.add("es_ES");

        // Mocking the behavior of the repository and service client

        when(digitalLanguageRefRepository.fetchAllLocaleCodeByLanguageEnabledFlag()).thenReturn(localeCode);

        DatabaseException exception = assertThrows(DatabaseException.class, () ->
                cahOperationsService.saveBroadcastMessage(saveBroadcastMsgRequest, templateStatus)
        );
        assertTrue(exception.getMessage().contains("Database error occurred while processing request."));
        verify(cahBroadcastMessageRepository).findIdByTemplateStatusName(templateStatus);

    }

    /**
     * Method under test:
     * {@link CAHOperationalSupportServiceImpl#updateBroadCastMessage(String, UpdateBroadcastMessageRequest)}
     */
    @Test
    void testSaveBroadcastMessageWhenBrandingServiceClientThrowsWebClientResponseException() {
        String templateStatus = "draft";
        when(cahBroadcastMessageRepository.findIdByTemplateStatusName(templateStatus))
                .thenReturn(Optional.of(1));
        when(cahBroadcastMessageRepository.saveAndFlush(any())).thenReturn(existingMessage);
        List<String> localeCode = new ArrayList<>();
        localeCode.add("en_US");
        localeCode.add("es_ES");

        // Mocking the behavior of the repository and service client

        when(digitalLanguageRefRepository.fetchAllLocaleCodeByLanguageEnabledFlag()).thenReturn(localeCode);
        doThrow(WebClientResponseException.class).when(brandingServiceClient)
                .saveBroadcastMessage(any());

        assertThrows(WebClientResponseException.class, () ->
                cahOperationsService.saveBroadcastMessage(saveBroadcastMsgRequest, templateStatus)
        );
    }

    /**
     * Method under test:
     * {@link CAHOperationalSupportServiceImpl#saveBroadcastMessage(SaveBroadcastMsgRequest, String)}
     */
    @Test
    void testSaveBroadcastMessageWhenDatabaseOperationFailsShouldThrowInvalidException() {
        String templateStatus = "draft";
        saveBroadcastMsgRequest.setBrodCastMessageContent(
                List.of(new BroadcastMessageContent("en_USS", "test")));

        when(cahBroadcastMessageRepository.findIdByTemplateStatusName(templateStatus))
                .thenReturn(Optional.of(1));

        List<String> localeCode = new ArrayList<>();
        localeCode.add("en_US");
        localeCode.add("es_ES");

        // Mocking the behavior of the repository and service client

        when(digitalLanguageRefRepository.fetchAllLocaleCodeByLanguageEnabledFlag()).thenReturn(localeCode);

        // Assert that exception is thrown
        assertThrows(InvalidLocalCodeException.class, () ->
                cahOperationsService.saveBroadcastMessage(saveBroadcastMsgRequest, templateStatus)
        );
    }

    @Test
    void testDeleteBroadcastMessagesSuccess() {
        // Setup test data
        String request = "1";
        Object messageDescription = new Object();
        Object[] row1 = {1, "Downtime",
                Timestamp.valueOf("2024-06-27 01:37:37.233"),
                Timestamp.valueOf("2024-07-08 01:37:37.233"), true, "delete", messageDescription};

        List<Object[]> broadcastMessages = new ArrayList<>();
        broadcastMessages.add(row1);

        when(cahBroadcastMessageRepository
                .getCountOfBroadcastMessagesForIdList(List.of(ONE_CONSTANT))).thenReturn(ONE_CONSTANT);
        when(brandingServiceClient.deleteBroadCastMessage(1)).thenReturn("SUCCESS");

        doNothing().when(cahBroadcastMessageRepository)
                .deleteBroadCastMessage(eq("delete"),
                        eq(List.of(1)), any(), anyString());

        // Call the method
        Mono<DynamicMessageResponse> result = cahOperationsService.deleteBroadcastMessages(request);

        verify(cahBroadcastMessageRepository, times(1))
                .deleteBroadCastMessage(anyString(), anyList(), any(), anyString());

        DynamicMessageResponse response = result.block();
        assertNotNull(response);
        DynamicMessageResponse.Message successMessage = response.getMessages().get(0);
        assertEquals(MessageCodesConstants.BROADCAST_MESSAGE_DELETE_SUCCESS_CODE, successMessage.getMessageCode());
        assertTrue(successMessage.getParams().isEmpty());
    }


    @Test
    void testDeleteBroadcastMessagesFailureShouldThrownByWebclient() {
        String request = "1,2";
        List<Integer> idList = Arrays.asList(1, 2);

        when(cahBroadcastMessageRepository.getCountOfBroadcastMessagesForIdList(idList))
                .thenReturn(1);

        assertThrows(InvalidBroadcastMessageIdException.class, () ->
                cahOperationsService.deleteBroadcastMessages(request));
    }

    @Test
    void testDeleteBroadcastMessagesShouldThrowDataAccessException() {
        String request = "1";

        when(cahBroadcastMessageRepository.getCountOfBroadcastMessagesForIdList(List.of(1)))
                .thenThrow(new DatabaseException("Database Error"));

        assertThrows(DataAccessException.class, () -> {
            cahOperationsService.deleteBroadcastMessages(request);
        });
    }

    @Test
    void testDeleteBroadcastMessagesShouldThrowInvalidBroadCastMessageIdException() {
        String request = "1,2";

        when(cahBroadcastMessageRepository.getCountOfBroadcastMessagesForIdList(List.of(1, 2)))
                .thenReturn(1);

        assertThrows(InvalidBroadcastMessageIdException.class, () -> {
            cahOperationsService.deleteBroadcastMessages(request);
        });
    }

    @Test
    void testSearchBroadcastMessagesFound() {
        String nameOrId = "test";
        List<Object[]> mockMessages = Arrays.asList(
                new Object[]{"1", "Message 1", "Active"},
                new Object[]{"2", "Message 2", "Inactive"}
        );

        when(cahBroadcastMessageRepository.getBroadcastMessagesByIdOrName("%" + nameOrId + "%"))
                .thenReturn(mockMessages);

        List<SearchBroadcastMessageResponse> result = cahOperationsService.searchBroadcastMessages(nameOrId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getMessageId());
        assertEquals("Message 1", result.get(0).getMessageName());
        assertEquals("Active", result.get(0).getStatus());
    }

    @Test
    void testSearchBroadcastMessagesEmptyOrNullNameOrIdThrowsException() {
        BroadcastMessageNotFound exception = assertThrows(
                BroadcastMessageNotFound.class,
                () -> cahOperationsService.searchBroadcastMessages("")
        );
        assertEquals("The search parameter cannot be null or empty.", exception.getMessage());
        verify(cahBroadcastMessageRepository, never()).getBroadcastMessagesByIdOrName(anyString());
    }
}