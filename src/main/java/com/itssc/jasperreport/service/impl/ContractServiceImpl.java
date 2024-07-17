package com.itssc.jasperreport.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itssc.jasperreport.dto.request.ContractRequestDTO;
import com.itssc.jasperreport.dto.response.ServiceResponse;
import com.itssc.jasperreport.models.ContractInfo;
import com.itssc.jasperreport.service.api.ContractService;
import com.itssc.jasperreport.utils.LocalDateFormatUtil;
import com.itssc.jasperreport.utils.Masker;
import com.itssc.jasperreport.utils.ResourceUtil;
import com.itssc.jasperreport.utils.Translation;
import com.opencsv.CSVWriter;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;


import java.io.InputStream;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    @Override
    public ServiceResponse DownloadContract(ContractRequestDTO ContractRequestDTO) {
        Map<String, Object> parameters = new HashMap<>();
        Locale locale;
        if (ContractRequestDTO.getLegalEntityId().equalsIgnoreCase("SL6940001") || ContractRequestDTO.getLegalEntityId().equalsIgnoreCase("GM2700001")) {
            locale = Locale.ENGLISH;
        } else {
            locale = Locale.FRENCH;
        }

        parameters.put("imageLogo", ResourceUtil.getImagePath(ContractRequestDTO.getLegalEntityId()));
        parameters.put("imgBackground", ResourceUtil.getBackgroundImgPath(ContractRequestDTO.getLegalEntityId()));
        parameters.put("title", Translation.CONTRACTREPORTS.getTranslation(locale));
        parameters.put("date", LocalDateFormatUtil.formatFullDate(ContractRequestDTO.getLegalEntityId(), LocalDate.now().toString()));
        parameters.put("fullDate", LocalDateFormatUtil.formatFullDate(ContractRequestDTO.getLegalEntityId(), LocalDate.now().toString()));

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(getContractInfo(ContractRequestDTO));
        parameters.put("contractData", dataSource);

        InputStream stream = ContractServiceImpl.class.getClassLoader().getResourceAsStream(ResourceUtil.getContractTemplate(ContractRequestDTO.getLegalEntityId()));

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
    public ServiceResponse downloadContractCsv(ContractRequestDTO contractRequestDTO) {
        byte[] decodedBytes = Base64.getDecoder().decode(contractRequestDTO.getBase64String());
        String decodedString = new String(decodedBytes);
        byte[] base64Data;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(decodedString);
            StringWriter csvWriterString = new StringWriter();
            try (CSVWriter csvWriter = new CSVWriter(csvWriterString)) {
                String[] headers = getHeaders(rootNode.get("vista_contract_list_view").get(0));
                String[] newHeaders = convertHeaders(headers, contractRequestDTO.getLegalEntityId());
                csvWriter.writeNext(newHeaders);
                for (JsonNode node : rootNode.get("vista_contract_list_view")) {
                    String[] data = getData(node, headers);
                    csvWriter.writeNext(data);
                }
            }
            String csvData = csvWriterString.toString();
            byte[] csvBytes = csvData.getBytes();
            base64Data = Base64.getEncoder().encode(csvBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.builder()
                    .base64String("")
                    .responseStatus("failure")
                    .responseCode("500")
                    .fileFormat("CSV")
                    .build();
        }

        String base64String = new String(base64Data);
        System.out.println(base64String);
        return ServiceResponse.builder()
                .base64String(base64String)
                .responseStatus("success")
                .responseCode("200")
                .fileFormat("CSV")
                .build();


    }

    @Override
    public ServiceResponse downloadContractExcel(ContractRequestDTO contractRequestDTO) {

        byte[] decodedBytes = Base64.getDecoder().decode(contractRequestDTO.getBase64String());
        String decodedString = new String(decodedBytes);
        String base64String = "";
        try {
            XSSFWorkbook xSSFWorkbook = new XSSFWorkbook();
            Sheet sheet = xSSFWorkbook.createSheet("Data");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(decodedString);

            // Create header row
            Row headerRow = sheet.createRow(0);
            int columnIndex = 0;
            String[] headers = getHeaders(rootNode.get("vista_contract_list_view").get(0));
            String[] newHeaders = convertHeaders(headers, contractRequestDTO.getLegalEntityId());
            for (String key : newHeaders) {
                Cell cell = headerRow.createCell(columnIndex++);
                cell.setCellValue(key);
            }

            // Create data rows
            int i = 1;
            for (JsonNode node : rootNode.get("vista_contract_list_view")) {
                columnIndex = 0;
                String[] data = getData(node, headers);
                Row dataRow = sheet.createRow(i++);
                for (String key : data) {
                    Cell cell = dataRow.createCell(columnIndex++);
                    cell.setCellValue(translateStatus(key, contractRequestDTO.getLegalEntityId()));
                }
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            xSSFWorkbook.write(outputStream);
            byte[] excelData = outputStream.toByteArray();
            base64String = Base64.getEncoder().encodeToString(excelData);

            xSSFWorkbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.builder()
                    .base64String("")
                    .responseStatus("failure")
                    .responseCode("500")
                    .fileFormat("XLSX")
                    .build();
        }

        System.out.println(base64String);
        return ServiceResponse.builder()
                .base64String(base64String)
                .responseStatus("success")
                .responseCode("200")
                .fileFormat("XLSX")
                .build();
    }


    public List<ContractInfo> getContractInfo(ContractRequestDTO ContractRequestDTO){
        final ObjectMapper objectMapper = new ObjectMapper();

        String Email = "N/A";
        String ContractType = "N/A";
        String ContractId = "N/A";
        String ContractCreateDate = "N/A";
        String ContractName = "N/A";
        String CoreCustomerId = "N/A";

        List<ContractInfo> contractInfoList = new ArrayList<>();
        byte[] decodedBytes = Base64.getDecoder().decode(ContractRequestDTO.getBase64String());
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

    private static String[] getHeaders(JsonNode node) {
        Iterator<String> fieldNames = node.fieldNames();
        String[] headers = new String[node.size()];
        int idx = 0;
        while (fieldNames.hasNext())
            headers[idx++] = fieldNames.next();
        return headers;
    }
    private static String[] getData(JsonNode node, String[] headers) {
        Iterator<String> fieldNames = node.fieldNames();
        String[] data = new String[node.size()];
        int idx = 0;
        while (fieldNames.hasNext()) {
            String text = node.get(fieldNames.next()).asText();
            data[idx++] = text;
        }
        for (int i = 0; i < headers.length; i++) {
            String head = headers[i];
            if (head.equals("Email"))
                data[i] = Masker.maskEmail(data[i]);
        }
        return data;
    }
    private static String[] convertHeaders(String[] headers, String legalEntityId) {
        Locale locale;
        if (legalEntityId.equalsIgnoreCase("SL6940001") || legalEntityId.equalsIgnoreCase("GM2700001")) {
            locale = Locale.ENGLISH;
        } else {
            locale = Locale.FRENCH;
        }
        String[] header = new String[headers.length];

        for (int i = 0; i < headers.length; i++) {
            String head = headers[i];
            header[i] = switch (head) {
                case "Email" -> Translation.EMAIL.getTranslation(locale).toUpperCase();
                case "ContractType" -> Translation.CONTRACTTYPE.getTranslation(locale).toUpperCase();
                case "CustomerId", "CoreCustomerId" -> Translation.CUSTOMERID.getTranslation(locale).toUpperCase();
                case "CreateDate" -> Translation.DATE.getTranslation(locale).toUpperCase();
                case "ContractName" -> Translation.CONTRACTNAME.getTranslation(locale).toUpperCase();
                case "ContractId" -> Translation.CONTRACTID.getTranslation(locale).toUpperCase();
                case "CoreCustomerName" -> Translation.CUSTOMERNAME.getTranslation(locale).toUpperCase();
                case "CompanyLegalUnit" -> Translation.COMPANYLEGALUNIT.getTranslation(locale).toUpperCase();
                default -> head.toUpperCase();
            };
        }
        return headers;
    }
    private static String translateStatus(String status, String legalEntityId) {
        Locale locale;
        if (legalEntityId.equals("SL6940001") || legalEntityId.equals("GM2700001")){
            locale = Locale.ENGLISH;
        } else {
            locale = Locale.FRENCH;
        }
        String translatedStatus = switch (status.toUpperCase()) {
            case "ACTIVE" -> Translation.ACTIVE.getTranslation(locale).toUpperCase();
            case "NEW" -> Translation.NEW.getTranslation(locale).toUpperCase();
            case "SID_CUS_SUSPENDED", "SUSPENDED" -> Translation.SUSPENDED.getTranslation(locale).toUpperCase();
            case "SID_CUS_INACTIVE", "INACTIVE" -> Translation.INACTIVE.getTranslation(locale).toUpperCase();
            default -> status.toUpperCase();
        };
        return translatedStatus;
    }

}
