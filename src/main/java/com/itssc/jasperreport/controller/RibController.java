package com.itssc.jasperreport.controller;

import com.itssc.jasperreport.dto.request.RequestHeaders;
import com.itssc.jasperreport.dto.request.RibRequestDTO;
import com.itssc.jasperreport.dto.response.DefaultResponse;
import com.itssc.jasperreport.service.api.RibService;
import com.itssc.jasperreport.utils.Responder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/JasperReports/api/v1")
public class RibController {

    private final RibService ribService;

    @GetMapping("/getRibData")
    public ResponseEntity<DefaultResponse> downloadRib(
            @RequestHeader("authenticationID") String authenticationID,
            @RequestHeader("applicationID") String applicationID,
            @RequestHeader("APIKey") String clientAPIKey,
            @RequestHeader("legalEntityId") String legalEntityId,
            @RequestParam("fullname") String fullname,
            @RequestParam("iban") String iban,
            @RequestParam("rib") String rib,
            @RequestParam("domiciliation") String domiciliation,
            @RequestParam("currency") String currency,
            @RequestParam("hash") String hash) {

        RequestHeaders headers = new RequestHeaders();
        headers.setAuthenticationID(authenticationID);
        headers.setApplicationID(applicationID);
        headers.setAPIKey(clientAPIKey);
        headers.setLegalEntityId(legalEntityId);
        headers.setHash(hash);

        // Populate the request DTO
        RibRequestDTO ribRequest = new RibRequestDTO();
        ribRequest.setFullname(fullname);
        ribRequest.setIban(iban);
        ribRequest.setRib(rib);
        ribRequest.setDomiciliation(domiciliation);
        ribRequest.setCurrency(currency);
        ribRequest.setLegalEntityId(legalEntityId);


        return Responder.success(ribService.downloadRib(headers, ribRequest));
    }
}
