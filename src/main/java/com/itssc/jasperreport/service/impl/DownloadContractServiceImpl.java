package com.itssc.jasperreport.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itssc.jasperreport.dto.request.downloadContractRequestDTO;
import com.itssc.jasperreport.dto.response.ServiceResponse;
import com.itssc.jasperreport.models.ContractInfo;
import com.itssc.jasperreport.models.KVTableInfo;
import com.itssc.jasperreport.service.api.DownloadContractService;
import com.itssc.jasperreport.utils.LocalDateFormatUtil;
import com.itssc.jasperreport.utils.Masker;
import com.itssc.jasperreport.utils.ResourceUtil;
import com.itssc.jasperreport.utils.Translation;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DownloadContractServiceImpl implements DownloadContractService {

    @Override
    public ServiceResponse DownloadContract(downloadContractRequestDTO downloadContractRequestDTO) {
        Map<String, Object> parameters = new HashMap<>();
        Locale locale;
        if (downloadContractRequestDTO.getLegalEntityId().equalsIgnoreCase("SL6940001") || downloadContractRequestDTO.getLegalEntityId().equalsIgnoreCase("GM2700001")) {
            locale = Locale.ENGLISH;
        } else {
            locale = Locale.FRENCH;
        }

        parameters.put("imageLogo", ResourceUtil.getImagePath(downloadContractRequestDTO.getLegalEntityId()));
        parameters.put("imgBackground", ResourceUtil.getBackgroundImgPath(downloadContractRequestDTO.getLegalEntityId()));
        parameters.put("title", Translation.CONTRACTREPORTS.getTranslation(locale));
        parameters.put("date", LocalDateFormatUtil.formatFullDate(downloadContractRequestDTO.getLegalEntityId(), LocalDate.now().toString()));
        parameters.put("fullDate", LocalDateFormatUtil.formatFullDate(downloadContractRequestDTO.getLegalEntityId(), LocalDate.now().toString()));

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(getContractInfo(downloadContractRequestDTO));
        parameters.put("contractData", dataSource);

        InputStream stream = DownloadContractServiceImpl.class.getClassLoader().getResourceAsStream(ResourceUtil.getContractTemplate(downloadContractRequestDTO.getLegalEntityId()));

        JasperReport jasperReport = null;

        try{
            jasperReport = JasperCompileManager.compileReport(stream);
            JasperPrint jasperPrint = new JasperPrint();
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, (JRDataSource)new JREmptyDataSource());
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);
            String base64String = Base64.getEncoder().encodeToString(pdfBytes);
            return ServiceResponse.builder()
                    .base64String(base64String)
                    .responseStatus("success")
                    .responseCode("200")
                    .fileFormat("PDF")
                    .build();
        } catch (JRException e) {
            e.printStackTrace();
        }

              return null;
    }

    public List<ContractInfo> getContractInfo(downloadContractRequestDTO downloadContractRequestDTO){
        final ObjectMapper objectMapper = new ObjectMapper();

        String Email = "N/A";
        String ContractType = "N/A";
        String ContractId = "N/A";
        String ContractCreateDate = "N/A";
        String ContractName = "N/A";
        String CoreCustomerId = "N/A";

        List<ContractInfo> contractInfoList = new ArrayList<>();
        byte[] decodedBytes = Base64.getDecoder().decode(downloadContractRequestDTO.getBase64String());
        String decodedString = new String(decodedBytes);

        try {
            JsonNode rootNode = objectMapper.readTree(decodedString);
            JsonNode contractList = rootNode.get("vista_contract_list_view");

            if (contractList != null && contractList.isArray()) {
                for (JsonNode contract : contractList) {
                    if (contract.has("Email")) {
                        Email = StringUtils.isBlank(contract.get("Email").asText()) ? "N/A" : Masker.maskEmail(contract.get("Email").asText());
                    }

                    if (contract.has("ContractType")) {
                        ContractType = StringUtils.isBlank(contract.get("ContractType").asText()) ? "N/A" : contract.get("ContractType").asText();
                    }
                    if (contract.has("ContractId")) {
                        ContractId = StringUtils.isBlank(contract.get("ContractId").asText()) ? "N/A" : contract.get("ContractId").asText();
                    }
                    if (contract.has("CoreCustomerId")) {
                        CoreCustomerId = StringUtils.isBlank(contract.get("CoreCustomerId").asText()) ? "N/A" : contract.get("CoreCustomerId").asText();
                    }
                    if (contract.has("CreateDate")) {
                        ContractCreateDate = StringUtils.isBlank(contract.get("CreateDate").asText()) ? "N/A" : contract.get("CreateDate").asText();
                    }
                    if (contract.has("ContractName")) {
                        ContractName = StringUtils.isBlank(contract.get("ContractName").asText()) ? "N/A" : contract.get("ContractName").asText();
                    }

                    contractInfoList.add(ContractInfo.builder()
                            .sn(String.valueOf(contractInfoList.size() + 1))
                            .contractName(ContractName)
                            .contractType(ContractType)
                            .contractId(ContractId)
                            .email(Email)
                            .createDate(ContractCreateDate)
                            .coreCustomerId(CoreCustomerId)
                            .build());
                }

            }
        } catch (Exception e) {

    }

        return contractInfoList;
}


}
