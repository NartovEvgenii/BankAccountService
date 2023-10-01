package org.nartov.exception;

public class NotFindBankAccountException extends BankAccountException {

    public NotFindBankAccountException(Long accountNumber) {
        super("BankAccount not found with accountNumber: "  + accountNumber);
    }

    public NotFindBankAccountException(Long accountNumber1, Long accountNumber2) {
        super("BankAccount not found with accountNumbers "  + accountNumber1 + " & " + accountNumber2);
    }
}
