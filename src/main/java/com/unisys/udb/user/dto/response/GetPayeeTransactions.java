package com.unisys.udb.user.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetPayeeTransactions {
     private LocalDateTime trxDateTime;
     private String trxAmt;

     private String trxAcctNumber;

}
