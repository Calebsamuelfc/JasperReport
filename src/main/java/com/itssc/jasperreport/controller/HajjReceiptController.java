package com.itssc.jasperreport.controller;

import com.itssc.jasperreport.dto.request.HajjReceiptRequestDTO;
import com.itssc.jasperreport.dto.request.TransactionReceiptRequestDTO;
import com.itssc.jasperreport.dto.response.DefaultResponse;
import com.itssc.jasperreport.service.api.HajjReceiptService;
import com.itssc.jasperreport.utils.Responder;
import lombok.AllArgsConstructor;
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
public class HajjReceiptController {

    private final HajjReceiptService HajjReceiptService;
    @PostMapping("/downloadHajjReport")
    public ResponseEntity<DefaultResponse> downloadHajjReceipt(
            @RequestHeader("legalEntityId") String legalEntityId,
            @RequestBody HajjReceiptRequestDTO HajjReceiptRequestDTO) {
        HajjReceiptRequestDTO.setLegalEntityId(legalEntityId);
        return Responder.success(HajjReceiptService.downloadHajjReceipt(HajjReceiptRequestDTO));
    }
}
