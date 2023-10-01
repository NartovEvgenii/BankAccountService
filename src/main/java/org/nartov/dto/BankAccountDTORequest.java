package org.nartov.dto;

import lombok.Data;

@Data
public class BankAccountDTORequest {

    private String name;

    private Integer pinCode;
}
