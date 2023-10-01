package org.nartov.exception;

public class IncorrectPinCodeException extends BankAccountException{
    public IncorrectPinCodeException() {
        super("Incorrect PIN code for account");
    }
}
