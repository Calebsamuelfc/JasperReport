package com.itssc.jasperreport.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ViewContractInfo {
    private String email;
    private String sn;
    private String contractId;
    private String contractName;
    private String servdef;
    private String createDate;
}
