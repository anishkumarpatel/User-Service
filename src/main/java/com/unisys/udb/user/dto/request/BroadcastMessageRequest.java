package com.unisys.udb.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastMessageRequest {
    private List<BroadcastMessageContent> brodCastMessageContent;
    private String updatedBy;
}