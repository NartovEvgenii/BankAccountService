package org.nartov.controller;

import org.nartov.dto.BankAccountDTO;
import org.nartov.dto.BankAccountDTORequest;
import org.nartov.dto.BankAccountSimpleDTO;
import org.nartov.exception.BankAccountException;
import org.nartov.exception.NotFindBankAccountException;
import org.nartov.service.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/account")
public class BankAccountController {
    @Autowired
    private BankAccountService bankAccountService;

    @PostMapping()
    public void createAccount(@RequestBody BankAccountDTORequest bankAccountDTORequest){
        bankAccountService.createAccount(bankAccountDTORequest);
    }

    @GetMapping
    public List<BankAccountDTO> getAllAccounts(){
        return bankAccountService.getAllAccounts();
    }

    @GetMapping("/simple")
    public List<BankAccountSimpleDTO> getAllSimpleAccounts(){
        return bankAccountService.getAllSimpleAccounts();
    }

    @PostMapping("/deposit")
    public void  depositAccount(@RequestParam Long accountNumber,@RequestParam BigDecimal depositSum) throws NotFindBankAccountException {
        bankAccountService.depositAccount(accountNumber, depositSum);
    }

    @PostMapping("/withdraw")
    public void  withdrawAccount(@RequestParam Long accountNumber,@RequestParam BigDecimal withdrawSum,@RequestParam Integer pinCode) throws BankAccountException {
        bankAccountService.withdrawAccount(accountNumber, withdrawSum, pinCode);
    }

    @PostMapping("/transfer")
    public void  transferBalanceAccount(@RequestParam Long senderAccountNumber,
                                 @RequestParam Long receiverAccountNumber,
                                 @RequestParam BigDecimal transferSum,
                                 @RequestParam Integer senderPinCode) throws BankAccountException {
        bankAccountService.transferBalanceAccount(senderAccountNumber, receiverAccountNumber, transferSum, senderPinCode);
    }
}
