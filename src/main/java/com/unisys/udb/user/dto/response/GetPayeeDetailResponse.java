package com.unisys.udb.user.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class GetPayeeDetailResponse {
     private Integer digitalCustomerPayeeId;

     private String payeeName;
     private String payeeBankName;
     private String payeeBankCode;
     private String payeeAccountNumber;

     private String payeeNickName;

     private String payeeReference;
     private List<GetPayeeTransactions> payeeTransactions;

}
