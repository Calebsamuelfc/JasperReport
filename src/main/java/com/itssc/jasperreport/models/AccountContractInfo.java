package com.itssc.jasperreport.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class AccountContractInfo {
    private String accountName;
    private String accountStatus;
    private String contractId;
    private String accountType;
    private String accountId;
    private String sn;
    private String coreCustomerId;
}
