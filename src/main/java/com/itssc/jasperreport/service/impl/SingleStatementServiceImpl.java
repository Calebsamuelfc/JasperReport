package com.itssc.jasperreport.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itssc.jasperreport.dto.request.SingleStatementRequestDTO;
import com.itssc.jasperreport.dto.response.ServiceResponse;
import com.itssc.jasperreport.models.KVTableInfo;
import com.itssc.jasperreport.models.TransactionInfo;
import com.itssc.jasperreport.service.api.SingleStatementService;
import com.itssc.jasperreport.utils.LocalDateFormatUtil;
import com.itssc.jasperreport.utils.ResourceUtil;
import com.itssc.jasperreport.utils.Translation;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;
@Service
@RequiredArgsConstructor
public class SingleStatementServiceImpl implements SingleStatementService {
    @Override
    public ServiceResponse downloadSingleStatement(SingleStatementRequestDTO singleStatementRequestDTO) {
        Map<String, Object> parameters = new HashMap<>();
        Locale locale;
        if (singleStatementRequestDTO.getLegalEntityId().equalsIgnoreCase("SL6940001") || singleStatementRequestDTO.getLegalEntityId().equalsIgnoreCase("GM2700001")) {
            locale = Locale.ENGLISH;
        } else {
            locale = Locale.FRENCH;
        }
        parameters.put("imgLogo", ResourceUtil.getImagePath(singleStatementRequestDTO.getLegalEntityId()));
        parameters.put("imgBackground", ResourceUtil.getBackgroundImgPath(singleStatementRequestDTO.getLegalEntityId()));
        parameters.put("fullDate", LocalDateFormatUtil.formatFullDate(singleStatementRequestDTO.getLegalEntityId(), LocalDate.now().toString()));
        parameters.put("shortDate", LocalDateFormatUtil.formatShortDate(singleStatementRequestDTO.getLegalEntityId(), LocalDate.now().toString()));

        Map<String, Object> transactionParameters = getTransactionParameters(singleStatementRequestDTO);
        List<TransactionInfo> transactionInfoList = (List<TransactionInfo>)transactionParameters.get("transactionList");

        String closingBalance = (String)transactionParameters.get("closingBalance");
        String sumCredit = transactionParameters.get("sumCredit").toString();
        String sumDebit = transactionParameters.get("sumDebit").toString();
        String countDebit = String.valueOf(transactionParameters.get("countDebit"));
        String countCredit = String.valueOf(transactionParameters.get("countCredit"));
        transactionParameters.put("startDate", StringUtils.isBlank(singleStatementRequestDTO.getStartDate()) ? "" : singleStatementRequestDTO.getStartDate());
        transactionParameters.put("endDate", StringUtils.isBlank(singleStatementRequestDTO.getEndDate()) ? "" : singleStatementRequestDTO.getEndDate());

        JRBeanCollectionDataSource personalInfo = new JRBeanCollectionDataSource(generateTableInfoForPersonalInformation(singleStatementRequestDTO));
        JRBeanCollectionDataSource accountSummary = new JRBeanCollectionDataSource(generateTableInfoForaccountSummary(transactionParameters,singleStatementRequestDTO));
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(transactionInfoList);

        parameters.put("personalInfo", personalInfo);
        parameters.put("accountSummary", accountSummary);
        parameters.put("dataSource", dataSource);


        InputStream stream = SingleStatementServiceImpl.class.getClassLoader().getResourceAsStream(ResourceUtil.getSingleStatmentTemplate(singleStatementRequestDTO.getLegalEntityId()));

        JasperReport jasperReport = null;
        try {
            jasperReport = JasperCompileManager.compileReport(stream);
            JasperPrint jasperPrint = new JasperPrint();
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, (JRDataSource) new JREmptyDataSource());
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

    public static List<KVTableInfo> generateTableInfoForPersonalInformation(SingleStatementRequestDTO singleStatementRequestDTO) {
        Locale locale;
        if (singleStatementRequestDTO.getLegalEntityId().equalsIgnoreCase("SL6940001") || singleStatementRequestDTO.getLegalEntityId().equalsIgnoreCase("GM2700001")) {
            locale = Locale.ENGLISH;
        } else {
            locale = Locale.FRENCH;
        }
        List<KVTableInfo> result = new ArrayList<>();

        if (!StringUtils.isBlank(singleStatementRequestDTO.getStartDate()))
            result.add(KVTableInfo.builder().key(Translation.STARTDATE.getTranslation(locale)).value(singleStatementRequestDTO.getStartDate()).build());
        if (!StringUtils.isBlank(singleStatementRequestDTO.getEndDate()))
            result.add(KVTableInfo.builder().key(Translation.ENDDATE.getTranslation(locale)).value(singleStatementRequestDTO.getEndDate()).build());
        if (!StringUtils.isBlank(singleStatementRequestDTO.getBranch()))
            result.add(KVTableInfo.builder().key(Translation.BRANCH.getTranslation(locale)).value(singleStatementRequestDTO.getBranch()).build());
        if (!StringUtils.isBlank(singleStatementRequestDTO.getTelephone()))
            result.add(KVTableInfo.builder().key(Translation.TELEPHONE.getTranslation(locale)).value(singleStatementRequestDTO.getTelephone()).build());
        if (!StringUtils.isBlank(singleStatementRequestDTO.getEmail()))
            result.add(KVTableInfo.builder().key(Translation.EMAIL.getTranslation(locale)).value(singleStatementRequestDTO.getEmail()).build());
        if (!StringUtils.isBlank(singleStatementRequestDTO.getGeneratedBy()))
            result.add(KVTableInfo.builder().key(Translation.GENERATEDBY.getTranslation(locale)).value(singleStatementRequestDTO.getGeneratedBy()).build());

        return result;

    }

    public static List<KVTableInfo> generateTableInfoForaccountSummary (Map<String, Object> param,SingleStatementRequestDTO singleStatementRequestDTO) {
        Locale locale;
        if (singleStatementRequestDTO.getLegalEntityId().equalsIgnoreCase("SL6940001") || singleStatementRequestDTO.getLegalEntityId().equalsIgnoreCase("GM2700001")) {
            locale = Locale.ENGLISH;
        } else {
            locale = Locale.FRENCH;
        }
        List<KVTableInfo> result = new ArrayList<>();

        if (!StringUtils.isBlank(param.get("startDate").toString()))
            result.add(KVTableInfo.builder().key(Translation.STARTDATE.getTranslation(locale)).value(param.get("startDate").toString()).build());
        if (!StringUtils.isBlank(param.get("endDate").toString()))
            result.add(KVTableInfo.builder().key(Translation.ENDDATE.getTranslation(locale)).value(param.get("endDate").toString()).build());
        if (!StringUtils.isBlank(param.get("openingBalance").toString()))
            result.add(KVTableInfo.builder().key(Translation.OPENINGBALANCE.getTranslation(locale)).value(param.get("openingBalance").toString()).build());
        if (!StringUtils.isBlank(param.get("closingBalance").toString()))
            result.add(KVTableInfo.builder().key(Translation.CLOSINGBALANCE.getTranslation(locale)).value(param.get("closingBalance").toString()).build());
        if (!StringUtils.isBlank(param.get("sumCredit").toString()))
            result.add(KVTableInfo.builder().key(Translation.SUMCREDIT.getTranslation(locale)).value(param.get("sumCredit").toString()).build());
        if (!StringUtils.isBlank(param.get("sumDebit").toString()))
            result.add(KVTableInfo.builder().key(Translation.SUMDEBIT.getTranslation(locale)).value(param.get("sumDebit").toString()).build());

        return result;

    }

    public Map<String, Object> getTransactionParameters(SingleStatementRequestDTO singleStatementRequestDTO) {
        Map<String, Object> result = new HashMap<>();
        List<TransactionInfo> transactionList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String decodedString = new String(Base64.getDecoder().decode(singleStatementRequestDTO.getBase64String()), StandardCharsets.UTF_8);
            JsonNode rootNode = objectMapper.readTree(decodedString);
            JsonNode jsonArray = rootNode.path("body");

            double sumDebit = 0.0;
            double sumCredit = 0.0;
            int countDebit = 0;
            int countCredit = 0;

            String balanceCurrency = null;
            if (jsonArray != null && jsonArray.isArray()) {
                for (JsonNode jsonArr : jsonArray) {
                    String amount = jsonArr.has("amount") ? jsonArr.get("amount").asText("N/A") : "N/A";
                    String balance = jsonArr.has("balance") ? jsonArr.get("balance").asText("N/A") : "N/A";
                    String transactionReference = jsonArr.has("transactionReference") ? jsonArr.get("transactionReference").asText("N/A") : "N/A";
                    String displayName = jsonArr.has("displayName") ? jsonArr.get("displayName").asText("N/A") : "N/A";
                    String narrative = jsonArr.has("narrative") ? jsonArr.get("narrative").asText(displayName) : displayName;
                    String bookingDate = jsonArr.has("bookingDate") ? jsonArr.get("bookingDate").asText("N/A") : "N/A";
                    String valueDate = jsonArr.has("valueDate") ? jsonArr.get("valueDate").asText("N/A") : "N/A";
                    String currencyId = jsonArr.has("currencyId") ? jsonArr.get("currencyId").asText("N/A") : "N/A";
                    String entryReference = jsonArr.has("entryReference") ? jsonArr.get("entryReference").asText("N/A") : "N/A";
                    String accountId = jsonArr.has("accountId") ? jsonArr.get("accountId").asText("N/A") : "N/A";

                    double amounts = Double.parseDouble(amount);
                    double balances = Double.parseDouble(balance);
                    DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
                    String formattedAmount = decimalFormat.format(amounts);
                    String formattedBalance = decimalFormat.format(balances);

                    if (narrative.equals("@ STAN")) {
                        narrative = displayName;
                    }

                    TransactionInfo transactionInfo = new TransactionInfo();
                    if (amounts < 0.0) {
                        transactionInfo.setWithdrawal("-" + currencyId + " " + formattedAmount.substring(1));
                        transactionInfo.setDeposit("");
                        sumDebit += amounts;
                        countDebit++;
                    } else {
                        transactionInfo.setDeposit(currencyId + " " + formattedAmount);
                        transactionInfo.setWithdrawal("");
                        sumCredit += amounts;
                        countCredit++;
                    }
                    transactionInfo.setDescription(narrative);
                    transactionInfo.setDate(valueDate);
                    transactionInfo.setBookingDate(bookingDate);
                    transactionInfo.setTransactionId(transactionReference);
                    transactionInfo.setBalance(balances < 0.0 ? "-" + currencyId + " " + formattedBalance.substring(1) : currencyId + " " + formattedBalance);
                    transactionList.add(transactionInfo);
                }

                JsonNode firstTransaction = jsonArray.get(jsonArray.size() - 1);
                JsonNode lastTransaction = jsonArray.get(0);
                balanceCurrency = firstTransaction.has("currencyId") ? firstTransaction.get("currencyId").asText("N/A") : "N/A";

                String openingBalance = firstTransaction.get("balance").asText();
                String openingAmount = firstTransaction.get("amount").asText();
                double openingBal = Double.parseDouble(openingBalance);
                double openingAmt = Double.parseDouble(openingAmount);
                openingBalance = formatAmountWithCurrency(openingBal - openingAmt, balanceCurrency);

                String closingBalance = lastTransaction.get("balance").asText();
                double closingAmt = Double.parseDouble(closingBalance);
                closingBalance = formatAmountWithCurrency(closingAmt, balanceCurrency);

                result.put("closingBalance", closingBalance);
                result.put("openingBalance", openingBalance);
            }

            result.put("transactionList", transactionList);
            result.put("sumCredit", formatAmountWithCurrency(sumCredit, balanceCurrency));
            result.put("sumDebit", formatAmountWithCurrency(sumDebit, balanceCurrency));
            result.put("countDebit", countDebit);
            result.put("countCredit", countCredit);

        } catch (Exception e) {
            // Handle or log exception
            e.printStackTrace();
        }

        return result;
    }


    public static String formatAmountWithCurrency(Double amount, String currency) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
        String formattedAmount = decimalFormat.format(amount);
        String result = "";
        if (amount.doubleValue() < 0.0D) {
            result = "-" + currency + " " + formattedAmount.substring(1);
        } else if (amount.equals(Double.valueOf(0.0D))) {
            result = currency + " " + amount;
        } else {
            result = currency + " " + formattedAmount;
        }
        return result;
    }
}
