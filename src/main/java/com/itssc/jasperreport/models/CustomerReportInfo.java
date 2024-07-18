package com.itssc.jasperreport.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CustomerReportInfo {
    private String email;
    private String sn;
    private String name;
    private String userName;
    private String phoneNumber;
    private String customerType;
    private String customerStatus;
    private String enrollmentDate;
}
