package utils;

public final class NumberUtils {

    private NumberUtils() {
    }

    /**
     * Parses UI numbers such as "2.555", "1,250.75" or " 638.75 ".
     */
    public static double parse(String text) {
        String normalized = text.replace(",", "").replace(" ", "").trim();
        if (normalized.isEmpty()) {
            throw new NumberFormatException("Empty numeric text");
        }
        return Double.parseDouble(normalized);
    }
}
