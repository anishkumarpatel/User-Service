package com.unisys.udb.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayeeRequest {
    private UUID digitalCustomerProfileId;
    private String payeeName;
    private String payeeBankName;
    private String payeeAccountNumber;
    private String payeeNickname;
    private String payeeBankCode;
    private String payeeReference;
}
