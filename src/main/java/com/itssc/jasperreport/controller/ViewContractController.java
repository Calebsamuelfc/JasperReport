package com.itssc.jasperreport.controller;

import com.itssc.jasperreport.dto.request.ContractRequestDTO;
import com.itssc.jasperreport.dto.response.DefaultResponse;
import com.itssc.jasperreport.service.api.ContractService;
import com.itssc.jasperreport.service.api.ViewContractService;
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
public class ViewContractController {

    private final ViewContractService viewContractService;
    @PostMapping("/viewMyContract")
    public ResponseEntity<DefaultResponse> viewContractPdf(
            @RequestHeader("legalEntityId") String legalEntityId,
            @RequestBody ContractRequestDTO ContractRequestDTO) {

        ContractRequestDTO.setLegalEntityId(legalEntityId);
        return Responder.success(viewContractService.viewContractPdf(ContractRequestDTO));
    }

    @PostMapping("/downloadMyContractCsv")
    public ResponseEntity<DefaultResponse> viewContractCsv(
            @RequestHeader("legalEntityId") String legalEntityId,
            @RequestBody ContractRequestDTO ContractRequestDTO) {

        ContractRequestDTO.setLegalEntityId(legalEntityId);
        return Responder.success(viewContractService.viewContractCsv(ContractRequestDTO));
    }

//    @PostMapping("/downloadMyContractExcel")
//    public ResponseEntity<DefaultResponse> viewContractExcel(
//            @RequestHeader("legalEntityId") String legalEntityId,
//            @RequestBody ContractRequestDTO ContractRequestDTO) {
//
//        ContractRequestDTO.setLegalEntityId(legalEntityId);
//        return Responder.success(viewContractService.viewContractPdf(ContractRequestDTO));
//    }
}
