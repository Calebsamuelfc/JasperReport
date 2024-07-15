package com.itssc.jasperreport.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class NewCombinedTransactionInfo {
    private String balance;
    private String accountNumber;
    private String date;
    private String description;
    private String transactionId;
    private String withdrawal;
    private String deposit;
}
