package utils;

public final class RegexUtils {

    private RegexUtils() {
    }

    public static String escape(String text) {
        return text.replaceAll("[.*+?^${}()|\\[\\]\\\\/&-]", "\\\\$0");
    }
}
