package core;

public class TextUtils {
    public static String normalizeText(String value) {
        if (value == null) return "";
        return value.replace("\n", " ")
                .replace("\r", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public static long parseMoney(String value) {
        if (value == null) return 0;
        String digits = value.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return 0;
        return Long.parseLong(digits);
    }
}
