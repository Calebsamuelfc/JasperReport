package com.itssc.jasperreport.controller;

import com.itssc.jasperreport.dto.request.RequestHeaders;
import com.itssc.jasperreport.dto.request.ContractRequestDTO;
import com.itssc.jasperreport.dto.response.DefaultResponse;
import com.itssc.jasperreport.service.api.ContractService;
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
public class ContractController {

    private final ContractService downloadContractService;
    @PostMapping("/downloadContract")
    public ResponseEntity<DefaultResponse> downloadContract(
            @RequestHeader("legalEntityId") String legalEntityId,
            @RequestHeader("authenticationID") String authenticationID,
            @RequestHeader("applicationID") String applicationID,
            @RequestHeader("APIKey") String ClientAPIKey,
            @RequestHeader("hash") String hash,
            @RequestBody ContractRequestDTO ContractRequestDTO) {

        RequestHeaders headers = new RequestHeaders();
        headers.setAuthenticationID(authenticationID);
        headers.setApplicationID(applicationID);
        headers.setAPIKey(ClientAPIKey);
        headers.setLegalEntityId(legalEntityId);
        headers.setHash(hash);

        ContractRequestDTO.setLegalEntityId(legalEntityId);
        return Responder.success(downloadContractService.DownloadContract(ContractRequestDTO));
}

}
