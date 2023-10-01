package org.nartov.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BankAccountSimpleDTO {
    private String name;

    private BigDecimal balance;
}
