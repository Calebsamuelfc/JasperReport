package com.itssc.jasperreport.utils;

public class Masker {
    public static String maskEmail(String email) {
        if (email == null || email.isEmpty() || email.equals("N/A")) {
            return email;
        }
        // Split the email into local part and domain
        String[] parts = email.split("@");
        if (parts.length != 2) {
            // Invalid email format
            return email;
        }

        // Mask the local part
        String maskedLocalPart = maskLocalPart(parts[0]);

        // Reconstruct the masked email
        return maskedLocalPart + "@" + parts[1];
    }

    private static String maskLocalPart(String localPart) {
        if (localPart.length() == 2) {
            return  localPart.substring(0,1) + "*" ;
        }

        if (localPart.length() < 2) {
            return "*" + localPart.substring(1);
        }

        StringBuilder masked = new StringBuilder(localPart);
        for (int i = 2; i < localPart.length(); i++) {
            masked.setCharAt(i, '*');
        }
        return masked.toString();
    }

    public static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return phoneNumber;
        }

        // Split the phone number into country code and the rest of the digits
        String[] parts = phoneNumber.split("-");
        if (parts.length != 2) {

            if(phoneNumber.length() > 5){
                return maskDigits(phoneNumber);
            }
            return phoneNumber;
        }

        // Mask the digits
        String maskedDigits = maskDigits(parts[1]);

        // Reconstruct the masked phone number
        return parts[0] + "-" + maskedDigits;
    }

    private static String maskDigits(String digits) {
        if (digits.length() < 2) {
            // Keep at least one digit visible
            return "*" + digits.substring(1);
        }

        // Replace all but the first digit with 'x'
        StringBuilder masked = new StringBuilder(digits);
        for (int i = 2; i < digits.length() - 2; i++) {
            masked.setCharAt(i, '*');
        }

        return masked.toString();
    }
}
