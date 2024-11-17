package com.unisys.udb.user.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDetailsResponse {
    private List<CustomerDetail> customerDetails;
}
