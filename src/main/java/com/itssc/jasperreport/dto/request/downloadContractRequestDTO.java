package com.itssc.jasperreport.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class downloadContractRequestDTO {
    private String base64String;
    private String title;
    private String name;
    private String accountNumber;
    private String branch;
    private String email;
    private String telephone;
    private String address;
    private String fromDate;
    private String toDate;
    private String bankName;
    private String generatedOn;
    private String generatedBy;
    private String userId;
    private String closingBalance;
    private String startDate;
    private String endDate;
    private String fileFormat;
    private String legalEntityId;
}
