package com.itssc.jasperreport.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class KVTableInfo {
    private String key;
    private  String value;
}
