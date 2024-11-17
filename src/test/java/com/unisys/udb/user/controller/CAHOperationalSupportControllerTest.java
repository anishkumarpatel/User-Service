package com.unisys.udb.user.controller;


import com.unisys.udb.user.constants.MessageCodesConstants;
import com.unisys.udb.user.constants.UdbConstants;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.unisys.udb.user.constants.UdbConstants.OK_RESPONSE_CODE;
import static com.unisys.udb.user.constants.UdbConstants.ONE_CONSTANT;
import static com.unisys.udb.user.constants.UdbConstants.SUCCESS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

class CAHOperationalSupportControllerTest {

    @Mock
    private CAHOperationalSupportService cahOperationalSupportService;

    @InjectMocks
    private CAHOperationalSupportController cahOperationalSupportController;
    private SaveBroadcastMsgRequest saveBroadcastMsgRequest;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        saveBroadcastMsgRequest = SaveBroadcastMsgRequest.builder()
                .accountType("all")
                .messageName("Test Message")
                .startDateTime("2024-09-20 14:30:00")
                .endDateTime("2024-09-20 15:30:00")
                .createdBy("User")
                .brodCastMessageContent(Collections.emptyList())
                .build();
    }

    /**
     * Method under test: {@link CAHOperationalSupportController #getAllBroadCastMessagesInDraft()}
     */
    @Test
    void testGetAllBroadCastMessagesInDraftForDraftsShouldReturnSuccessResponse() {

        // Arrange
        ArrayList<CAHBroadcastResponse> cahBroadcastResponseList = new ArrayList<>();
        when(cahOperationalSupportService.getBroadCastMessagesInDraft()).thenReturn(cahBroadcastResponseList);

        // Act and Assert
        StepVerifier.FirstStep<ResponseEntity<List<CAHBroadcastResponse>>> createResult = StepVerifier
                .create(cahOperationalSupportController.getAllBroadCastMessagesInDraft());

        createResult.assertNext(r -> {
            List<CAHBroadcastResponse> body = r.getBody();
            assert body != null;
            assertTrue(body.isEmpty());
            assertSame(cahBroadcastResponseList, body);
            assertTrue(r.getHeaders().isEmpty());
            assertTrue(r.hasBody());
        }).expectComplete().verify();

        verify(cahOperationalSupportService).getBroadCastMessagesInDraft();
    }

    @Test
    void testGetCompletedBroadcastMessagesShouldReturnListOfCompletedMessages() {
        // Arrange
        CAHBroadcastResponse expectedResponse = CAHBroadcastResponse.builder()
                .messageId("id")
                .messageName("name")
                .startDateAndTime("startDate")
                .endDateAndTime("endDate")
                .accountType("all")
                .build();

        List<CAHBroadcastResponse> responseList = Arrays.asList(expectedResponse);

        // Mock the service method
        when(cahOperationalSupportService.getCompletedBroadcastMessages()).thenReturn(responseList);

        List<CAHBroadcastResponse> response = cahOperationalSupportController.getCompletedBroadcastMessages().getBody();
        assertNotNull(response);
        assertEquals(expectedResponse.getMessageId(), response.get(0).getMessageId());
        assertEquals(response, responseList);
        verify(cahOperationalSupportService, times(1)).getCompletedBroadcastMessages();

    }

    /**
     * Method under test: {@link CAHOperationalSupportController #getAllBroadCastMessagesInWithdrawn}
     */
    @Test
    void testGetAllBroadCastMessagesInWithdrawnStateShouldReturnSuccessResponse() {

        // Arrange
        ArrayList<CAHBroadcastResponse> cahBroadcastResponseList = new ArrayList<>();
        when(cahOperationalSupportService.getBroadCastMessagesWithStatusWithdrawn())
                .thenReturn(cahBroadcastResponseList);

        // Act and Assert
        StepVerifier.FirstStep<ResponseEntity<List<CAHBroadcastResponse>>> createResult = StepVerifier
                .create(cahOperationalSupportController.getAllBroadCastMessagesInWithdrawn());

        createResult.assertNext(r -> {
            List<CAHBroadcastResponse> body = r.getBody();
            assert body != null;
            assertSame(cahBroadcastResponseList, body);
            assertTrue(r.getHeaders().isEmpty());
            assertTrue(r.hasBody());
        }).expectComplete().verify();

        verify(cahOperationalSupportService).getBroadCastMessagesWithStatusWithdrawn();
    }

    /**
     * Method under test: {@link CAHOperationalSupportController #withdrawLiveBroadcastMessages}
     */
    @Test
    void testWithdrawLiveBroadcastMessagesShouldReturnDynamicMessageResponse() {
        WithdrawBroadcastMsgRequest request = WithdrawBroadcastMsgRequest.builder()
                .messageIds(List.of(1, 2))
                .build();

        DynamicMessageResponse response = new DynamicMessageResponse(
                "SUCCESS",
                "Message has been successfully withdrawn.",
                List.of(new DynamicMessageResponse.Message(
                        MessageCodesConstants.BROADCAST_MESSAGE_WITHDRAW_SUCCESS_CODE,
                        Collections.emptyList()))
        );

        when(cahOperationalSupportService.withdrawLiveBroadcastMessages(request))
                .thenReturn(Mono.just(response));

        Mono<ResponseEntity<DynamicMessageResponse>> resultMono =
                cahOperationalSupportController.withdrawLiveBroadcastMessages(request);

        // Assert
        StepVerifier.create(resultMono)
                .assertNext(responseEntity -> {
                    assertThat(responseEntity).isNotNull();
                    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(responseEntity.getBody()).isNotNull();
                    assertThat(responseEntity.getBody().getResponseType()).isEqualTo("SUCCESS");
                    assertThat(responseEntity.getBody().getStatusMessage())
                            .isEqualTo("Message has been successfully withdrawn.");
                })
                .verifyComplete();
    }

    /**
     * Method under test: {@link CAHOperationalSupportController #getAllLiveAndUpcomingBroadcastMessages}
     */

    @Test
    void testShouldReturnLiveAndUpcomingBroadcastMessagesSuccessfully() {
        // Mocking the response from the service
        ScheduleBroadcastMessageResponse mockResponse = mockScheduleBroadcastMessageResponse();
        when(cahOperationalSupportService.getLiveAndUpcomingBroadcastMessages()).thenReturn(mockResponse);

        // Call the controller method
        Mono<ResponseEntity<ScheduleBroadcastMessageResponse>> responseMono =
                cahOperationalSupportController.getAllLiveAndUpcomingBroadcastMessages();

        // Verify the response
        ResponseEntity<ScheduleBroadcastMessageResponse> responseEntity = responseMono.block();
        assertNotNull(responseEntity);
        assertEquals(OK, responseEntity.getStatusCode()); // Assert that status is 200 OK
        assertNotNull(responseEntity.getBody());

        // Assert the contents of the response body
        ScheduleBroadcastMessageResponse responseBody = responseEntity.getBody();
        assertEquals(mockResponse, responseBody); // Assert that the body is the mock response

        // Verify that the service method was called once
        verify(cahOperationalSupportService, times(1)).getLiveAndUpcomingBroadcastMessages();
    }

    /**
     * Method under test: {@link CAHOperationalSupportController #getAllBroadCastMessagesInWithdrawn}
     */
    @Test
    void testGetBroadCastMessageByIdShouldReturnSuccessResponse() {

        // Arrange
        CAHBroadcastMessageDetailsResponse cahBroadcastMessageDetailsResponse =
                new CAHBroadcastMessageDetailsResponse();
        when(cahOperationalSupportService.getBroadcastMessagesById(ONE_CONSTANT))
                .thenReturn(cahBroadcastMessageDetailsResponse);

        // Act and Assert
        StepVerifier.FirstStep<ResponseEntity<CAHBroadcastMessageDetailsResponse>> createResult = StepVerifier
                .create(cahOperationalSupportController.getBroadcastMessageDetailsById(ONE_CONSTANT));

        createResult.assertNext(r -> {
            CAHBroadcastMessageDetailsResponse body = r.getBody();
            assert body != null;
            assertSame(cahBroadcastMessageDetailsResponse, body);
            assertTrue(r.getHeaders().isEmpty());
            assertTrue(r.hasBody());
        }).expectComplete().verify();

        verify(cahOperationalSupportService).getBroadcastMessagesById(ONE_CONSTANT);
    }

    // Helper method to mock ScheduleBroadcastMessageResponse
    private ScheduleBroadcastMessageResponse mockScheduleBroadcastMessageResponse() {
        List<CAHBroadcastResponse> liveBroadcastMessages = List.of(
                CAHBroadcastResponse.builder()
                        .messageId("1")
                        .messageName("Live Message")
                        .startDateAndTime("01-01-2024 10:00:00")
                        .endDateAndTime("01-01-2024 11:00:00")
                        .accountType("All")
                        .build()
        );

        List<CAHBroadcastResponse> upcomingBroadcastMessages = List.of(
                CAHBroadcastResponse.builder()
                        .messageId("2")
                        .messageName("Upcoming Message")
                        .startDateAndTime("01-02-2024 10:00:00")
                        .endDateAndTime("01-02-2024 11:00:00")
                        .accountType("All")
                        .build()
        );

        return ScheduleBroadcastMessageResponse.builder()
                .liveBroadcastMessages(liveBroadcastMessages)
                .upcomingBroadcastMessages(upcomingBroadcastMessages)
                .build();
    }

    @Test
    void updateBroadCastMessageWithValidInputAndScheduleStatusShouldUpdateSuccessfully() {
        String templateStatus = "DRAFT";
        UpdateBroadcastMessageRequest broadcastMessageRequest = new UpdateBroadcastMessageRequest();
        DynamicMessageResponse expectedResponse = new DynamicMessageResponse(
                UdbConstants.STATUS_MESSAGE_SUCCESSFUL,
                "Broadcast message updated successfully",
                Collections.emptyList());

        when(cahOperationalSupportService.updateBroadCastMessage(templateStatus, broadcastMessageRequest))
                .thenReturn(expectedResponse);

        ResponseEntity<DynamicMessageResponse> response =
                cahOperationalSupportController.updateBroadCastMessage(templateStatus, broadcastMessageRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void updateBroadCastMessageWithInvalidMessageStatusShouldThrowInvalidBroadCastMessageStatusException() {
        String invalidMessageStatus = "INVALID_STATUS";
        UpdateBroadcastMessageRequest broadcastMessageRequest = new UpdateBroadcastMessageRequest();

        InvalidBroadCastMessageStatusException exception = assertThrows(
                InvalidBroadCastMessageStatusException.class,
                () -> cahOperationalSupportController
                        .updateBroadCastMessage(invalidMessageStatus, broadcastMessageRequest)
        );
        assertEquals("Broadcast message status must be either 'Schedule' or 'Draft', "
                + "but found 'INVALID_STATUS'", exception.getMessage());
    }

    @Test
    void testSaveBroadcastMessageWithValidInputAndScheduleStatusShouldReturnDynamicResponse() {
        // Mock the service response
        DynamicMessageResponse expectedResponse = new DynamicMessageResponse(
                "SUCCESS",
                "Message has been successfully created & scheduled.",
                List.of(new DynamicMessageResponse.Message(
                        MessageCodesConstants.BROADCAST_MESSAGE_SCHEDULED_SUCCESS_CODE,
                        Collections.emptyList())));

        when(cahOperationalSupportService.saveBroadcastMessage(any(), any()))
                .thenReturn(Mono.just(expectedResponse));

        Mono<ResponseEntity<DynamicMessageResponse>> response =
                cahOperationalSupportController.saveBroadcastMessage("draft", saveBroadcastMsgRequest);



        StepVerifier.create(response)
                .assertNext(responseEntity -> {
                    assertThat(responseEntity).isNotNull();
                    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(responseEntity.getBody()).isNotNull();
                    assertThat(responseEntity.getBody().getResponseType()).isEqualTo("SUCCESS");
                    assertThat(responseEntity.getBody().getStatusMessage())
                            .isEqualTo("Message has been successfully created & scheduled.");
                })
                .verifyComplete();
    }

    @Test
    void testSaveBroadcastMessageWithInvalidMessageStatusShouldThrowInvalidBroadCastMessageStatusException() {
        String invalidMessageStatus = "INVALID_STATUS";

        InvalidBroadCastMessageStatusException exception = assertThrows(
                InvalidBroadCastMessageStatusException.class,
                () -> cahOperationalSupportController
                        .saveBroadcastMessage(invalidMessageStatus, saveBroadcastMsgRequest)
        );
        assertEquals("Broadcast message status must be either 'Schedule' or 'Draft', "
                + "but found 'INVALID_STATUS'", exception.getMessage());
    }
    @Test
    void testDeleteBroadCastMessagesSuccessShouldReturnDynamicMessageResponse() {
        String ids = "1,2,3";
        DynamicMessageResponse response = new DynamicMessageResponse(SUCCESS,
                "success",
                List.of(new DynamicMessageResponse.Message("msg", new ArrayList<>())));

        when(cahOperationalSupportService.deleteBroadcastMessages(ids))
                .thenReturn(Mono.just(response));

        Mono<ResponseEntity<DynamicMessageResponse>> result =
                cahOperationalSupportController.deleteBroadCastMessages(ids);

        StepVerifier.create(result)
                .expectNextMatches(entity -> entity.getStatusCode() == HttpStatus.OK
                        && "success".equals(Objects.requireNonNull(entity.getBody()).getResponseType()))
                .verifyComplete();
    }

    @Test
    void testSearchBroadcastMessage() {
        String nameOrId = "TestID";
        List<SearchBroadcastMessageResponse> mockResponseList = Collections.singletonList(
                SearchBroadcastMessageResponse.builder()
                        .messageId("123")
                        .messageName("Test Message")
                        .status("Active")
                        .build()
        );

        when(cahOperationalSupportService.searchBroadcastMessages(nameOrId)).thenReturn(mockResponseList);

        ResponseEntity<List<SearchBroadcastMessageResponse>> response =
                cahOperationalSupportController.searchBroadcastMessage(nameOrId);

        assertNotNull(response);
        assertEquals(OK_RESPONSE_CODE, response.getStatusCode().value());
        assertEquals(mockResponseList, response.getBody());
        verify(cahOperationalSupportService, times(1)).searchBroadcastMessages(nameOrId);
    }
}