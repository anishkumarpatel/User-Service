package com.unisys.udb.user.dto.response;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayeeFailedResponse {
     private String response;
     private Integer httpStatusCode;
}
