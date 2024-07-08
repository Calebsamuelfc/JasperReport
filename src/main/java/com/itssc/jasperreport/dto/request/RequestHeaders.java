package com.itssc.jasperreport.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestHeaders {
    private String authenticationID;
    private String applicationID;
    private String APIKey;
    private String legalEntityId;
    private String hash;
}
