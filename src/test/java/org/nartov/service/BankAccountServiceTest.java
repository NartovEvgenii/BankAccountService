package org.nartov.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.nartov.domain.BankAccount;
import org.nartov.dto.BankAccountDTO;
import org.nartov.dto.BankAccountDTORequest;
import org.nartov.exception.BankAccountException;
import org.nartov.exception.NotFindBankAccountException;
import org.nartov.repository.BankAccountRepository;
import org.nartov.utils.AccountNumberGenerator;
import org.nartov.utils.HashUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BankAccountServiceTest {

    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    @Mock
    private BankAccountRepository bankAccountRepository;
    @Mock
    private HashUtils hashUtils;
    @Mock
    private AccountNumberGenerator accountNumberGenerator;

    @Test
    public void createAccountTest() {
        BankAccountDTORequest bankAccountDTORequest = new BankAccountDTORequest();

        when(accountNumberGenerator.getNextAccountNumber())
                .thenReturn(1L);
        when(bankAccountRepository.save(any()))
                .thenReturn(new BankAccount());

        BankAccountDTO bankAccountDTO = bankAccountService.createAccount(bankAccountDTORequest);
        assertNotNull(bankAccountDTO);
        verify(bankAccountRepository, times(1)).save(any());
    }


    @Test
    public void depositAccount() throws NotFindBankAccountException {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(BigDecimal.valueOf(100));

        when(bankAccountRepository.findByAccountNumber(1L))
                .thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.save(any()))
                .thenReturn(new BankAccount());

        bankAccountService.depositToAccount(1L, BigDecimal.valueOf(100));

        assertEquals(bankAccount.getBalance(), BigDecimal.valueOf(200));
        verify(bankAccountRepository, times(1)).save(bankAccount);
    }


    @Test
    public void withdrawAccount() throws BankAccountException {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(BigDecimal.valueOf(100));

        when(bankAccountRepository.findByAccountNumber(1L))
                .thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.save(any()))
                .thenReturn(new BankAccount());
        when(hashUtils.checkPinCode(1234, null))
                .thenReturn(true);

        bankAccountService.withdrawFromAccount(1L, BigDecimal.valueOf(100), 1234);

        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);
        verify(bankAccountRepository, times(1)).save(bankAccount);
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

        when(bankAccountRepository.findAllByAccountNumber(List.of(1L, 2L)))
                .thenReturn(List.of(bankAccount1, bankAccount2));

        when(hashUtils.checkPinCode(1234, "TESTHASH"))
                .thenReturn(true);

        bankAccountService.transferBalanceAccount(1L, 2L, BigDecimal.valueOf(100), 1234);

        assertEquals(bankAccount1.getBalance(), BigDecimal.ZERO);
        assertEquals(bankAccount2.getBalance(), BigDecimal.valueOf(200));
        verify(bankAccountRepository, times(1)).saveAll(any());
    }
}
