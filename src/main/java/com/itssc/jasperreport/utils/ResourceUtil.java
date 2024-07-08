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

}
