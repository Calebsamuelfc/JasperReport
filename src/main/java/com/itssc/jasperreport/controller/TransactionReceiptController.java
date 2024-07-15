package com.itssc.jasperreport.controller;

import com.itssc.jasperreport.dto.request.CombinedStatementRequestDTO;
import com.itssc.jasperreport.dto.request.TransactionReceiptRequestDTO;
import com.itssc.jasperreport.dto.response.DefaultResponse;
import com.itssc.jasperreport.service.api.TransactionReceiptService;
import com.itssc.jasperreport.utils.Responder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/JasperReports/api")
public class TransactionReceiptController {

    private final TransactionReceiptService singleTransactionReceiptService;
    @PostMapping("/v1/downloadSingleTransaction")
    public ResponseEntity<DefaultResponse> downloadSingleTransactionReceipt(
            @RequestHeader("legalEntityId") String legalEntityId,
            @RequestBody TransactionReceiptRequestDTO transactionReceiptRequestDTO) {
        transactionReceiptRequestDTO.setLegalEntityId(legalEntityId);
        return Responder.success(singleTransactionReceiptService.downloadSingleTransactionReceipt(transactionReceiptRequestDTO));
    }
    @PostMapping("/v2/downloadSingleTransaction")
    public ResponseEntity<DefaultResponse> downloadSingleTransactionReceiptV2(
            @RequestHeader("legalEntityId") String legalEntityId,
            @RequestBody TransactionReceiptRequestDTO transactionReceiptRequestDTO) {
        transactionReceiptRequestDTO.setLegalEntityId(legalEntityId);
        return Responder.success(singleTransactionReceiptService.downloadSingleTransactionReceiptMobile(transactionReceiptRequestDTO));
    }
}
