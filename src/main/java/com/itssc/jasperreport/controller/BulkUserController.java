package com.itssc.jasperreport.controller;

import com.itssc.jasperreport.dto.request.CombinedStatementRequestDTO;
import com.itssc.jasperreport.dto.response.DefaultResponse;
import com.itssc.jasperreport.service.api.BulkUserService;
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
public class BulkUserController {
        private final BulkUserService bulkUserService;
        @PostMapping("/getBulkUserCreationReportPdf")
        public ResponseEntity<DefaultResponse> getBulkUserCreationReportPdf(
                @RequestHeader("legalEntityId") String legalEntityId,
                @RequestBody CombinedStatementRequestDTO combinedStatementRequestDTO) {
            combinedStatementRequestDTO.setLegalEntityId(legalEntityId);
            return Responder.success(bulkUserService.downloadBulkUser(combinedStatementRequestDTO));
        }

}
