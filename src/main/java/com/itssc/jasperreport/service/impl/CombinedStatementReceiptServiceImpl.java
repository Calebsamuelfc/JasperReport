package com.itssc.jasperreport.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itssc.jasperreport.dto.request.CombinedStatementRequestDTO;
import com.itssc.jasperreport.dto.request.TransactionReceiptRequestDTO;
import com.itssc.jasperreport.dto.response.MobileServiceResponse;
import com.itssc.jasperreport.dto.response.ServiceResponse;
import com.itssc.jasperreport.models.KVTableInfo;
import com.itssc.jasperreport.models.NewCombinedTransactionInfo;
import com.itssc.jasperreport.service.api.CombinedStatementReceiptService;
import com.itssc.jasperreport.utils.FileUtil;
import com.itssc.jasperreport.utils.LocalDateFormatUtil;
import com.itssc.jasperreport.utils.ResourceUtil;
import com.itssc.jasperreport.utils.Translation;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CombinedStatementReceiptServiceImpl implements CombinedStatementReceiptService {
    // Lets Default locale here
    private Map<String, Object> parameters = new HashMap<>();
    private Locale locale = Locale.ENGLISH;

    @Override
    public ServiceResponse downloadCombinedStatementReceipt(CombinedStatementRequestDTO combinedStatementRequestDTO) {
        System.out.println("enter here"+generateServiceResponse(combinedStatementRequestDTO));
        setupParameters(combinedStatementRequestDTO);
        return generateServiceResponse(combinedStatementRequestDTO);
    }

    @Override
    public MobileServiceResponse downloadCombinedStatementReceiptMobile(CombinedStatementRequestDTO combinedStatementRequestDTO) {
        setupParameters(combinedStatementRequestDTO);
        return generateMobileServiceResponse(combinedStatementRequestDTO);
    }

    // Parameters setup logic
    private void setupParameters(CombinedStatementRequestDTO combinedStatementRequestDTO) {
        if(combinedStatementRequestDTO.getLegalEntityId().equalsIgnoreCase("SL6940001") || combinedStatementRequestDTO.getLegalEntityId().equalsIgnoreCase("GM2700001")){
            locale = Locale.ENGLISH;
        } else {
            locale = Locale.FRENCH;
        }

        parameters.put("imgLogo", ResourceUtil.getImagePath(combinedStatementRequestDTO.getLegalEntityId()));
        parameters.put("imgBackground", ResourceUtil.getBackgroundImgPath(combinedStatementRequestDTO.getLegalEntityId()));
        parameters.put("title", Translation.TITLE.getTranslation(locale));
        parameters.put("fullDate", LocalDateFormatUtil.formatFullDate(combinedStatementRequestDTO.getLegalEntityId(), LocalDate.now().toString()));

        String bankName = StringUtils.isBlank(combinedStatementRequestDTO.getBankName()) ? "N/A" : combinedStatementRequestDTO.getBankName();
        String startDate = StringUtils.isBlank(combinedStatementRequestDTO.getFromDate()) ? "N/A" : combinedStatementRequestDTO.getFromDate();
        String endDate = StringUtils.isBlank(combinedStatementRequestDTO.getToDate()) ? "N/A" : combinedStatementRequestDTO.getToDate();
        String generatedBy = StringUtils.isBlank(combinedStatementRequestDTO.getGeneratedBy()) ? "N/A" : combinedStatementRequestDTO.getGeneratedBy();
        String generatedOn = StringUtils.isBlank(combinedStatementRequestDTO.getGeneratedOn()) ? "N/A" : combinedStatementRequestDTO.getGeneratedOn();

        parameters.put("bankName", bankName);
        parameters.put("startDate", startDate);
        parameters.put("endDate", endDate);
        parameters.put("generatedBy", generatedBy);
        parameters.put("generatedOn", generatedOn);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(getCombinedStatementInfo(combinedStatementRequestDTO));
        parameters.put("dataSource", dataSource);
    }

    // Generate ServiceResponse for web here
    private ServiceResponse generateServiceResponse(CombinedStatementRequestDTO combinedStatementRequestDTO) {
        try {
            InputStream stream = CombinedStatementReceiptServiceImpl.class.getClassLoader().getResourceAsStream(ResourceUtil.getCombinedStatementReceiptTemplate(combinedStatementRequestDTO.getLegalEntityId()));
            JasperReport jasperReport = JasperCompileManager.compileReport(stream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, (JRDataSource)new JREmptyDataSource());
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

    // Generate MobileServiceResponse for mobile here
    private MobileServiceResponse generateMobileServiceResponse(CombinedStatementRequestDTO combinedStatementRequestDTO) {
        try {
            InputStream stream = CombinedStatementReceiptServiceImpl.class.getClassLoader().getResourceAsStream(ResourceUtil.getCombinedStatementReceiptTemplate(combinedStatementRequestDTO.getLegalEntityId()));
            JasperReport jasperReport = JasperCompileManager.compileReport(stream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, (JRDataSource)new JREmptyDataSource());
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);
            String fileName = FileUtil.saveFile(pdfBytes);
            return MobileServiceResponse.builder()
                    .base64String(fileName)
                    .responseStatus("success")
                    .responseCode("200")
                    .build();
        } catch (JRException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<NewCombinedTransactionInfo> getCombinedStatementInfo(CombinedStatementRequestDTO combinedStatementRequestDTO){

        String transactionType = "";
        String amount = "";
        String accountId = "";
        String balance = "";
        String transactionReference = "";
        String displayName = "";
        String narrative = "";
        String bookingDate = "";
        String valueDate = "";
        String currencyId = "";
        String entryReference = "";
        String deposit = "";
        String withdrawal = "";

        byte[] decodedBytes = Base64.getDecoder().decode(combinedStatementRequestDTO.getBase64String());
        String decodedString = new String(decodedBytes);

        List<NewCombinedTransactionInfo> transactionList = new ArrayList<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(decodedString);

            if (rootNode.has("error")) {
                return new ArrayList<>();
            }

            JsonNode jsonArray = rootNode.get("body");

            if (jsonArray != null && jsonArray.isArray()) {
                for (JsonNode jsonArr : jsonArray) {
                    amount = jsonArr.path("amount").asText("N/A");
                    balance = jsonArr.path("balance").asText("N/A");
                    transactionReference = jsonArr.path("transactionReference").asText("N/A");
                    displayName = jsonArr.path("displayName").asText("N/A");
                    narrative = jsonArr.path("narrative").asText("");
                    bookingDate = jsonArr.path("bookingDate").asText("N/A");
                    valueDate = jsonArr.path("valueDate").asText("N/A");
                    currencyId = jsonArr.path("currencyId").asText("N/A");
                    entryReference = jsonArr.path("entryReference").asText("N/A");
                    accountId = jsonArr.path("accountId").asText("N/A");

                    double amounts = Double.parseDouble(amount);
                    double balances = Double.parseDouble(balance);
                    DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
                    String formattedAmount = decimalFormat.format(amounts);
                    String formattedBalance = decimalFormat.format(balances);

                    if (amounts < 0.0) {
                        withdrawal = "-" + currencyId + " " + formattedAmount.substring(1);
                        deposit = "";
                    } else {
                        deposit = currencyId + " " + formattedAmount;
                        withdrawal = "";
                    }

                    if (balances < 0.0) {
                        balance = "-" + currencyId + " " + formattedBalance.substring(1);
                    } else {
                        balance = currencyId + " " + formattedBalance;
                    }

                    if (StringUtils.isBlank(narrative)) {
                        narrative = displayName;
                    }

                    transactionList.add(NewCombinedTransactionInfo.builder()
                            .balance(balance)
                            .accountNumber(accountId)
                            .date(valueDate)
                            .description(narrative)
                            .transactionId(transactionReference)
                            .withdrawal(withdrawal)
                            .deposit(deposit)
                            .build());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return transactionList;
    }

}