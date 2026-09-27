package utils;

import constants.AppLocale;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

/**
 * Locale-specific UI texts, stored in resources/testdata/locales/{ka|en}.properties.
 */
public final class LocalizedText {

    private static final Map<AppLocale, Properties> CACHE = new EnumMap<>(AppLocale.class);

    private LocalizedText() {
    }

    public static String get(AppLocale locale, String key) {
        String value = properties(locale).getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing text '" + key + "' for locale " + locale);
        }
        return value;
    }

    private static synchronized Properties properties(AppLocale locale) {
        return CACHE.computeIfAbsent(locale, LocalizedText::load);
    }

    private static Properties load(AppLocale locale) {
        String resource = "testdata/locales/" + locale.path() + ".properties";
        try (InputStream in = LocalizedText.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException(resource + " not found on classpath");
            }
            Properties properties = new Properties();
            properties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + resource, e);
        }
    }
}
