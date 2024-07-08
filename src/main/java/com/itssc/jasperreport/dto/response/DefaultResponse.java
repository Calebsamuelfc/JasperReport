package com.itssc.jasperreport.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DefaultResponse<T> {
    private String message;

    private boolean status;

    private T data;

    public DefaultResponse(String message, boolean status) {
        this.message = message;
        this.status = status;
    }
}
