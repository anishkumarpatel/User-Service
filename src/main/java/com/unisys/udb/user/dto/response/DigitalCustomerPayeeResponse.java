package com.unisys.udb.user.dto.response;


import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DigitalCustomerPayeeResponse {

     private Integer digitalCustomerPayeeId;

     private UUID digitalCustomerProfileId;

     private String payeeName;
     private String payeeBankName;
     private String payeeAccountNumber;

     private String payeeNickName;

     private String payeeBankCode;

     private String payeeReference;

     private String payeeCreationDate;

}
