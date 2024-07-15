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
@RequestMapping("/JasperReports/api/")
public class CombinedStatementController {
    private final CombinedStatementReceiptService combinedStatementReceiptService;

    @PostMapping("v1/downloadCombinedStatement")
    public ResponseEntity<DefaultResponse> downloadCombinedStatementReceiptV1(
            @RequestHeader("legalEntityId") String legalEntityId,
            @RequestBody CombinedStatementRequestDTO combinedStatementRequestDTO) {

        combinedStatementRequestDTO.setLegalEntityId(legalEntityId);
        return Responder.success(combinedStatementReceiptService.downloadCombinedStatementReceipt(combinedStatementRequestDTO));
    }

    @PostMapping("/v2/downloadCombinedStatement")
    public ResponseEntity<DefaultResponse> downloadCombinedStatementReceiptV2(
            @RequestHeader("legalEntityId") String legalEntityId,
            @RequestBody CombinedStatementRequestDTO combinedStatementRequestDTO) {

        combinedStatementRequestDTO.setLegalEntityId(legalEntityId);
        return Responder.success(combinedStatementReceiptService.downloadCombinedStatementReceiptMobile(combinedStatementRequestDTO));
    }
}
