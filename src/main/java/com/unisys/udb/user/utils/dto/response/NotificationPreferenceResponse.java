package com.unisys.udb.user.utils.dto.response;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class NotificationPreferenceResponse {
    private String status;
    private String message;
    private HttpStatus httpStatus;
    private Date timeStamp;
}