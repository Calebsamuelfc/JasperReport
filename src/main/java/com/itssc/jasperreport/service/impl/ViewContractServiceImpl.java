package com.itssc.jasperreport.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itssc.jasperreport.dto.request.ContractRequestDTO;
import com.itssc.jasperreport.dto.response.ServiceResponse;
import com.itssc.jasperreport.models.ViewContractInfo;
import com.itssc.jasperreport.service.api.ViewContractService;
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
public class ViewContractServiceImpl implements ViewContractService {

    @Override
    public ServiceResponse viewContractPdf(ContractRequestDTO ContractRequestDTO) {
        Map<String, Object> parameters = new HashMap<>();
        Locale locale;
        if (ContractRequestDTO.getLegalEntityId().equalsIgnoreCase("SL6940001") || ContractRequestDTO.getLegalEntityId().equalsIgnoreCase("GM2700001")) {
            locale = Locale.ENGLISH;
        } else {
            locale = Locale.FRENCH;
        }
        String title = ContractRequestDTO.getTitle();
        if (title.equals("") || StringUtils.isBlank(title)) title = Translation.MYCONTRACTS.getTranslation(locale);

        parameters.put("imageLogo", ResourceUtil.getImagePath(ContractRequestDTO.getLegalEntityId()));
        parameters.put("imgBackground", ResourceUtil.getBackgroundImgPath(ContractRequestDTO.getLegalEntityId()));
        parameters.put("title",title);
        parameters.put("fullDate", LocalDateFormatUtil.formatFullDate(ContractRequestDTO.getLegalEntityId(), LocalDate.now().toString()));
        parameters.put("snHeader","S/N");
        parameters.put("contractIdHeader",Translation.CONTRACTID.getTranslation(locale).toUpperCase());
        parameters.put("contractNameHeader",Translation.CONTRACTNAME.getTranslation(locale).toUpperCase());
        parameters.put("sevdefHeader",Translation.SERVICEDEFINITION.getTranslation(locale));
        parameters.put("emailHeader",Translation.EMAIL.getTranslation(locale).toUpperCase());
        parameters.put("userId",Translation.USERID.getTranslation(locale).toUpperCase() + ContractRequestDTO.getUserId());
        parameters.put("createDateHeader",Translation.DATECREATED.getTranslation(locale));

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(getContractListView(ContractRequestDTO));
        parameters.put("contractData", dataSource);

        InputStream stream = ContractServiceImpl.class.getClassLoader().getResourceAsStream("templates/viewMyContract.jrxml");

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

    @Override
    public ServiceResponse viewContractCsv(ContractRequestDTO ContractRequestDTO) {
        return null;
    }

    @Override
    public ServiceResponse viewContractExcel(ContractRequestDTO ContractRequestDTO) {
        return null;
    }

    public static List<ViewContractInfo> getContractListView(ContractRequestDTO ContractRequestDTO) {
        final ObjectMapper objectMapper = new ObjectMapper();

        String ContractName = "N/A";
        String Email = "N/A";
        String ServiceDefinitionName = "N/A";
        String ContractID = "N/A";
        String CreatedDate = "N/A";

        List<ViewContractInfo> viewContractList = new ArrayList<>();
        byte[] decodedBytes = Base64.getDecoder().decode(ContractRequestDTO.getBase64String());
        String decodedString = new String(decodedBytes);

        try {
            JsonNode rootNode = objectMapper.readTree(decodedString);
            JsonNode jsonArray = rootNode.get("records");

            if (jsonArray != null && jsonArray.isArray()) {
                for (JsonNode contract : jsonArray) {
                    if (contract.has("Email")) {
                        Email = StringUtils.isBlank(contract.get("Email").asText()) ? "N/A" : Masker.maskEmail(contract.get("Email").asText());
                    }
                    if (contract.has("ContractName")) {
                        ContractName = StringUtils.isBlank(contract.get("ContractName").asText()) ? "N/A" : contract.get("ContractName").asText();
                    }
                    if (contract.has("ContractId")) {
                        ContractID = StringUtils.isBlank(contract.get("ContractId").asText()) ? "N/A" : contract.get("ContractId").asText();
                    }
                    if (contract.has("CreatedDate")) {
                        CreatedDate = StringUtils.isBlank(contract.get("CreatedDate").asText()) ? "N/A" : contract.get("CreatedDate").asText();
                    }
                    if (contract.has("ServiceDefinitionName")) {
                        ServiceDefinitionName = StringUtils.isBlank(contract.get("ServiceDefinitionName").asText()) ? "N/A" : contract.get("ServiceDefinitionName").asText();
                    }


                    viewContractList.add(ViewContractInfo.builder()
                            .sn(String.valueOf(viewContractList.size() + 1))
                            .contractName(ContractName)
                            .servdef(ServiceDefinitionName)
                            .email(Email)
                            .contractId(ContractID)
                            .createDate(CreatedDate)
                            .build());
                }

            }
        } catch (Exception e) {

        }
        return viewContractList;
    }
}
