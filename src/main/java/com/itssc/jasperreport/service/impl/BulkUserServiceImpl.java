package com.itssc.jasperreport.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itssc.jasperreport.controller.BulkUserController;
import com.itssc.jasperreport.dto.request.CombinedStatementRequestDTO;
import com.itssc.jasperreport.dto.response.ServiceResponse;
import com.itssc.jasperreport.models.BulkUserInfo;
import com.itssc.jasperreport.models.NewCombinedTransactionInfo;
import com.itssc.jasperreport.service.api.BulkUserService;
import com.itssc.jasperreport.utils.LocalDateFormatUtil;
import com.itssc.jasperreport.utils.Masker;
import com.itssc.jasperreport.utils.ResourceUtil;
import com.itssc.jasperreport.utils.Translation;
import com.opencsv.CSVWriter;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BulkUserServiceImpl implements BulkUserService {
    @Override
    public ServiceResponse downloadBulkUser(CombinedStatementRequestDTO combinedStatementRequestDTO) {
        Map<String, Object> parameters = new HashMap<>();
        Locale locale = Locale.ENGLISH;
        if (combinedStatementRequestDTO.getLegalEntityId().equalsIgnoreCase("SL6940001") || combinedStatementRequestDTO.getLegalEntityId().equalsIgnoreCase("GM2700001")) {
            locale = Locale.ENGLISH;

        } else {
            locale = Locale.FRENCH;
        }
        if (combinedStatementRequestDTO.getTitle().equals("") || StringUtils.isBlank(combinedStatementRequestDTO.getTitle())) {

            String title = combinedStatementRequestDTO.getTitle();
            if (title.equals("") || StringUtils.isBlank(title)) title = Translation.BULKUSERTITLE.getTranslation(locale);
            String contractIdHeader = Translation.CONTRACTID.getTranslation(locale);
            String firstNameHeader = Translation.FIRSTNAMEHEADER.getTranslation(locale);
            String lastNameHeader = Translation.LASTNAMEHEADER.getTranslation(locale);
            String emailHeader = Translation.EMAIL.getTranslation(locale);
            String customerIdHeader = Translation.CUSTOMERID.getTranslation(locale);
            String userIdHeader = Translation.USERID.getTranslation(locale);
            String statusHeader = Translation.STATUS.getTranslation(locale);
            String userId = Translation.USERID.getTranslation(locale) + combinedStatementRequestDTO.getUserId();
            String snHeader = "S/N";

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(getBulkUserInfo(combinedStatementRequestDTO));

            parameters.put("imageLogo", ResourceUtil.getImagePath(combinedStatementRequestDTO.getLegalEntityId()));
            parameters.put("imgBackground", ResourceUtil.getBackgroundImgPath(combinedStatementRequestDTO.getLegalEntityId()));
            parameters.put("title", title);
            parameters.put("fullDate", LocalDateFormatUtil.formatFullDate(combinedStatementRequestDTO.getLegalEntityId(), LocalDate.now().toString()));
            parameters.put("contractData", dataSource);
            parameters.put("snHeader", snHeader);
            parameters.put("contractIdHeader", contractIdHeader);
            parameters.put("firstNameHeader", firstNameHeader);
            parameters.put("lastNameHeader", lastNameHeader);
            parameters.put("emailHeader", emailHeader);
            parameters.put("customerIdHeader", customerIdHeader);
            parameters.put("userIdHeader", userIdHeader);
            parameters.put("statusHeader", statusHeader);
            InputStream stream = BulkUserServiceImpl.class.getClassLoader().getResourceAsStream(ResourceUtil.getBulkUserReceiptTemplate(combinedStatementRequestDTO.getLegalEntityId()));
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

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public ServiceResponse downloadBulkUserCsv(CombinedStatementRequestDTO combinedStatementRequestDTO) {
        byte[] base64Data;
        byte[] decodedBytes = Base64.getDecoder().decode(combinedStatementRequestDTO.getBase64String());

        String decodedString = new String(decodedBytes);
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(decodedString);

            StringWriter csvWriterString = new StringWriter();
            // Write headers
            try (CSVWriter csvWriter = new CSVWriter(csvWriterString)) {
                // Write headers
                String[] headers = getHeaders(rootNode.get("bulkUserData").get(0));
                String[] newHeaders = convertHeaders(headers, combinedStatementRequestDTO.getLegalEntityId());
                csvWriter.writeNext(newHeaders);
                // Write data
                for (JsonNode node : rootNode.get("bulkUserData")) {
                    String[] data = getData(node, headers);
                    csvWriter.writeNext(translateStatus(data,combinedStatementRequestDTO.getLegalEntityId()));
                }
            }

            // Get the CSV data as a string
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
    public ServiceResponse downloadBulkUserExcel(CombinedStatementRequestDTO combinedStatementRequestDTO) {

            byte[] decodedBytes = Base64.getDecoder().decode(combinedStatementRequestDTO.getBase64String());
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
                String[] headers = getHeaders(rootNode.get("bulkUserData").get(0));
                String[] newHeaders = convertHeaders(headers, combinedStatementRequestDTO.getLegalEntityId());
                for (String key : newHeaders) {
                    Cell cell = headerRow.createCell(columnIndex++);
                    cell.setCellValue(key);
                }

                // Create data rows
                int i = 1;
                for (JsonNode node : rootNode.get("bulkUserData")) {
                    columnIndex = 0;
                    String[] data = getData(node, headers);
                    Row dataRow = sheet.createRow(i++);
                    for (String key : data) {
                        Cell cell = dataRow.createCell(columnIndex++);
                        cell.setCellValue(translateStatus(key, combinedStatementRequestDTO.getLegalEntityId()));
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


    public static List<BulkUserInfo> getBulkUserInfo(CombinedStatementRequestDTO combinedStatementRequestDTO) {
        List<BulkUserInfo> bulkUserInfoList = new ArrayList<>();

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(combinedStatementRequestDTO.getBase64String());
            String decodedString = new String(decodedBytes);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(decodedString);

            if (rootNode.has("error")) {
                // Handle error condition if needed
                return new ArrayList<>();
            }

            JsonNode jsonArray = rootNode.get("bulkUserData");

            if (jsonArray != null && jsonArray.isArray()) {
                for (JsonNode jsonArr : jsonArray) {
                    String lastName = jsonArr.path("LastName").asText("N/A");
                    String infinityUserId = jsonArr.path("InfinityUserId").asText("N/A");
                    String firstName = jsonArr.path("FirstName").asText("N/A");
                    String customerId = jsonArr.path("CustomerId").asText("N/A");
                    String phoneNumber = jsonArr.path("PhoneNumber").asText("N/A");
                    String contractId = jsonArr.path("ContractId").asText("N/A");
                    String email = jsonArr.path("Email").asText("N/A");
                    String status = jsonArr.path("Status").asText("N/A");

                    // Mask email if present
                    if (!StringUtils.isBlank(email)) {
                        email = Masker.maskEmail(email);
                    }

                    // Translate status using translateStatus method
                    status = translateStatus(status, combinedStatementRequestDTO.getLegalEntityId()); // Assuming legalEntity is accessible

                    bulkUserInfoList.add(BulkUserInfo.builder()
                            .sn(String.valueOf(bulkUserInfoList.size() + 1))
                            .lastName(lastName)
                            .userId(infinityUserId)
                            .firstName(firstName)
                            .contractId(contractId)
                            .status(status)
                            .customerId(customerId)
                            .build());
                }
            }
        } catch (Exception e) {
            // Handle exceptions or log errors
            e.printStackTrace();
        }

        return bulkUserInfoList;
    }
    private static String translateStatus(String status, String legalEntityId){
        Locale locale;
        if (legalEntityId.equals("SL6940001") || legalEntityId.equals("GM2700001")){
            locale = Locale.ENGLISH;
        } else {
            locale = Locale.FRENCH;
        }
        switch(status.toUpperCase()){
            case "SUCCESS" -> Translation.SUCCESS.getTranslation(locale).toUpperCase();
            case "FAILURE" -> Translation.FAILURE.getTranslation(locale).toUpperCase();
        }
        return status;
    }

    private static String[] getData(JsonNode node, String[] headers) {
        Iterator<String> fieldNames = node.fieldNames();
        String[] data = new String[node.size()];
        int idx = 0;
        while (fieldNames.hasNext()) {
            String text = node.get(fieldNames.next()).asText();
            data[idx++] = text;
        }
        for(int i = 0; i < headers.length; i++){
            String head = headers[i];
            if(head.equals("Email")){
                data[i] = Masker.maskEmail(data[i]);
            }
            if(head.equals("PhoneNumber")){
                data[i] = Masker.maskPhoneNumber(data[i]);
            }
        }
        return data;
    }

    private static String[] translateStatus(String[] dataArray, String legalEntityId){
        String[] status = new String[dataArray.length];
        for(int i = 0; i < dataArray.length; i++){
            status[i] = translateStatus(dataArray[i], legalEntityId);
        }
        return status;
    }
    private static String[] getHeaders(JsonNode node) {
        Iterator<String> fieldNames = node.fieldNames();
        String[] headers = new String[node.size()];
        int idx = 0;
        while (fieldNames.hasNext()) {
            headers[idx++] = fieldNames.next();
        }
        return headers;
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
                case "LastName" -> Translation.LASTNAMEHEADER.getTranslation(locale).toUpperCase();
                case "CustomerId" -> Translation.CUSTOMERID.getTranslation(locale).toUpperCase();
                case "FirstName" -> Translation.FIRSTNAMEHEADER.getTranslation(locale).toUpperCase();
                case "ContractId" -> Translation.CONTRACTID.getTranslation(locale).toUpperCase();
                case "PhoneNumber" -> Translation.TELEPHONE.getTranslation(locale).toUpperCase();
                case "InfinityUserId" -> Translation.INFINITYUSERID.getTranslation(locale).toUpperCase();
                case "Status" -> Translation.STATUS.getTranslation(locale).toUpperCase();
                default -> head.toUpperCase();
            };
        }
        return headers;
    }

}
