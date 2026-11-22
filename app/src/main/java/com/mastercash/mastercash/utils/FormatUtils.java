package com.mastercash.mastercash.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtils {

    private static final Locale LOCALE_BR = new Locale("pt", "BR");

    public static String formatCurrency(double valor) {
        NumberFormat formato = NumberFormat.getCurrencyInstance(LOCALE_BR);
        return formato.format(valor);
    }

    public static double parseCurrency(String texto) {
        if (texto == null || texto.isEmpty()) return 0.0;
        String limpo = texto.replace("R$", "").replace(".", "").replace(",", ".").trim();
        try {
            return Double.parseDouble(limpo);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public static String formatDateToBr(String isoDate) {
        if (isoDate != null && isoDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            String[] parts = isoDate.split("-");
            if (parts.length == 3) {
                return parts[2] + "/" + parts[1] + "/" + parts[0];
            }
        }
        return isoDate != null ? isoDate : "";
    }

    public static String parseDateToIso(String brDate) {
        if (brDate != null && brDate.matches("\\d{2}/\\d{2}/\\d{4}")) {
            String[] parts = brDate.split("/");
            if (parts.length == 3) {
                return parts[2] + "-" + parts[1] + "-" + parts[0];
            }
        }
        return brDate != null ? brDate : "";
    }

    public static String applyDateMask(String input) {
        if (input == null) return "";
        String digits = input.replaceAll("\\D", "");
        if (digits.length() > 8) {
            digits = digits.substring(0, 8);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digits.length(); i++) {
            if (i == 2 || i == 4) {
                sb.append('/');
            }
            sb.append(digits.charAt(i));
        }
        return sb.toString();
    }
}
