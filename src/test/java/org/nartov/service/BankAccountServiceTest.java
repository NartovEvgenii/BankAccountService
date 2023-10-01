package org.nartov.service;

import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.nartov.domain.BankAccount;
import org.nartov.dto.BankAccountDTO;
import org.nartov.dto.BankAccountDTORequest;
import org.nartov.exception.BankAccountException;
import org.nartov.exception.NotFindBankAccountException;
import org.nartov.repository.BankAccountRepository;
import org.nartov.utils.AccountNumberGenerator;
import org.nartov.utils.HashUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BankAccountServiceTest {

    @Autowired
    private BankAccountService bankAccountService;

    @MockBean
    private BankAccountRepository bankAccountRepository;
    @MockBean
    private HashUtils hashUtils;
    @MockBean
    private AccountNumberGenerator accountNumberGenerator;

    @Test
    public void createAccountTest() {
        BankAccountDTORequest bankAccountDTORequest = new BankAccountDTORequest();

        Mockito.when(hashUtils.hashPinCode(anyInt()))
                .thenReturn("HASH");
        Mockito.when(accountNumberGenerator.getNextAccountNumber())
                .thenReturn(1L);
        Mockito.when(bankAccountRepository.save(any()))
                .thenReturn(new BankAccount());

        BankAccountDTO bankAccountDTO = bankAccountService.createAccount(bankAccountDTORequest);
        Assert.assertNotNull(bankAccountDTO);
        Mockito.verify(bankAccountRepository,Mockito.times(1)).save(any());
    }


    @Test
    public void depositAccount() throws NotFindBankAccountException {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(BigDecimal.valueOf(100));

        Mockito.when(bankAccountRepository.findByAccountNumber(any()))
                .thenReturn(Optional.of(bankAccount));
        Mockito.when(bankAccountRepository.save(any()))
                .thenReturn(new BankAccount());

        bankAccountService.depositAccount(1L, BigDecimal.valueOf(100));

        Assert.assertEquals(bankAccount.getBalance(),BigDecimal.valueOf(200));
        Mockito.verify(bankAccountRepository,Mockito.times(1)).save(bankAccount);
    }


    @Test
    public void withdrawAccount() throws BankAccountException {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(BigDecimal.valueOf(100));

        Mockito.when(bankAccountRepository.findByAccountNumber(any()))
                .thenReturn(Optional.of(bankAccount));
        Mockito.when(bankAccountRepository.save(any()))
                .thenReturn(new BankAccount());
        Mockito.when(hashUtils.checkPinCode(1, null))
                .thenReturn(true);

        bankAccountService.withdrawAccount(1L, BigDecimal.valueOf(100),1);

        Assert.assertEquals(bankAccount.getBalance(),BigDecimal.ZERO);
        Mockito.verify(bankAccountRepository,Mockito.times(1)).save(bankAccount);
    }

    @Test
    public void transferBalanceAccount() throws BankAccountException {
        BankAccount bankAccount1 = new BankAccount();
        bankAccount1.setAccountNumber(1L);
        bankAccount1.setBalance(BigDecimal.valueOf(100));

        bankAccount1.setPinCode("TESTHASH");

        BankAccount bankAccount2 = new BankAccount();
        bankAccount2.setAccountNumber(2L);
        bankAccount2.setBalance(BigDecimal.valueOf(100));

        Mockito.when(bankAccountRepository.findAllByAccountNumber(List.of(1L,2L)))
                .thenReturn(List.of(bankAccount1, bankAccount2));

        Mockito.when(hashUtils.checkPinCode(1234, "TESTHASH"))
                .thenReturn(true);

        bankAccountService.transferBalanceAccount(1L,2L,BigDecimal.valueOf(100),1234);

        Assert.assertEquals(bankAccount1.getBalance(),BigDecimal.ZERO);
        Assert.assertEquals(bankAccount2.getBalance(),BigDecimal.valueOf(200));
        Mockito.verify(bankAccountRepository,Mockito.times(1)).saveAll(any());
    }
}
