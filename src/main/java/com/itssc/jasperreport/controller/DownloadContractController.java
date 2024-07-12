package com.itssc.jasperreport.controller;

import com.itssc.jasperreport.dto.request.RequestHeaders;
import com.itssc.jasperreport.dto.request.TransactionReceiptRequestDTO;
import com.itssc.jasperreport.dto.request.downloadContractRequestDTO;
import com.itssc.jasperreport.dto.response.DefaultResponse;
import com.itssc.jasperreport.service.api.DownloadContractService;
import com.itssc.jasperreport.service.api.TransactionReceiptService;
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
public class DownloadContractController {

    private final DownloadContractService downloadContractService;
    @PostMapping("/downloadContract")
    public ResponseEntity<DefaultResponse> downloadContract(
            @RequestHeader("legalEntityId") String legalEntityId,
            @RequestHeader("authenticationID") String authenticationID,
            @RequestHeader("applicationID") String applicationID,
            @RequestHeader("APIKey") String ClientAPIKey,
            @RequestHeader("hash") String hash,
            @RequestBody downloadContractRequestDTO downloadContractRequestDTO) {

        RequestHeaders headers = new RequestHeaders();
        headers.setAuthenticationID(authenticationID);
        headers.setApplicationID(applicationID);
        headers.setAPIKey(ClientAPIKey);
        headers.setLegalEntityId(legalEntityId);
        headers.setHash(hash);

        downloadContractRequestDTO.setLegalEntityId(legalEntityId);
        return Responder.success(downloadContractService.DownloadContract(downloadContractRequestDTO));
}

}
