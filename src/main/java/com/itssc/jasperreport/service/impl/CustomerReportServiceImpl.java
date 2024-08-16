package com.itssc.jasperreport.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itssc.jasperreport.dto.request.CombinedStatementRequestDTO;
import com.itssc.jasperreport.dto.response.ServiceResponse;
import com.itssc.jasperreport.models.CustomerReportInfo;
import com.itssc.jasperreport.service.api.CustomerReportService;
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
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomerReportServiceImpl implements CustomerReportService {
    @Override
    public ServiceResponse downloadCustomerReport(CombinedStatementRequestDTO combinedStatementRequestDTO) {
        String title = combinedStatementRequestDTO.getTitle();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String date = LocalDateFormatUtil.formatDate(combinedStatementRequestDTO.getLegalEntityId(), combinedStatementRequestDTO.getFromDate()) + " - " + LocalDateFormatUtil.formatDate(combinedStatementRequestDTO.getLegalEntityId(), LocalDate.now().format(formatter));
        Map<String, Object> parameters = new HashMap<>();
        Locale locale = Locale.ENGLISH;
        if (combinedStatementRequestDTO.getLegalEntityId().equalsIgnoreCase("SL6940001") || combinedStatementRequestDTO.getLegalEntityId().equalsIgnoreCase("GM2700001")) {
            locale = Locale.ENGLISH;

        } else {
            locale = Locale.FRENCH;
        }
        if (title.equals("") || StringUtils.isBlank(title)) {
                title = Translation.CUSTOMERREPORTS.getTranslation(locale);
            }
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(getCustomerReport(combinedStatementRequestDTO));
        parameters.put("imageLogo", ResourceUtil.getImagePath(combinedStatementRequestDTO.getLegalEntityId()));
        parameters.put("imgBackground", ResourceUtil.getBackgroundImgPath(combinedStatementRequestDTO.getLegalEntityId()));
        parameters.put("title", title);
        parameters.put("date",date);
        parameters.put("customerData",dataSource);
        parameters.put("fullDate", LocalDateFormatUtil.formatFullDate(combinedStatementRequestDTO.getLegalEntityId(), LocalDate.now().toString()));
        InputStream stream = CustomerReportServiceImpl.class.getClassLoader().getResourceAsStream(ResourceUtil.getCustomerReportReceiptTemplate(combinedStatementRequestDTO.getLegalEntityId()));
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
        return null;
    }

    @Override
    public ServiceResponse downloadCustomerReportCsv(CombinedStatementRequestDTO combinedStatementRequestDTO) {
        byte[] base64Data;
        byte[] decodedBytes = Base64.getDecoder().decode(combinedStatementRequestDTO.getBase64String());

        String decodedString = new String(decodedBytes);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(decodedString);

            StringWriter csvWriterString = new StringWriter();
            // Write headers
            try (CSVWriter csvWriter = new CSVWriter(csvWriterString)) {
                // Write headers
                String[] headers = getHeaders(rootNode.get("vista_customers_list_view").get(0));
                String[] newHeaders = convertHeaders(headers, combinedStatementRequestDTO.getLegalEntityId());
                csvWriter.writeNext(newHeaders);
                // Write data
                for (JsonNode node : rootNode.get("vista_customers_list_view")) {
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

        String base64StringResponse = new String(base64Data);
        System.out.println(base64StringResponse);
        return ServiceResponse.builder()
                .base64String(base64StringResponse)
                .responseStatus("success")
                .responseCode("200")
                .fileFormat("CSV")
                .build();
    }


    public static List<CustomerReportInfo> getCustomerReport(CombinedStatementRequestDTO combinedStatementRequestDTO) {
            List<CustomerReportInfo> customerReportList = new ArrayList<>();

            try {
                byte[] decodedBytes = Base64.getDecoder().decode(combinedStatementRequestDTO.getBase64String());
                String decodedString = new String(decodedBytes);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(decodedString);

                if (rootNode.has("error")) {
                    // Handle error condition if needed
                    return new ArrayList<>();
                }

                JsonNode jsonArray = rootNode.get("vista_customers_list_view");

                if (jsonArray != null && jsonArray.isArray()) {
                    int index = 1;
                    for (JsonNode jsonArr : jsonArray) {
                        String email = jsonArr.path("Email").asText("N/A");
                        String customerType = jsonArr.path("CustomerType").asText("N/A");
                        String enrollmentDate = jsonArr.path("EnrollmentDate").asText("N/A");
                        String userName = jsonArr.path("UserName").asText("N/A");
                        String phoneNo = jsonArr.path("PhoneNo").asText("N/A");
                        String customerStatus = jsonArr.path("CustomerStatus").asText("N/A");
                        String name = jsonArr.path("Name").asText("N/A");

                        if (!StringUtils.isBlank(email)) {
                            email = Masker.maskEmail(email);
                        }

                        if (!StringUtils.isBlank(phoneNo)) {
                            phoneNo = Masker.maskPhoneNumber(phoneNo);
                        }

                        customerStatus = translateStatus(customerStatus, combinedStatementRequestDTO.getLegalEntityId());

                        CustomerReportInfo customerReportInfo = CustomerReportInfo.builder()
                                .sn(String.valueOf(index))
                                .email(email)
                                .customerType(customerType)
                                .enrollmentDate(enrollmentDate)
                                .userName(userName)
                                .phoneNumber(phoneNo)
                                .customerStatus(customerStatus)
                                .name(name)
                                .build();

                        customerReportList.add(customerReportInfo);
                        index++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return customerReportList;
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

        String[] translatedHeaders = new String[headers.length];

        for (int i = 0; i < headers.length; i++) {
            String head = headers[i];
            translatedHeaders[i] = switch (head) {
                case "Email" -> Translation.EMAIL.getTranslation(locale).toUpperCase();
                case "CustomerType" -> Translation.CUSTOMERTYPE.getTranslation(locale).toUpperCase();
                case "Name" -> Translation.CUSTOMERNAME.getTranslation(locale).toUpperCase();
                case "EnrollmentDate" -> Translation.ENROLLMENTDATE.getTranslation(locale).toUpperCase();
                case "UserName" -> Translation.USERNAME.getTranslation(locale).toUpperCase();
                case "CustomerStatus" -> Translation.CUSTOMERSTATUS.getTranslation(locale).toUpperCase();
                case "PhoneNo" -> Translation.TELEPHONE.getTranslation(locale).toUpperCase();
                case "CompanyLegalUnit" -> Translation.COMPANYLEGALUNIT.getTranslation(locale).toUpperCase();
                default -> head.toUpperCase();
            };
        }

        return translatedHeaders;
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
            if(head.equals("PhoneNo")){
                data[i] = Masker.maskPhoneNumber(data[i]);
            }
        }
        return data;
    }
    private static String[] translateStatus(String[] dataArray, String legalEntityId){
        String[] status = new String[dataArray.length];
        for(int i = 0; i < dataArray.length; i++){
            status[i] = translateStatus(dataArray[i],legalEntityId);
        }
        return status;
    }

    private static String translateStatus(String status, String legalEntityId) {
        Locale locale;
        if (legalEntityId.equals("SL6940001") || legalEntityId.equals("GM2700001")) {
            locale = Locale.ENGLISH;
        } else {
            locale = Locale.FRENCH;
        }

        switch (status.toUpperCase()) {
            case "ACTIVE":
                return Translation.ACTIVE.getTranslation(locale);
            case "NEW":
                return Translation.NEW.getTranslation(locale);
            case "SID_CUS_SUSPENDED":
            case "SUSPENDED":
                return Translation.SUSPENDED.getTranslation(locale);
            case "SID_CUS_INACTIVE":
            case "INACTIVE":
                return Translation.INACTIVE.getTranslation(locale);
            default:
                return status;
        }
    }

}
