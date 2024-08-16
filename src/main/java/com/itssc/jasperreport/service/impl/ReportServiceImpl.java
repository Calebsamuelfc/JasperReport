package com.itssc.jasperreport.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itssc.jasperreport.dto.request.ContractRequestDTO;
import com.itssc.jasperreport.dto.response.ServiceResponse;
import com.itssc.jasperreport.service.api.ReportService;
import com.itssc.jasperreport.utils.Masker;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.Base64;
import java.util.Iterator;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    @Override
    public ServiceResponse downloadCSV(ContractRequestDTO contractRequestDTO) {
        byte[] decodedBytes = Base64.getDecoder().decode(contractRequestDTO.getBase64String());
        String decodedString = new String(decodedBytes);
        byte[] base64Data;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(decodedString);
            StringWriter csvWriterString = new StringWriter();
            try (CSVWriter csvWriter = new CSVWriter(csvWriterString)) {
                String[] headers = getHeaders(rootNode.get(contractRequestDTO.getTitle()).get(0));
                csvWriter.writeNext(headers);
                for (JsonNode node : rootNode.get(contractRequestDTO.getTitle())) {
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
                    .title("csvData")
                    .build();
        }

        String base64String = new String(base64Data);
        return ServiceResponse.builder()
                .base64String(base64String)
                .responseStatus("success")
                .responseCode("200")
                .fileFormat("CSV")
                .title("csvData")
                .build();

    }

    @Override
    public ServiceResponse downloadExcel(ContractRequestDTO contractRequestDTO) {
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
            String[] headers = getHeaders(rootNode.get(contractRequestDTO.getTitle()).get(0));
            for (String key : headers) {
                Cell cell = headerRow.createCell(columnIndex++);
                cell.setCellValue(key);
            }

            // Create data rows
            int i = 1;
            for (JsonNode node : rootNode.get(contractRequestDTO.getTitle())) {
                columnIndex = 0;
                String[] data = getData(node, headers);
                Row dataRow = sheet.createRow(i++);
                for (String key : data) {
                    Cell cell = dataRow.createCell(columnIndex++);
                    cell.setCellValue(key);
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
                    .title("excelData")
                    .build();
        }
        return ServiceResponse.builder()
                .base64String(base64String)
                .responseStatus("success")
                .responseCode("200")
                .fileFormat("XLSX")
                .title("excelData")
                .build();
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
}
