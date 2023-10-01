package org.nartov.repository;

import org.nartov.domain.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    Optional<BankAccount> findByAccountNumber(Long accountNumber);

    @Query("select ba from BankAccount ba where ba.accountNumber in :accNumbers")
    List<BankAccount> findAllByAccountNumber(@Param("accNumbers") Iterable<Long> accNumbers);
}
