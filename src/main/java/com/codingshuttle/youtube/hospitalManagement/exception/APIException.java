package com.codingshuttle.youtube.hospitalManagement.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class APIException {

    private LocalDateTime localDateTime;
    private String message;
    private HttpStatus httpStatus;

    public APIException(){
        this.localDateTime = LocalDateTime.now();
    }

    public APIException(String message, HttpStatus httpStatus){
        this();
        this.message = message;
        this.httpStatus = httpStatus;
    }


}
