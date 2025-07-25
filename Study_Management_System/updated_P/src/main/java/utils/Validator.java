package main.utils;

public class Validator {

    public static boolean isValidEmail(String email) {
        // Only allow emails ending with these domains
        return email != null && email.matches("^[\\w.-]+@(?:gmail\\.com|yahoo\\.com|mbstu\\.ac\\.bd|hotmail\\.com)$");
    }

    public static boolean isValidField(String field) {
        return field != null && !field.trim().isEmpty();
    }
}