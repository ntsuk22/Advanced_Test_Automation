package utils;

import constants.AppLocale;

import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RateHistoryDateParser {

    private static final Pattern FORMAT = Pattern.compile("^(\\d{1,2})\\s+(\\S+?),?\\s+(\\d{1,2}):(\\d{2})$");

    private RateHistoryDateParser() {
    }

    public static LocalDateTime parse(String text, AppLocale locale) {
        Matcher matcher = FORMAT.matcher(text.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Unexpected rate-history date format: '" + text + "'");
        }
        List<String> months = Arrays.asList(LocalizedText.get(locale, "months").split(","));
        int month = months.indexOf(matcher.group(2)) + 1;
        if (month == 0) {
            throw new IllegalArgumentException("Unknown month '" + matcher.group(2) + "' for locale " + locale);
        }
        int day = Integer.parseInt(matcher.group(1));
        int year = Year.now().getValue();
        if (MonthDay.of(month, day).isAfter(MonthDay.now())) {
            year--;
        }
        return LocalDateTime.of(year, month, day,
                Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)));
    }
}
