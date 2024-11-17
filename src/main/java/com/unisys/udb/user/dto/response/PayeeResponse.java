package com.unisys.udb.user.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayeeResponse {
    private UUID digitalCustomerProfileId;
    private String payeeName;
    private String payeeBankName;
    private String payeeAccountNumber;
    private String payeeNickname;
    private String payeeBankCode;
    private String payeeReference;
    private String response;
    private Integer httpStatusCode;
}
