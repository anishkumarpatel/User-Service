package com.unisys.udb.user.exception.cahoperationalsupportexception;

import lombok.Data;
import lombok.RequiredArgsConstructor;
@Data
@RequiredArgsConstructor
public class DatabaseException extends RuntimeException {
    public DatabaseException(String message) {
        super(message);
    }
}
