package com.itssc.jasperreport.utils;


import com.itssc.jasperreport.dto.response.DefaultResponse;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.*;

public class Responder {

    public static ResponseEntity<DefaultResponse> success(Object response){
        return new ResponseEntity<>(new DefaultResponse("Successful", true, response), OK);
    }

}
