package com.unisys.udb.user.service.client;

import com.unisys.udb.user.dto.request.BroadcastMessageContentRequest;
import com.unisys.udb.user.dto.request.BroadcastMessageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

@HttpExchange
public interface BrandingServiceClient {
    @GetExchange("api/v1/cah/broadcastMessage/{messageCode}")
    Object getBroadcastMessageDescription(@PathVariable Integer messageCode) throws WebClientResponseException;

    @PutExchange("api/v1/cah/broadcastMessage/{messageCode}")
    Object updateBroadCastMessage(
            @PathVariable Integer messageCode, @RequestBody BroadcastMessageRequest updatedContent)
            throws WebClientResponseException;

    @PostExchange("/api/v1/cah/broadcastMessage")
    Object saveBroadcastMessage(@RequestBody BroadcastMessageContentRequest request) throws WebClientResponseException;

    @DeleteExchange("/api/v1/cah/broadcastMessage/{messageCode}")
    Object deleteBroadCastMessage(@PathVariable Integer messageCode) throws WebClientResponseException;

}
