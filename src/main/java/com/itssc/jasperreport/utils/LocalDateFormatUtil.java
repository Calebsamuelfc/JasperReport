package com.itssc.jasperreport.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateFormatUtil {
    public static String formatDate(String legalEntity, String date) {

        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate inputDate = LocalDate.parse(date, formatter1);

        // Define a French locale
        Locale locale = null;

        if (legalEntity.equals("SL6940001") || legalEntity.equals("GM2700001")) {
            locale = new Locale("en", "EN");
        } else {
            locale = new Locale("fr", "FR");
        }

        // Create a DateTimeFormatter with the French locale
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", locale);

        // Format the current date in French
        String formattedDate = inputDate.format(formatter);

        return formattedDate.toUpperCase(locale);
    }

    public static String formatFullDate(String legalEntity, String date) {

        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate inputDate = LocalDate.parse(date, formatter1);

        // Define a French locale
        Locale locale = null;

        if (legalEntity.equals("SL6940001") || legalEntity.equals("GM2700001")) {
            locale = new Locale("en", "EN");
        } else {
            locale = new Locale("fr", "FR");
        }

        // Create a DateTimeFormatter with the French locale
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", locale);

        // Format the current date in French
        String formattedDate = inputDate.format(formatter);

        return formattedDate.toUpperCase(locale);
    }

    public static String formatShortDate(String legalEntity, String date) {

        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate inputDate = LocalDate.parse(date, formatter1);

        // Define a French locale
        Locale locale = null;

        if (legalEntity.equals("SL6940001") || legalEntity.equals("GM2700001")) {
            locale = new Locale("en", "EN");
        } else {
            locale = new Locale("fr", "FR");
        }

        // Create a DateTimeFormatter with the French locale
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E dd MMM yyyy", locale);

        // Format the current date in French
        String formattedDate = inputDate.format(formatter);

        return formattedDate.toUpperCase(locale);
    }
}
