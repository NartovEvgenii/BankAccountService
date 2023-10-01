package org.nartov.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class BankAccountDTO extends BankAccountSimpleDTO{

    private Long idBankAccount;

    private Long accountNumber;

    private String pinCode;

}
