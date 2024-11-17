package com.unisys.udb.user.dto.response;

import lombok.*;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Getter
@Setter
public class TermsConditionsAndCookieResponse {

    private  String message;

    private  HttpStatus status;

    private  LocalDateTime timestamp;

}
