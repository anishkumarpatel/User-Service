package com.unisys.udb.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleBroadcastMessageResponse {
    private List<CAHBroadcastResponse> liveBroadcastMessages;
    private List<CAHBroadcastResponse> upcomingBroadcastMessages;
}

