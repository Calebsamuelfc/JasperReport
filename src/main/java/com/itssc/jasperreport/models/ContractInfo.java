package com.itssc.jasperreport.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ContractInfo {
    private String email;
    private String sn;
    private String contractType;
    private String contractId;
    private String createDate;
    private String contractName;
    private String coreCustomerId;
}
