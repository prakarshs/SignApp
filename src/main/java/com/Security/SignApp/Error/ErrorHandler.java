package com.Security.SignApp.Error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {
    public ResponseEntity<ErrorBody> customError(CustomError exception){
        return new ResponseEntity<>(ErrorBody.builder()
                .text(":: MISHTAKE HUI GAWA ::")
                .message(exception.getMessage())
                .resolution(exception.getResolution())
                .build(), HttpStatus.NOT_FOUND);
    }
}
