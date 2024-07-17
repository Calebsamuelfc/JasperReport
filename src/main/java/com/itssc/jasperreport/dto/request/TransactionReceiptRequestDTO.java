package com.itssc.jasperreport.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionReceiptRequestDTO {
    private String transactionDate;
    private String sender;
    private String transactionReference;
    private String transactionAmount;
    private String description;
    private String account;
    private String beneficiary;
    private String bank;
    private String legalEntityId;
}
