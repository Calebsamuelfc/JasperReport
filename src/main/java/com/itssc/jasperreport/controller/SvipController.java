package com.itssc.jasperreport.controller;

import com.itssc.jasperreport.dto.request.ContractRequestDTO;
import com.itssc.jasperreport.dto.response.DefaultResponse;
import com.itssc.jasperreport.service.api.SvipService;
import com.itssc.jasperreport.utils.Responder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/JasperReports/api/v1")
public class SvipController {

    private final SvipService svipService;
    @PostMapping("/getSVIPLogsOperations")
    public ResponseEntity<DefaultResponse> getSvipCSV(
            @RequestHeader("legalEntityId") String legalEntityId,
            @RequestBody ContractRequestDTO contractRequestDTO) {
        String fileFormat = contractRequestDTO.getFileFormat();
        contractRequestDTO.setLegalEntityId(legalEntityId);
        if ("CSV".equalsIgnoreCase(fileFormat)) {
            return Responder.success(svipService.downloadSvipCsv(contractRequestDTO));
        }
        if ("EXCEL".equalsIgnoreCase(fileFormat)) {
            return Responder.success(svipService.downloadSvipExcel(contractRequestDTO));
        }
        return null;
    }
}
