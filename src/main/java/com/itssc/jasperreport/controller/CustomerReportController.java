package com.itssc.jasperreport.controller;

import com.itssc.jasperreport.dto.request.CombinedStatementRequestDTO;
import com.itssc.jasperreport.dto.request.HajjReceiptRequestDTO;
import com.itssc.jasperreport.dto.response.DefaultResponse;
import com.itssc.jasperreport.service.api.CustomerReportService;
import com.itssc.jasperreport.service.api.HajjReceiptService;
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
public class CustomerReportController {
    private final CustomerReportService customerReportService;
    @PostMapping("/downloadCustomerReport")
    public ResponseEntity<DefaultResponse> downloadCustomerReport(
            @RequestHeader("legalEntityId") String legalEntityId,
            @RequestBody CombinedStatementRequestDTO combinedStatementRequestDTO) {

        combinedStatementRequestDTO.setLegalEntityId(legalEntityId);
        return Responder.success(customerReportService.downloadCustomerReport(combinedStatementRequestDTO));
    }

    @PostMapping("/downloadCustomerCsv")
    public ResponseEntity<DefaultResponse> downloadCustomerCsv(
            @RequestHeader("legalEntityId") String legalEntityId,
            @RequestBody CombinedStatementRequestDTO combinedStatementRequestDTO) {
        combinedStatementRequestDTO.setLegalEntityId(legalEntityId);
        return Responder.success(customerReportService.downloadCustomerReportCsv(combinedStatementRequestDTO));
    }
}
