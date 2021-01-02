package com.bmk.bmkbookings.exception;

import com.bmk.bmkbookings.response.out.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity invalidStatusHandler(InvalidStatusException e){
        logger.info(e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("400", e.getMessage()));
    }

    @ExceptionHandler(UnauthorizedUserException.class)
    public ResponseEntity unauthorizedUserHandler(UnauthorizedUserException e){
        logger.info(e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("400", e.getMessage()));
    }

    @ExceptionHandler(ServiceNotAvailableException.class)
    public ResponseEntity serviceNotAvailable(ServiceNotAvailableException e){
        logger.info(e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("400", e.getMessage()));
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity jsonHadler(JsonProcessingException e){
        logger.info(e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("400", e.getMessage()));
    }

    @ExceptionHandler(MerchantDoesNotExistException.class)
    public ResponseEntity exceptionHandler(MerchantDoesNotExistException e) {
        logger.info(e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("400", e.getMessage()));
    }

    @ExceptionHandler(GenericException.class)
    public ResponseEntity exceptionHandler(GenericException e) {
        logger.info(e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("400", e.getMessage()));
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity internalServerError(Exception e){
//        logger.info(e);
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("500", "Internal Server Error"));
//    }
}