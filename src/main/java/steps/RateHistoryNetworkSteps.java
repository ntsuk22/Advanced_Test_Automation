package steps;

import api.models.RateHistoryEntry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Request;
import com.microsoft.playwright.Response;
import components.RateHistoryDialogComponent;
import constants.AppLocale;
import pages.ExchangeRatesPage;
import utils.LocalizedText;
import utils.RateHistoryDateParser;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Captures the rate-history request the browser sends when a currency row is clicked,
 * validates it at network level and then checks the dialog against the captured response.
 */
public class RateHistoryNetworkSteps {

    private static final String HISTORY_ENDPOINT = "/api/v1/commercialRates/list";
    private static final int HISTORY_PERIOD_DAYS = 7;
    private static final ObjectMapper JSON = new ObjectMapper();

    private final AppLocale locale;
    private final ExchangeRatesPage ratesPage;
    private final Page page;

    private Response response;
    private List<RateHistoryEntry> entries;

    public RateHistoryNetworkSteps(Page page, AppLocale locale) {
        this.page = page;
        this.locale = locale;
        this.ratesPage = new ExchangeRatesPage(page, locale);
    }

    /** Clicks the row and synchronises on the history response itself - no arbitrary waiting. */
    public RateHistoryNetworkSteps openHistoryAndCaptureTraffic(String currencyCode) {
        assertThat(ratesPage.rateRow(currencyCode)).isVisible();
        response = page.waitForResponse(
                r -> URI.create(r.url()).getPath().equals(HISTORY_ENDPOINT),
                () -> ratesPage.openHistory(currencyCode));
        return this;
    }

    public RateHistoryNetworkSteps verifyRequest(String fromCurrency, String toCurrency) {
        Request request = response.request();
        assertEquals(URI.create(request.url()).getHost(), "apigw.tbcbank.ge", "History request host");
        assertEquals(URI.create(request.url()).getPath(), HISTORY_ENDPOINT, "History request endpoint");
        assertEquals(request.method(), "POST", "History request method");
        String contentType = request.headerValue("content-type");
        assertTrue(contentType != null && contentType.startsWith("application/json"), "Request content type: " + contentType);

        JsonNode query = readJson(request.postData(), "request body").path("queryModel");
        assertEquals(textList(query.path("ccyFrom")), List.of(fromCurrency), "queryModel.ccyFrom");
        assertEquals(textList(query.path("ccyTo")), List.of(toCurrency), "queryModel.ccyTo");

        LocalDate endDate = LocalDate.parse(query.path("endDate").asText());
        LocalDate startDate = LocalDate.parse(query.path("startDate").asText());
        LocalDate today = LocalDate.now();
        // The browser uses this machine's clock; accept yesterday too in case the click happened around midnight
        assertTrue(!endDate.isAfter(today) && !endDate.isBefore(today.minusDays(1)), "endDate should be today, was " + endDate);
        assertEquals(ChronoUnit.DAYS.between(startDate, endDate), HISTORY_PERIOD_DAYS, "Requested period " + startDate + ".." + endDate);
        return this;
    }

    public RateHistoryNetworkSteps verifyResponse(String fromCurrency, String toCurrency) {
        assertEquals(response.status(), 200, "History response status");
        try {
            entries = Arrays.asList(JSON.readValue(response.text(), RateHistoryEntry[].class));
        } catch (JsonProcessingException e) {
            throw new AssertionError("History response is not the expected JSON list: " + e.getOriginalMessage());
        }
        assertFalse(entries.isEmpty(), "History response is empty");

        LocalDateTime previous = null;
        for (RateHistoryEntry entry : entries) {
            assertEquals(entry.getCcyFrom(), fromCurrency, "ccyFrom of " + entry);
            assertEquals(entry.getCcyTo(), toCurrency, "ccyTo of " + entry);
            assertNotNull(entry.getBuy(), "buy missing: " + entry);
            assertNotNull(entry.getSell(), "sell missing: " + entry);
            assertTrue(entry.getBuy().signum() > 0 && entry.getSell().signum() > 0, "Rates must be positive: " + entry);
            assertTrue(entry.getBuy().compareTo(entry.getSell()) < 0, "buy must be lower than sell: " + entry);

            LocalDateTime start = LocalDateTime.parse(entry.getStartDate());
            if (previous != null) {
                assertFalse(start.isAfter(previous), "Entries must be newest first: " + entry);
            }
            previous = start;
        }
        return this;
    }

    public RateHistoryNetworkSteps verifyDialogMatchesResponse(String currencyCode) {
        RateHistoryDialogComponent dialog = ratesPage.historyDialog();
        assertThat(dialog.root()).isVisible();
        assertThat(dialog.currencyBadge()).hasText(currencyCode);
        assertThat(dialog.title()).hasText(LocalizedText.get(locale, "rates.history.title"));
        // One UI row per response entry; retries until the grid has rendered the response
        assertThat(dialog.rows()).hasCount(entries.size());
        assertThat(dialog.unfilledRows()).hasCount(0);

        RateHistoryEntry newest = entries.get(0);
        Map<String, String> firstRow = dialog.entries().get(0);
        LocalDateTime uiDate = RateHistoryDateParser.parse(firstRow.get("date"), locale);
        assertEquals(uiDate, LocalDateTime.parse(newest.getStartDate()).truncatedTo(ChronoUnit.MINUTES),
                "First row date vs response " + newest);
        assertSameNumber(firstRow.get("buyRate"), newest.getBuy(), "First row buy rate");
        assertSameNumber(firstRow.get("sellRate"), newest.getSell(), "First row sell rate");
        return this;
    }

    private static JsonNode readJson(String text, String what) {
        assertNotNull(text, what + " is empty");
        try {
            return JSON.readTree(text);
        } catch (JsonProcessingException e) {
            throw new AssertionError(what + " is not valid JSON: " + text);
        }
    }

    private static List<String> textList(JsonNode array) {
        assertTrue(array.isArray(), "Expected a JSON array but got " + array);
        return StreamSupport.stream(array.spliterator(), false).map(JsonNode::asText).toList();
    }

    private static void assertSameNumber(String uiText, BigDecimal expected, String what) {
        assertEquals(new BigDecimal(uiText.replace(",", "").trim()).compareTo(expected), 0,
                what + ": UI " + uiText + " vs response " + expected);
    }
}
