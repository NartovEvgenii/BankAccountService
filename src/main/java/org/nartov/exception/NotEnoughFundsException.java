package org.nartov.exception;

public class NotEnoughFundsException extends BankAccountException{
    public NotEnoughFundsException() {
        super("Account does not have enough funds");
    }
}
