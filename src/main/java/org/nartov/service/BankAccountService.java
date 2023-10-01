package org.nartov.service;

import org.nartov.dto.BankAccountDTO;
import org.nartov.dto.BankAccountDTORequest;
import org.nartov.dto.BankAccountSimpleDTO;
import org.nartov.exception.BankAccountException;
import org.nartov.exception.NotFindBankAccountException;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;


public interface BankAccountService {

    BankAccountDTO createAccount(BankAccountDTORequest bankAccountDTORequest);
    List<BankAccountDTO> getAllAccounts();
    List<BankAccountSimpleDTO> getAllSimpleAccounts();
    BankAccountDTO depositAccount(Long accountNumber, BigDecimal depositSum) throws NotFindBankAccountException;
    BankAccountDTO withdrawAccount( Long accountNumber, BigDecimal withdrawSum, Integer pinCode) throws BankAccountException;

    void transferBalanceAccount(Long senderAccountNumber,
                                Long receiverAccountNumber,
                                BigDecimal transferSum,
                                Integer senderPinCode) throws BankAccountException;
}
