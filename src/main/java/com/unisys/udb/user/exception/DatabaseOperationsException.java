package com.unisys.udb.user.exception;




public class DatabaseOperationsException extends RuntimeException {

    public DatabaseOperationsException(String message, String digitalDeviceUdid) {
        super(message + digitalDeviceUdid);
    }


    public DatabaseOperationsException(String message) {
        super(message);
    }



}
