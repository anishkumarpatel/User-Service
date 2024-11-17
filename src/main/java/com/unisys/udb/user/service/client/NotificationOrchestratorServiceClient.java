package com.unisys.udb.user.service.client;

import com.unisys.udb.user.dto.request.NotificationOrchestratorRequest;
import com.unisys.udb.user.dto.response.NotificationOrchestratorResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange
public interface NotificationOrchestratorServiceClient {
    @PostExchange("api/v1/notificationorchestrator/publish/notification")
    NotificationOrchestratorResponse publishNotification(@RequestBody NotificationOrchestratorRequest request);
}
