package com.dinidu.lk.pmt.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PhoneUtil {
    private static final Map<String, String> COUNTRY_CODE_MAP = new HashMap<>();

    static {
        COUNTRY_CODE_MAP.put("94", "+94");   // Sri Lanka
        COUNTRY_CODE_MAP.put("1", "+1");     // US or Canada
        COUNTRY_CODE_MAP.put("91", "+91");   // India
        COUNTRY_CODE_MAP.put("86", "+86");   // China
        COUNTRY_CODE_MAP.put("44", "+44");   // UK
        COUNTRY_CODE_MAP.put("33", "+33");   // France
        COUNTRY_CODE_MAP.put("49", "+49");   // Germany
        COUNTRY_CODE_MAP.put("34", "+34");   // Spain
        COUNTRY_CODE_MAP.put("39", "+39");   // Italy
        COUNTRY_CODE_MAP.put("52", "+52");   // Mexico
        COUNTRY_CODE_MAP.put("55", "+55");   // Brazil
        COUNTRY_CODE_MAP.put("61", "+61");   // Australia
        COUNTRY_CODE_MAP.put("81", "+81");   // Japan
        COUNTRY_CODE_MAP.put("7", "+7");     // Russia
    }

    public static String extractCountryCode(String phoneNumber) {
        String cleanedNumber = phoneNumber.replaceAll("[^0-9]", "");

        List<String> sortedCountryCodes = COUNTRY_CODE_MAP.keySet().stream().sorted((a, b) -> Integer.compare(b.length(), a.length())).collect(Collectors.toList());

        for (String prefix : sortedCountryCodes) {
            if (cleanedNumber.startsWith(prefix)) {
                return COUNTRY_CODE_MAP.get(prefix);
            }
        }
        return null;
    }
}