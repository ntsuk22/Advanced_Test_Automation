package utils;

public final class NumberUtils {

    private NumberUtils() {
    }

    public static double parse(String text) {
        String normalized = text.replace(",", "").replace(" ", "").trim();
        if (normalized.isEmpty()) {
            throw new NumberFormatException("Empty numeric text");
        }
        return Double.parseDouble(normalized);
    }
}
