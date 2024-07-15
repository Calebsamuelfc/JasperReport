package com.itssc.jasperreport.models;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class TransactionInfo {
    private String transactionId;
    private String date;
    private String bookingDate;
    private String description;
    private String withdrawal;
    private String deposit;
    private String balance;
}
