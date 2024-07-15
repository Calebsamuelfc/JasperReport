package com.itssc.jasperreport.controller;

import com.itssc.jasperreport.dto.request.CombinedStatementRequestDTO;
import com.itssc.jasperreport.dto.request.RequestHeaders;
import com.itssc.jasperreport.dto.response.DefaultResponse;
import com.itssc.jasperreport.service.api.CombinedStatementReceiptService;
import com.itssc.jasperreport.utils.Responder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/JasperReports/api/v1")
public class CombinedStatementController {
    private final CombinedStatementReceiptService combinedStatementReceiptService;

    @PostMapping("/downloadCombinedStatement")
    public ResponseEntity<DefaultResponse> downloadCombinedStatementReceipt(
            @RequestHeader("authenticationID") String authenticationID,
            @RequestHeader("applicationID") String applicationID,
            @RequestHeader("APIKey") String clientAPIKey,
            @RequestHeader("legalEntityId") String legalEntityId,
            @RequestHeader("hash") String hash,
            @RequestBody CombinedStatementRequestDTO combinedStatementRequestDTO) {

            RequestHeaders headers = new RequestHeaders();
            headers.setAuthenticationID(authenticationID);
            headers.setApplicationID(applicationID);
            headers.setAPIKey(clientAPIKey);
            headers.setLegalEntityId(legalEntityId);
            headers.setHash(hash);
            combinedStatementRequestDTO.setLegalEntityId(legalEntityId);
            return Responder.success(combinedStatementReceiptService.downloadCombinedStatementReceipt(combinedStatementRequestDTO));
    }
}
