package com.itssc.jasperreport.utils;

public class ResourceUtil {

    public static String getImagePath(String legalEntity){
        if (legalEntity.equalsIgnoreCase("GN2240001")){
            return "templates/vistaGui.png";
        } else {
            return "templates/vistaBank.png";
        }
    }
    public static String getRibTemplate(){
       return "templates/RibTemplate.jrxml";
    }

    public static String getTransactionReceiptTemplate(String legalEntityId){
        if (legalEntityId.equalsIgnoreCase("GM2700001") || legalEntityId.equalsIgnoreCase("SL6940001")) {
            return "templates/vistaBankReportEnglish.jrxml";
        } else {
            return "templates/vistaBankReportFrench.jrxml";
        }
    }
    public static String getContractTemplate(String legalEntityId){
        if (legalEntityId.equalsIgnoreCase("GM2700001") || legalEntityId.equalsIgnoreCase("SL6940001")) {
            return "templates/listOfcontract.jrxml";
        } else {
            return "templates/listOfcontract French.jrxml";
        }
    }
    public static String getAccountTemplate(String legalEntityId){
        if (legalEntityId.equalsIgnoreCase("GM2700001") || legalEntityId.equalsIgnoreCase("SL6940001")) {
            return "templates/listOfAccountContract.jrxml";
        } else {
            return "templates/listOfAccountContractFrench.jrxml";
        }
    }

    public static String getSingleStatmentTemplate(String legalEntityId){
        if (legalEntityId.equalsIgnoreCase("GM2700001") || legalEntityId.equalsIgnoreCase("SL6940001")) {
            return "templates/acctStatmentEnglish.jrxml";
        } else {
            return "templates/acctStatmentFrench.jrxml";
        }
    }
    public static String getCombinedStatementReceiptTemplate(String legalEntityId){
        if (legalEntityId.equalsIgnoreCase("GM2700001") || legalEntityId.equalsIgnoreCase("SL6940001")) {
            return "templates/combinedstatementEnglish.jrxml";
        } else {
            return "templates/combinedstatementEnglish.jrxml";
        }
    }

    public static String getHeaderImagePath(String legalEntityId){
        if (legalEntityId.equalsIgnoreCase("GN2240001")) {
            return "templates/Vista GUI Header.png";
        } else {
            return "templates/Vista Bank Header.png";
        }
    }

    public static String getBackgroundImgPath(String legalEntityId){
        if (legalEntityId.equalsIgnoreCase("GN2240001")) {
            return "templates/vistaguibackground.png";
        } else {
            return  "templates/vistabankbackground.png";
        }
    }

}
