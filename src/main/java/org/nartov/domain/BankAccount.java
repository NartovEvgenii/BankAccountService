package org.nartov.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table
@Getter
@Setter
public class BankAccount {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idBankAccount;

    @Column
    private Long accountNumber;

    @Column
    private String name;

    @Column
    private String pinCode;

    @Column
    private BigDecimal balance;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccount that = (BankAccount) o;
        return Objects.equals(idBankAccount, that.idBankAccount) && Objects.equals(accountNumber, that.accountNumber) && Objects.equals(name, that.name) && Objects.equals(pinCode, that.pinCode) && Objects.equals(balance, that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idBankAccount, accountNumber, name, pinCode, balance);
    }
}
