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
}
