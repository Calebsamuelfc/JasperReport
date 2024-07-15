package com.itssc.jasperreport.controller;

import com.itssc.jasperreport.dto.request.RequestHeaders;
import com.itssc.jasperreport.dto.request.SingleStatementRequestDTO;
import com.itssc.jasperreport.dto.response.DefaultResponse;
import com.itssc.jasperreport.service.api.SingleStatementService;
import com.itssc.jasperreport.utils.Responder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/JasperReports/api/v1")
public class SingleStatementController {
    private final SingleStatementService singleStatementService;

    @PostMapping("/downloadSingleStatement")
    public ResponseEntity<DefaultResponse> downloadSingleStatement(
            @RequestHeader("authenticationID") String authenticationID,
            @RequestHeader("applicationID") String applicationID,
            @RequestHeader("APIKey") String clientAPIKey,
            @RequestHeader("legalEntityId") String legalEntityId,
            @RequestHeader("hash") String hash,
            @RequestBody SingleStatementRequestDTO singleStatementRequestDTO) {

        RequestHeaders headers = new RequestHeaders();
        headers.setAuthenticationID(authenticationID);
        headers.setApplicationID(applicationID);
        headers.setAPIKey(clientAPIKey);
        headers.setLegalEntityId(legalEntityId);
        headers.setHash(hash);

        singleStatementRequestDTO.setLegalEntityId(legalEntityId);
        return Responder.success(singleStatementService.downloadSingleStatement(singleStatementRequestDTO));
    }
}
