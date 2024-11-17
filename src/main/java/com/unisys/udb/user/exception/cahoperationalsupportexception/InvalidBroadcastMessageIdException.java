package com.unisys.udb.user.exception.cahoperationalsupportexception;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class InvalidBroadcastMessageIdException extends RuntimeException {

    public InvalidBroadcastMessageIdException(String message) {
        super(message);
    }
}
