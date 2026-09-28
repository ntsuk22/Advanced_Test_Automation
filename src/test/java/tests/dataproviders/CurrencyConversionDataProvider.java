package tests.dataproviders;

import database.CurrencyConversionRepository;
import database.models.CurrencyConversion;
import org.testng.annotations.DataProvider;

import java.util.List;

public final class CurrencyConversionDataProvider {

    public static final String ACTIVE_CONVERSIONS = "activeCurrencyConversions";

    private CurrencyConversionDataProvider() {
    }

    @DataProvider(name = ACTIVE_CONVERSIONS)
    public static Object[][] activeConversions() {
        List<CurrencyConversion> rows = CurrencyConversionRepository.findActive();
        if (rows.isEmpty()) {
            throw new IllegalStateException("currency_conversion has no active rows - nothing to test");
        }
        return rows.stream().map(row -> new Object[]{row}).toArray(Object[][]::new);
    }
}
