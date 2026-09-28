package steps;

import api.clients.ExchangeRatesClient;
import api.models.CommercialRatesResponse;
import api.models.CurrencyRate;
import api.models.ValidationErrorResponse;
import constants.AppLocale;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;
import utils.LocalizedText;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class ExchangeRatesApiSteps {

    private static final Pattern ISO_CODE = Pattern.compile("^[A-Z]{3}$");
    private static final String VALIDATION_TITLE = "One or more validation errors occurred.";
    /** Tolerated difference between the API server clock and this machine. */
    private static final long CLOCK_SKEW_MINUTES = 5;

    private final ExchangeRatesClient client = new ExchangeRatesClient();

    private CommercialRatesResponse rates;
    private ValidationErrorResponse validationError;
    private Response lastResponse;

    // ---------- Happy path (SCRUM-T34) ----------

    public ExchangeRatesApiSteps requestCommercialRates(AppLocale locale) {
        rates = fetchCommercialRates(locale);
        return this;
    }

    private CommercialRatesResponse fetchCommercialRates(AppLocale locale) {
        Response response = client.getCommercialRates(locale.apiLocale());
        assertEquals(response.statusCode(), 200, "commercialList status. Body: " + response.asString());
        assertTrue(response.contentType().contains("application/json"), "Unexpected content type " + response.contentType());

        CommercialRatesResponse body = response.as(CommercialRatesResponse.class, ObjectMapperType.JACKSON_2);
        assertNotNull(body.getRates(), "'rates' list is missing");
        assertNotNull(body.getUpdateDateTime(), "'updateDateTime' is missing");
        return body;
    }

    public ExchangeRatesApiSteps verifyEveryRateIsValid() {
        List<CurrencyRate> list = rates.getRates();
        assertFalse(list.isEmpty(), "'rates' list is empty");

        Set<String> seen = new HashSet<>();
        for (CurrencyRate rate : list) {
            assertNotNull(rate.getIso(), "Rate without iso: " + rate);
            assertTrue(ISO_CODE.matcher(rate.getIso()).matches(), "Invalid ISO code: " + rate);
            assertTrue(seen.add(rate.getIso()), "Duplicate currency " + rate.getIso());
            assertTrue(rate.getName() != null && !rate.getName().isBlank(), "Empty name: " + rate);
            assertPositive(rate.getBuyRate(), "buyRate", rate);
            assertPositive(rate.getSellRate(), "sellRate", rate);
            assertPositive(rate.getWeight(), "weight", rate);
            assertTrue(rate.getBuyRate().compareTo(rate.getSellRate()) < 0, "buyRate must be lower than sellRate: " + rate);
        }
        return this;
    }

    /** Checks name (in the requested locale), unit weight and that the official rate lies inside the buy/sell spread. */
    public ExchangeRatesApiSteps verifyMajorCurrency(String iso, AppLocale locale) {
        CurrencyRate rate = rate(rates, iso);
        assertEquals(rate.getName(), LocalizedText.get(locale, "currency.name." + iso), iso + " name for " + locale);
        assertEquals(rate.getWeight().compareTo(BigDecimal.ONE), 0, iso + " should be quoted per 1 unit: " + rate);
        assertNotNull(rate.getOfficialCourse(), iso + " has no officialCourse");
        assertTrue(rate.getBuyRate().compareTo(rate.getOfficialCourse()) <= 0
                        && rate.getOfficialCourse().compareTo(rate.getSellRate()) <= 0,
                iso + " official rate should lie between buy and sell: " + rate);
        return this;
    }

    public ExchangeRatesApiSteps verifyUpdateTimeIsValid() {
        OffsetDateTime updated;
        try {
            updated = OffsetDateTime.parse(rates.getUpdateDateTime());
        } catch (DateTimeParseException e) {
            fail("updateDateTime is not an ISO-8601 date-time: " + rates.getUpdateDateTime());
            return this;
        }
        assertFalse(updated.isAfter(OffsetDateTime.now().plusMinutes(CLOCK_SKEW_MINUTES)),
                "updateDateTime is in the future: " + updated);
        return this;
    }

    /**
     * Requests the same list in another locale: currencies and rates must be identical, only names are translated.
     */
    public ExchangeRatesApiSteps verifySameRatesTranslatedTo(AppLocale otherLocale, String... isos) {
        CommercialRatesResponse other = fetchCommercialRates(otherLocale);
        assertEquals(isoCodes(other), isoCodes(rates), "Currency list differs between locales");

        for (String iso : isos) {
            CurrencyRate original = rate(rates, iso);
            CurrencyRate translated = rate(other, iso);
            assertEquals(translated.getName(), LocalizedText.get(otherLocale, "currency.name." + iso),
                    iso + " name for " + otherLocale);
            assertEquals(translated.getBuyRate().compareTo(original.getBuyRate()), 0,
                    iso + " buyRate differs between locales: " + original + " vs " + translated);
            assertEquals(translated.getSellRate().compareTo(original.getSellRate()), 0,
                    iso + " sellRate differs between locales: " + original + " vs " + translated);
        }
        return this;
    }

    // ---------- Negative (SCRUM-T35) ----------

    public ExchangeRatesApiSteps requestExchangeRateExpectingValidationError(String fromIso, String toIso) {
        lastResponse = client.getExchangeRate(fromIso, toIso);
        assertEquals(lastResponse.statusCode(), 400,
                "Invalid Iso1=" + fromIso + "&Iso2=" + toIso + " should be rejected. Body: " + lastResponse.asString());
        // ASP.NET returns application/problem+json, which is JSON but not "application/json"
        assertTrue(lastResponse.contentType().contains("json"), "Unexpected content type " + lastResponse.contentType());

        validationError = lastResponse.as(ValidationErrorResponse.class, ObjectMapperType.JACKSON_2);
        assertEquals(validationError.getStatus(), 400, "'status' in body");
        assertEquals(validationError.getTitle(), VALIDATION_TITLE, "'title' in body");
        assertTrue(validationError.getTraceId() != null && !validationError.getTraceId().isBlank(), "'traceId' is missing");
        assertNotNull(validationError.getErrors(), "'errors' object is missing");
        return this;
    }

    /** {@code field} is the query parameter name, or "" for errors about the request as a whole. */
    public ExchangeRatesApiSteps verifyValidationError(String field, String expectedMessage) {
        List<String> messages = validationError.getErrors().get(field);
        assertNotNull(messages, "No validation errors for '" + field + "'. Errors: " + validationError.getErrors());
        assertTrue(messages.contains(expectedMessage),
                "Expected '" + expectedMessage + "' for '" + field + "', got " + messages);
        return this;
    }

    public ExchangeRatesApiSteps verifyNoValidationErrorFor(String field) {
        assertFalse(validationError.getErrors().containsKey(field),
                "Valid parameter '" + field + "' should not be reported: " + validationError.getErrors());
        return this;
    }

    public ExchangeRatesApiSteps verifyNoRateDataReturned() {
        assertNotNull(lastResponse, "No request was sent");
        assertTrue(lastResponse.jsonPath().get("buyRate") == null && lastResponse.jsonPath().get("sellRate") == null,
                "Error response must not contain rate data: " + lastResponse.asString());
        return this;
    }

    // ---------- helpers ----------

    private static CurrencyRate rate(CommercialRatesResponse response, String iso) {
        return response.rate(iso).orElseThrow(() -> new AssertionError(iso + " is missing from the rates list"));
    }

    private static List<String> isoCodes(CommercialRatesResponse response) {
        return response.getRates().stream().map(CurrencyRate::getIso).toList();
    }

    private static void assertPositive(BigDecimal value, String field, CurrencyRate rate) {
        assertNotNull(value, field + " is missing: " + rate);
        assertTrue(value.signum() > 0, field + " must be positive: " + rate);
    }
}
