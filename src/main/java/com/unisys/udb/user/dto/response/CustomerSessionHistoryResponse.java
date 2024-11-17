package com.unisys.udb.user.dto.response;

import com.unisys.udb.user.entity.CustomerSessionHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerSessionHistoryResponse {
    private List<CustomerSessionHistory> customerSessionHistory;
}

