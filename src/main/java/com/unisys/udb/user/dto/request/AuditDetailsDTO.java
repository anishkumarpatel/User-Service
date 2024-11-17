package com.unisys.udb.user.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
@Builder
public class AuditDetailsDTO {
    private Integer digitalCustomerStatusTypeRefId;
    private String digitalAccountStatusReason;
}
