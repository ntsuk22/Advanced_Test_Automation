package utils;

public final class RegexUtils {

    private RegexUtils() {
    }

    /**
     * Escapes regex metacharacters. Use instead of Pattern.quote() for patterns passed to Playwright:
     * they are evaluated as JavaScript regexes in the browser, which do not support \Q...\E.
     */
    public static String escape(String text) {
        return text.replaceAll("[.*+?^${}()|\\[\\]\\\\/&-]", "\\\\$0");
    }
}
