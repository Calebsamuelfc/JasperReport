package com.itssc.jasperreport.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class BulkUserInfo {
    private String lastName;
    private String userId;
    private String firstName;
    private String contractId;
    private String sn;
    private String status;
    private String customerId;
    private String legalEntityId;
}
