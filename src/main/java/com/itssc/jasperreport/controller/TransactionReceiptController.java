package com.itssc.jasperreport.controller;

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
@RequestMapping("/JasperReports/api/v1")
public class TransactionReceiptController {

    private final TransactionReceiptService singleTransactionReceiptService;
    @PostMapping("/downloadSingleTransaction")
    public ResponseEntity<DefaultResponse> downloadSingleTransactionReceipt(
            @RequestHeader("legalEntityId") String legalEntityId,
            @RequestBody TransactionReceiptRequestDTO transactionReceiptRequestDTO) {
        transactionReceiptRequestDTO.setLegalEntityId(legalEntityId);
        return Responder.success(singleTransactionReceiptService.downloadSingleTransactionReceipt(transactionReceiptRequestDTO));
    }
}
