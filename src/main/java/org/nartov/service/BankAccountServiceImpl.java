package org.nartov.service;

import lombok.RequiredArgsConstructor;
import org.nartov.domain.BankAccount;
import org.nartov.dto.BankAccountDTO;
import org.nartov.dto.BankAccountDTORequest;
import org.nartov.dto.BankAccountSimpleDTO;
import org.nartov.exception.BankAccountException;
import org.nartov.exception.IncorrectPinCodeException;
import org.nartov.exception.NotEnoughFundsException;
import org.nartov.exception.NotFindBankAccountException;
import org.nartov.repository.BankAccountRepository;
import org.nartov.utils.AccountNumberGenerator;
import org.nartov.utils.HashUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final HashUtils hashUtils;
    private final AccountNumberGenerator accountNumberGenerator;
    @Override
    public BankAccountDTO createAccount(BankAccountDTORequest bankAccountDTORequest) {
        BankAccount bankAccount = createBankAccountFromRequest(bankAccountDTORequest);
        bankAccount = bankAccountRepository.save(bankAccount);
        return mapBankAccountToDTO(bankAccount);
    }

    @Override
    public List<BankAccountDTO> getAllAccounts() {
        return bankAccountRepository.findAll().stream()
                .map(this::mapBankAccountToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BankAccountSimpleDTO> getAllSimpleAccounts() {
        return bankAccountRepository.findAll().stream()
                .map(this::mapBankAccountToSimpleDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BankAccountDTO depositToAccount(Long accountNumber, BigDecimal depositSum) throws NotFindBankAccountException {
        BankAccount bankAccount = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFindBankAccountException(accountNumber));
        increaseBalanceAccount(bankAccount, depositSum);
        bankAccount = bankAccountRepository.save(bankAccount);
        return mapBankAccountToDTO(bankAccount);
    }

    @Override
    public BankAccountDTO withdrawFromAccount(Long accountNumber, BigDecimal withdrawSum, Integer pinCode) throws BankAccountException {
        BankAccount bankAccount = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFindBankAccountException(accountNumber));
        decreaseBalanceAccount(bankAccount, pinCode, withdrawSum);
        bankAccountRepository.save(bankAccount);
        return mapBankAccountToDTO(bankAccount);
    }

    @Override
    @Transactional
    public void transferBalanceAccount(Long senderAccountNumber,
                                       Long receiverAccountNumber,
                                       BigDecimal transferSum,
                                       Integer senderPinCode) throws BankAccountException {
        Map<Long, BankAccount> bankAccounts = bankAccountRepository
                .findAllByAccountNumber(Arrays.asList(senderAccountNumber, receiverAccountNumber))
                .stream()
                .collect(Collectors.toMap(BankAccount::getAccountNumber, Function.identity()));

        if (!bankAccounts.containsKey(senderAccountNumber) || !bankAccounts.containsKey(receiverAccountNumber)) {
            throwBankAccountException(senderAccountNumber, receiverAccountNumber, bankAccounts);
        }

        decreaseBalanceAccount(bankAccounts.get(senderAccountNumber), senderPinCode, transferSum);
        increaseBalanceAccount(bankAccounts.get(receiverAccountNumber), transferSum);
        bankAccountRepository.saveAll(bankAccounts.values());
    }

    private static void throwBankAccountException(Long senderAccountNumber,
                                                   Long receiverAccountNumber,
                                                   Map<Long, BankAccount> bankAccounts) throws NotFindBankAccountException {
        if (!bankAccounts.containsKey(senderAccountNumber)) {
            throw new NotFindBankAccountException(senderAccountNumber);
        } else if (!bankAccounts.containsKey(receiverAccountNumber)) {
            throw new NotFindBankAccountException(receiverAccountNumber);
        } else {
            throw new NotFindBankAccountException(senderAccountNumber, receiverAccountNumber);
        }
    }


    private void decreaseBalanceAccount(BankAccount bankAccount, Integer pinCode, BigDecimal decreaseSum) throws BankAccountException {
        if (!hashUtils.checkPinCode(pinCode, bankAccount.getPinCode())) {
            throw new IncorrectPinCodeException();
        }

        if (bankAccount.getBalance().subtract(decreaseSum).compareTo(BigDecimal.ZERO) >= 0) {
            bankAccount.setBalance(bankAccount.getBalance().subtract(decreaseSum));
        } else {
            throw new NotEnoughFundsException();
        }
    }

    private void increaseBalanceAccount(BankAccount bankAccount, BigDecimal increaseSum) {
        bankAccount.setBalance(bankAccount.getBalance().add(increaseSum));
    }

    private BankAccountDTO mapBankAccountToDTO(BankAccount bankAccount) {
        BankAccountDTO bankAccountDTO = new BankAccountDTO();
        bankAccountDTO.setIdBankAccount(bankAccount.getIdBankAccount());
        bankAccountDTO.setAccountNumber(bankAccount.getAccountNumber());
        bankAccountDTO.setBalance(bankAccount.getBalance());
        bankAccountDTO.setName(bankAccount.getName());
        bankAccountDTO.setPinCode(bankAccount.getPinCode());
        return bankAccountDTO;
    }

    private BankAccountSimpleDTO mapBankAccountToSimpleDTO(BankAccount bankAccount) {
        BankAccountSimpleDTO bankAccountDTO = new BankAccountSimpleDTO();
        bankAccountDTO.setBalance(bankAccount.getBalance());
        bankAccountDTO.setName(bankAccount.getName());
        return bankAccountDTO;
    }

    private BankAccount createBankAccountFromRequest(BankAccountDTORequest bankAccountDTORequest) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setName(bankAccountDTORequest.getName());
        bankAccount.setPinCode(hashUtils.hashPinCode(bankAccountDTORequest.getPinCode()));
        bankAccount.setBalance(BigDecimal.ZERO);
        bankAccount.setAccountNumber(accountNumberGenerator.getNextAccountNumber());
        return bankAccount;
    }
}
