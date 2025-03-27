package com.dinidu.lk.pmt.utils.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    private static final String GLOBAL_PHONE_REGEX = "(?:([+]?\\d{1,4})[-.\\s]?)?(?:[(](\\d{1,3})[)][-.\\s]?)?(\\d{1,4})[-.\\s]?(\\d{1,4})[-.\\s]?(\\d{1,9})";
    private static final Pattern GLOBAL_PHONE_PATTERN = Pattern.compile(GLOBAL_PHONE_REGEX);
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    public static boolean validatePhoneNumber(String phoneNumber) {
        Matcher matcher = GLOBAL_PHONE_PATTERN.matcher(phoneNumber);
        return matcher.matches();
    }

    public static boolean isEmailValid(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }

    public static boolean containsUpperCase(String password) {
        return Pattern.compile("[A-Z]").matcher(password).find();
    }

    public static boolean containsLowerCase(String password) {
        return Pattern.compile("[a-z]").matcher(password).find();
    }

    public static boolean containsDigit(String password) {
        return Pattern.compile("\\d").matcher(password).find();
    }

    public static boolean isMinLength(String password) {
        return password.length() >= 8;
    }

    public boolean containsSpecialChar(String password) {
        return Pattern.compile("[^a-zA-Z0-9]").matcher(password).find();
    }

    public static boolean isAlphabetic(String txt) {
        return txt.matches("[a-zA-Z\\s]+");
    }
}
