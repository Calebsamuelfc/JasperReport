package com.itssc.jasperreport.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RibResponse {
    private String responseCode;
    private String responseStatus;
    private String base64String;
    private String fileFormat;
    private String title;
}
