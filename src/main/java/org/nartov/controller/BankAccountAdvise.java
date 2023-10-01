package org.nartov.controller;

import org.nartov.exception.BankAccountException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BankAccountAdvise {

    @ExceptionHandler(BankAccountException.class)
    public ResponseEntity<String> handleException(BankAccountException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
    }

}

