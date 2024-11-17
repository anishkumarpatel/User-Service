package com.unisys.udb.user.service;

import com.unisys.udb.user.dto.request.SaveBroadcastMsgRequest;
import com.unisys.udb.user.dto.request.UpdateBroadcastMessageRequest;
import com.unisys.udb.user.dto.request.WithdrawBroadcastMsgRequest;
import com.unisys.udb.user.dto.response.CAHBroadcastMessageDetailsResponse;
import com.unisys.udb.user.dto.response.CAHBroadcastResponse;
import com.unisys.udb.user.dto.response.ScheduleBroadcastMessageResponse;
import com.unisys.udb.user.dto.response.SearchBroadcastMessageResponse;
import com.unisys.udb.utility.linkmessages.dto.DynamicMessageResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CAHOperationalSupportService {
    List<CAHBroadcastResponse> getBroadCastMessagesInDraft();

    List<CAHBroadcastResponse> getBroadCastMessagesWithStatusWithdrawn();

    List<CAHBroadcastResponse> getCompletedBroadcastMessages();

    Mono<DynamicMessageResponse> withdrawLiveBroadcastMessages(WithdrawBroadcastMsgRequest request);

    ScheduleBroadcastMessageResponse getLiveAndUpcomingBroadcastMessages();

    CAHBroadcastMessageDetailsResponse getBroadcastMessagesById(Integer id);

    DynamicMessageResponse updateBroadCastMessage(
            String templateStatus, UpdateBroadcastMessageRequest broadcastMessageRequest);

    Mono<DynamicMessageResponse> saveBroadcastMessage(SaveBroadcastMsgRequest request, String messageStatus);
    Mono<DynamicMessageResponse> deleteBroadcastMessages(String request);
    List<SearchBroadcastMessageResponse> searchBroadcastMessages(String nameOrId);
}
