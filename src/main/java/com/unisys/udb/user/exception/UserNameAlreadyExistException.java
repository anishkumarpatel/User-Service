package com.unisys.udb.user.exception;

public class UserNameAlreadyExistException extends RuntimeException {


    public UserNameAlreadyExistException(String userName) {

        super("Duplicate user found: " + userName);

    }

}

