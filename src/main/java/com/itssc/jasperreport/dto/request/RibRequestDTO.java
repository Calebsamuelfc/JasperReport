package com.itssc.jasperreport.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RibRequestDTO {
    private String fullname;
    private String iban;
    private String rib;
    private String domiciliation;
    private String currency;
    private String legalEntityId;
}
