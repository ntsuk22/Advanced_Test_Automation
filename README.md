# TBC Digital — Advanced Test Automation (Final Project 2)

tbcbank.ge-ის ავტომატიზაციის ფრეიმვორკი: UI (Playwright), ლოკალიზაცია, SQL-ზე დაფუძნებული data-driven ტესტი (MyBatis + DataProvider), API (Rest Assured), API → UI და ბრაუზერის network ვალიდაცია. ყოველი ტესტი მიბმულია Zephyr Scale-ის სცენარზე.

| | |
|---|---|
| სამიზნე | https://tbcbank.ge, API: https://apigw.tbcbank.ge/api/v1 |
| სტეკი | Java 21, Maven, Playwright 1.63.0, TestNG 7.12.0, MyBatis 3.5.19, H2 2.5.252, Rest Assured 6.0.1, Jackson 2.22.3 |
| ტესტები | 11 ტესტ-მეთოდი, 15 გაშვება (DataProvider-ების ჩათვლით) |
| Zephyr | SCRUM-T27 … SCRUM-T37 |

---

## გაშვება

**საჭიროა:** JDK 21 ან უფრო ახალი, Maven 3.9+. Playwright ბრაუზერებს პირველი გაშვებისას თვითონ ჩამოტვირთავს.

```bash
mvn test                                   # სრული სუიტი (testng.xml), 3 პარალელური thread
mvn test -Dthreads=1 -Dheadless=false      # ერთ thread-ზე, ხილული ბრაუზერით
mvn test -Dbrowser=firefox                 # chromium | firefox | webkit
mvn test -Dtest=LocationsTest#searchLocationsByCity   # ერთი ტესტი
```

IntelliJ-ში: მარჯვენა ღილაკი `testng.xml` → **Run**.

ყველა პარამეტრი [config.properties](src/main/resources/config.properties)-შია (`base.url`, `api.base.url`, `browser`, `headless`, `slow.mo`, viewport, timeout-ები, `threads`, ბაზის `db.*`, geolocation). ნებისმიერი მათგანი `-Dkey=value`-ით იცვლება, ფაილის შეცვლა საჭირო არ არის. `threads`-ს runtime-ში [SuiteConfigListener](src/main/java/utils/SuiteConfigListener.java) აყენებს, ამიტომ `testng.xml`-ის რედაქტირება არ სჭირდება.

ჩავარდნისას screenshot ინახება `target/screenshots/<method>.png`-ში.

> **tbcbank.ge-ის WAF.** მოკლე დროში ბევრი გაშვების შემდეგ საიტი დროებით აბრუნებს გვერდს „Your request has been blocked, Error code: 101“. ტესტი ამ დროს მაშინვე ჩერდება მკაფიო შეტყობინებით ([BasePage.failIfBlockedBySite](src/main/java/pages/BasePage.java)). ასეთ შემთხვევაში რამდენიმე წუთს დაელოდე ან გაუშვი `-Dthreads=1`-ით.

---

## ტესტები და Zephyr traceability

ყოველი `@Test` Zephyr-ის ID-ით იწყება: `@Test(description = "SCRUM-T27 | Locations - search branches and ATMs by city")`. ტესტ-რეპორტში სახელი ამ ფორმით ჩანს, ამიტომ ჩავარდნილი ტესტი მაშინვე შესაბამის სცენარს უკავშირდება.

| Zephyr | სექცია | ტესტი | ენა | გაშვებები |
|---|---|---|---|---|
| SCRUM-T27 | UI | `LocationsTest.searchLocationsByCity` | EN | 1 |
| SCRUM-T28 | UI | `LocationsTest.filterLocationsByType` | EN | 1 |
| SCRUM-T29 | UI | `ExchangeRatesTest.viewRateHistory` | KA | 1 |
| SCRUM-T30 | UI | `SiteSearchTest.searchProductAndOpenIt` | KA | 1 |
| SCRUM-T31 | UI | `ExchangeRatesTest.convertCurrency` | KA | 1 |
| SCRUM-T32 | Localization | `LocalizationTest.exchangeRatesJourneyIsLocalized` | KA↔EN | 2 |
| SCRUM-T33 | SQL + MyBatis + DataProvider | `CurrencyConverterDataDrivenTest.convertCurrencyFromDatabase` | KA | 4 (ბაზის 4 ჩანაწერი) |
| SCRUM-T34 | API (happy) | `ExchangeRatesApiTest.getCommercialRates` | — | 1 |
| SCRUM-T35 | API (negative) | `ExchangeRatesApiTest.getExchangeRateWithInvalidParameters` | — | 1 |
| SCRUM-T36 | API → UI | `ApiToUiConsistencyTest.exchangeRatesTableMatchesApi` | KA | 1 |
| SCRUM-T37 | Network | `NetworkValidationTest.rateHistoryRequestAndResponse` | KA | 1 |

UI სცენარებიდან მარტივ ნავიგაციაზე რთულია:
- **T28:** სამი ტაბის რაოდენობების ჯამი უდრის „All“-ს.
- **T29:** ისტორიის თარიღები დალაგებულია და ყიდვის კურსი ნაკლებია გაყიდვისაზე.
- **T31:** კონვერტაციის შედეგი ცხრილის კურსით ითვლება, swap-ის შემდეგ კი გაყიდვის კურსით.

---

## პროექტის სტრუქტურა

```
src/main/java/
  pages/        BasePage, HomePage, LocationsPage, ExchangeRatesPage, ProductPage
  components/   Header, LanguageSwitcher, CookieBanner, QuickActions, Breadcrumbs, TabMenu,
                SearchOverlay, CurrencyConverter, RateHistoryDialog (+ BaseComponent)
  steps/        Home, Header, Locations, ExchangeRates, Search, ExchangeRatesApi,
                ExchangeRatesApiToUi, RateHistoryNetwork
  api/clients/  BaseApiClient, ExchangeRatesClient
  api/models/   CommercialRatesResponse, CurrencyRate, ValidationErrorResponse, RateHistoryEntry
  database/     MyBatisSessionFactory, CurrencyConversionRepository
  database/mappers/  CurrencyConversionMapper
  database/models/   CurrencyConversion
  utils/        PlaywrightFactory, ConfigReader, LocalizedText, SuiteConfigListener,
                RateHistoryDateParser, NumberUtils, RegexUtils
  constants/    AppLocale, PagePaths, LocationType
src/main/resources/
  config.properties
  mybatis/      mybatis-config.xml, mappers/CurrencyConversionMapper.xml
  testdata/     locales/ka.properties, locales/en.properties, sql/schema.sql, sql/data.sql
src/test/java/tests/
  BaseTest + 8 ტესტ-კლასი, dataproviders/CurrencyConversionDataProvider
pom.xml, testng.xml, README.md
```

---

## Framework Architecture

ტესტში გამოძახებების ჯაჭვი ასეთია: **tests → steps → pages → components**. თითოეულ ფენას ერთი პასუხისმგებლობა აქვს.

| საქაღალდე | პასუხისმგებლობა ამ პროექტში |
|---|---|
| `tests/` | მხოლოდ სცენარის თანმიმდევრობა და ტესტის მონაცემები, მაგალითად `"Batumi"` ან `"USD"`. ტესტში არც სელექტორი წერია და არც `assert`. [BaseTest](src/test/java/tests/BaseTest.java) ყოველ ტესტს ცალკე ბრაუზერს უხსნის და ჩავარდნისას screenshot-ს იღებს. API ტესტი (`ExchangeRatesApiTest`) `BaseTest`-ს არ აფართოებს (extends), ამიტომ ბრაუზერს არ ხსნის. |
| `steps/` | ბიზნეს-მოქმედებები და შემოწმებები, fluent API-ით: `openFromQuickActions()`, `selectTypeAndVerifyOnlyThatType()`, `verifyHistoryIsValid()`. აქ წყდება, **რა** მოწმდება. აქ არც ერთი CSS სელექტორი არ წერია. |
| `pages/` | გვერდის სელექტორები და ტექნიკური ქმედებები (URL ლოკალის პრეფიქსით, ცხრილის წაკითხვა, network მოვლენის ლოდინი). [BasePage](src/main/java/pages/BasePage.java) ყველა გვერდს ერთნაირ კომპონენტებს აძლევს: Header, CookieBanner, QuickActions და Breadcrumbs. |
| `components/` | UI-ის ფრაგმენტები, რომლებიც რამდენიმე გვერდზე მეორდება. თითოეულს აქვს საკუთარი root locator და ყველა სელექტორი ამ root-იდან ითვლება, ამიტომ ერთი და იგივე კლასი ნებისმიერ გვერდზე მუშაობს. |
| `api/` | `clients/`: Rest Assured-ის მოთხოვნები (base URI, `Accept: JSON`, `Origin`). `models/`: POJO-ები, რომლებშიც პასუხები დესერიალიზდება (Jackson, `@JsonIgnoreProperties(ignoreUnknown = true)`). |
| `database/` | MyBatis: `SqlSessionFactory` config-იდან, mapper-ის ინტერფეისი (`mappers/`), ცხრილის Java მოდელი (`models/`) და repository, რომელსაც DataProvider იძახებს. |
| `utils/`, `constants/` | Playwright-ის სასიცოცხლო ციკლი (ThreadLocal), კონფიგურაცია, ლოკალიზებული ტექსტები, პარსერები; enum-ები `AppLocale` და `LocationType`, გვერდების path-ები. |

**რატომ არის Components ცალკე Page Object-ებისგან.** tbcbank.ge-ზე ჰედერი, cookie banner, sticky quick-action მენიუ, breadcrumb და tab menu ყველა ან რამდენიმე გვერდზე ერთნაირადაა აწყობილი. ცალკე კომპონენტად გატანის გამო მათი სელექტორი და ქცევა ერთ ადგილას წერია:
- **Cookie banner:** საიტის ერთ-ერთმა განახლებამ ღილაკების CSS კლასები შეცვალა. ამის გამოსწორებას მხოლოდ `CookieBannerComponent.acceptAll()` დასჭირდა, და ყველა ტესტი ისევ გავიდა ისე, რომ არც ერთი გვერდი და steps არ შეცვლილა.
- **Quick-action მენიუ:** ღილაკები ერთმანეთზეა დაწყობილი, სანამ kebab ღილაკით მენიუ არ გაიხსნება. ეს ლოგიკა `QuickActionsComponent.expand()`-შია ჩაწერილი, ამიტომ არც ერთ გვერდს და steps-ს ამაზე ფიქრი არ სჭირდება.

**გამოყენების კონკრეტული მაგალითი: `TabMenuComponent`.** ორი სხვადასხვა გვერდი ერთსა და იმავე კლასს იყენებს: `TabMenuComponent.firstOn(page)`.
- `LocationsPage`-ზე ის ATMs / Branches / CDMs ფილტრია (T28).
- `ExchangeRatesPage`-ზე ის კურსების ტიპის გადამრთველია: „კომერციული კურსები“ / „Commercial“ (T29, T31–T33, T36, T37).

ორივე გვერდი იყენებს ერთსა და იმავე `select(label)`-სა და `activeTab()`-ს. აქტიური ტაბის სელექტორი (`.tbcx-pw-tab-menu__item.active`) მხოლოდ კომპონენტშია და არც ერთ გვერდზე არ მეორდება.

კიდევ ერთი მაგალითია `HeaderComponent`, რომელიც `BasePage`-ის ნაწილია:
- T30 იყენებს მის ძებნას: `openSearch()` → `SearchOverlayComponent`.
- T32 იყენებს მასში ჩადგმულ `LanguageSwitcherComponent`-ს.
- T32 ორივე გვერდზე ამოწმებს ნავიგაციის ლეიბლს (`HeaderSteps.verifyLocalized()`): ჯერ მთავარზე, შემდეგ ვალუტის კურსებზე.

---

## Localization Strategy

**ერთი ტესტი, ორი ენა.** [LocalizationTest](src/test/java/tests/LocalizationTest.java)-ს ერთი მეთოდი აქვს. DataProvider მას ორი ჩანაწერით უშვებს: `{KA, EN}` და `{EN, KA}`, ანუ საწყისი ენა და ენა, რომელზეც გადადის. ლოგიკა ორივე ენაზე ერთი და იგივეა:

1. მთავარი გვერდი საწყის ენაზე → შემოწმდება ჰედერი.
2. ვალუტის კურსები → შემოწმდება h1, breadcrumb, აქტიური ტაბი და USD-ის დასახელება.
3. USD-ის ისტორია → შემოწმდება სათაური და თარიღები საწყის ენაზე.
4. ენის შეცვლა ჰედერში. `switchLanguageTo()` აბრუნებს ახალ `ExchangeRatesSteps`-ს, რომელიც მეორე ენაზეა მიბმული, და ამოწმებს, რომ მომხმარებელი **იმავე გვერდზე დარჩა**: URL `/en/valutis-kursi`-დან `/ka/valutis-kursi`-ზე გადავიდა.
5. იგივე შემოწმებები მეორე ენაზე, შემდეგ ისტორია ხელახლა იხსნება. ეს ამოწმებს, რომ journey ენის შეცვლის შემდეგაც მუშაობს.

**სად ინახება ენის მონაცემები.**
- [AppLocale](src/main/java/constants/AppLocale.java) თითოეული ენისთვის ინახავს URL-ის პრეფიქსს (`ka`, `en`) და API-ის locale-ს (`ka-GE`, `en-US`). ყველა გვერდის URL `BasePage.localizedPath()`-ით აიწყობა.
- მოსალოდნელი ტექსტები მხოლოდ ორ ფაილშია: [testdata/locales/ka.properties](src/main/resources/testdata/locales/ka.properties) და [en.properties](src/main/resources/testdata/locales/en.properties). მაგალითად, `rates.heading=ვალუტის კურსი` / `Exchange Rates`, `rates.history.title=კურსის ისტორია` / `Currency history`.
- თვეების სახელებიც ამ ფაილებშია: `months=იანვარი,...` / `January,...`. [RateHistoryDateParser](src/main/java/utils/RateHistoryDateParser.java) ისტორიის თარიღს („25 სექტემბერი, 11:54“) მიმდინარე ენის თვეებით კითხულობს. თუ საიტმა თარიღი სხვა ენაზე აჩვენა, ტესტი ჩავარდება.

**ერთი და იგივე ფაილები ყველგან.** ამ ფაილებს იყენებს UI ტესტებიც (მაგალითად, ტაბების ლეიბლები T28-ში, „ყველა ვალუტა“ T36-ში) და API ტესტიც (ვალუტების სახელები T34-ში). ამიტომ ერთი ტექსტი პროექტში მხოლოდ ერთხელ წერია. [LocalizedText](src/main/java/utils/LocalizedText.java) ფაილებს UTF-8-ით კითხულობს და თუ key არ არსებობს, ტესტს მაშინვე აჩერებს. ასე ახალი ენის ფაილში გამოტოვებული ტექსტი შეუმჩნეველი არ დარჩება.

**ახალი ენის დამატება**, მაგალითად რუსულის:
1. `AppLocale`-ში ახალი მნიშვნელობა: `RU("ru", "ru-RU")`.
2. `testdata/locales/ru.properties` იგივე key-ებით, რაც `ka`/`en`-შია.
3. `LocalizationTest`-ის DataProvider-ში ახალი რიგი, მაგალითად `{AppLocale.RU, AppLocale.KA}`.

ტესტ-მეთოდი, steps და pages არ იცვლება. ერთი შენიშვნა: tbcbank.ge-ის გადამრთველი ორ ენას შორის მუშაობს (ღილაკზე ყოველთვის **მეორე** ენა წერია). მესამე ენისთვის `LanguageSwitcherComponent.toggle()`-ს dropdown-იდან არჩევა დასჭირდება.

---

## SQL & Test Data Strategy

**რატომ SQL და MyBatis.**
- **მონაცემები კოდისგან ცალკეა.** ახალი ვარიაცია SQL-ის ერთი რიგია, Java კოდის ცვლილება კი საჭირო არ არის.
- **ბაზა არასწორ მონაცემს არ მიიღებს:** `CHECK (amount > 0)`, `UNIQUE (currency_code, amount)`, `NOT NULL`.
- **`active` სვეტით** ჩანაწერის დროებით გამორთვა შეიძლება, მისი წაშლის გარეშე.
- **MyBatis-ის როლი:** SQL XML-შია, შედეგი კი `resultMap`-ით პირდაპირ Java ობიექტად გარდაიქმნება. JDBC-ის ხელით დაწერილი კოდი (`ResultSet`, კავშირის მართვა) საჭირო არ არის.
- **ბაზა H2-ია** და მეხსიერებაში (in-memory) იქმნება. ცალკე სერვერი არ სჭირდება და ყოველი გაშვება ერთი და იგივე მონაცემებით იწყება.

**გზა Database → MyBatis → Java Model → DataProvider → Test:**

1. **Database.** [schema.sql](src/main/resources/testdata/sql/schema.sql) ქმნის ცხრილს `currency_conversion` (`currency_code`, `currency_name`, `amount`, `active`). [data.sql](src/main/resources/testdata/sql/data.sql) შეიცავს 4 ჩანაწერს: USD 250, EUR 100, GBP 75, CHF 40. ორივე სკრიპტი ავტომატურად სრულდება ბაზასთან პირველი კავშირისას: `db.url`-ში წერია `INIT=RUNSCRIPT FROM 'classpath:...'`. `MERGE ... KEY` სკრიპტს განმეორებით გაშვებისთვის უსაფრთხოს ხდის.
2. **MyBatis.** [MyBatisSessionFactory](src/main/java/database/MyBatisSessionFactory.java) კითხულობს [mybatis-config.xml](src/main/resources/mybatis/mybatis-config.xml)-ს (POOLED datasource) და კავშირის პარამეტრებს `config.properties`-იდან იღებს. [CurrencyConversionMapper.xml](src/main/resources/mybatis/mappers/CurrencyConversionMapper.xml) შეიცავს `SELECT ... WHERE active = TRUE ORDER BY id`-ს და `resultMap`-ს (`currency_code` → `currencyCode` და ა.შ.).
3. **Java Model.** [CurrencyConversion](src/main/java/database/models/CurrencyConversion.java). `amountAsInput()` თანხას ისე აბრუნებს, როგორც მომხმარებელი აკრეფს (`250.00` → `"250"`). `toString()` რეპორტში ჩანს, მაგალითად `#2 EUR 100`, ამიტომ ცნობილია, რომელი ჩანაწერი ჩავარდა.
4. **DataProvider.** [CurrencyConversionDataProvider](src/test/java/tests/dataproviders/CurrencyConversionDataProvider.java) იძახებს `CurrencyConversionRepository.findActive()`-ს და ყოველ ჩანაწერს ცალკე გაშვებად გადასცემს. თუ ცხრილი ცარიელია, შეცდომას აგდებს, რომ „0 ტესტი, 0 ჩავარდნა“ წარმატებად არ ჩაითვალოს.
5. **Test.** [CurrencyConverterDataDrivenTest](src/test/java/tests/CurrencyConverterDataDrivenTest.java) ერთი მეთოდია და ოთხჯერ ეშვება. თითოეულ გაშვებაზე:
   - ცხრილში მოწმდება ვალუტის დასახელება, რომელიც ბაზიდან მოდის (`1 ევრო`);
   - კონვერტორში ირჩევა ვალუტა;
   - იწერება თანხა;
   - მოწმდება, რომ შედეგი ტოლია თანხა × ცხრილის ყიდვის კურსი.

**ახალი ვარიაციის დამატება** ტესტის კოდის შეცვლის გარეშე: `data.sql`-ის `VALUES`-ს ემატება ერთი რიგი:

```sql
('CAD', '1 კანადური დოლარი', 500.00, TRUE)
```

შემდეგ გაშვებაზე DataProvider 5 ჩანაწერს დააბრუნებს და ტესტი 5-ჯერ გაეშვება. ჩანაწერის დროებით გამოსართავად საკმარისია `active = FALSE`. სხვა ბაზაზე, მაგალითად MySQL-ზე, გადასასვლელად საჭიროა `-Ddb.url=... -Ddb.driver=...` და ამ ბაზის JDBC driver-ი `pom.xml`-ში.

შეზღუდვა: ბაზაში მხოლოდ ისეთი ვალუტა უნდა იყოს, რომლის კურსი 1 ერთეულზეა (API-ის `weight = 1`). CNY-ის კურსი 10 ერთეულზეა, JPY-ისა 100-ზე, ამიტომ მათთვის `თანხა × კურსი` ფორმულა არასწორი იქნებოდა.

---

## API → UI Strategy

**რომელი API:** `GET /api/v1/exchangeRates/commercialList?locale=ka-GE`. ეს ის endpoint-ია, რომლითაც ვალუტის კურსების გვერდი თავის ცხრილს ავსებს (ეს ბრაუზერის ტრაფიკში დადასტურდა). ამიტომ API-ის პასუხი ზუსტად ის მონაცემია, რაც მომხმარებელმა ეკრანზე უნდა ნახოს. პასუხი `CommercialRatesResponse`-ში დესერიალიზდება, რომელიც შეიცავს `List<CurrencyRate>`-ს: 20 ვალუტა, ველებით `iso`, `name`, `weight`, `buyRate`, `sellRate`, `officialCourse`.

**API სიმართლის წყაროა.** [ApiToUiConsistencyTest](src/test/java/tests/ApiToUiConsistencyTest.java) ჯერ Rest Assured-ით იღებს პასუხს და ამოწმებს, რომ ის ვალიდურია. შემდეგ Playwright-ით ხსნის გვერდს. [ExchangeRatesApiToUiSteps](src/main/java/steps/ExchangeRatesApiToUiSteps.java) ყოველ მოსალოდნელ მნიშვნელობას POJO-დან იღებს, UI-ში არაფერი წერია hardcoded-ად.

**რა ედრება და რატომ:**

| UI | API | რატომ ეს მნიშვნელობა |
|---|---|---|
| რიგის დასახელება „CNY 10 ჩინური იუანი“ | `iso` + `weight` + `name` | `weight` განსაზღვრავს, რამდენ ერთეულზეა კურსი. თუ UI-ში „1 ჩინური იუანი“ ეწერება, როცა კურსი 10-ზეა, მომხმარებელი 10-ჯერ არასწორ ფასს ნახავს |
| ყიდვა / გაყიდვა | `buyRate` / `sellRate` | ამ კურსებით ხდება გადაცვლა. ეს ყველაზე მნიშვნელოვანი ბიზნეს-მონაცემია |
| ოფიციალური კურსი | `officialCourse` | ეროვნული ბანკის კურსი, მომხმარებლის ორიენტირი |
| რიგების რაოდენობა „ყველა ვალუტა“-ს შემდეგ | `rates.size()` | ამოწმებს, რომ სრული სია ნაჩვენებია |

ჯერ მოწმდება USD და EUR, რომლებიც გვერდის გახსნისას ჩანს. შემდეგ ტესტი აჭერს „ყველა ვალუტა“-ს, ელოდება, სანამ რიგების რაოდენობა API-ის სიის ზომას გაუტოლდება (`hasCount(20)`), და **20-ვე ვალუტას** ამოწმებს. რიცხვები `BigDecimal`-ით და რიცხობრივად ედრება, რადგან UI ბოლო ნულებს აშორებს (API `2.5530` → UI `2.553`).

**რა შეუსაბამობებს დაიჭერს:**
- UI-ში ძველი ან ქეშირებული კურსები, მაშინ როცა API უკვე განახლებულია;
- არეული სვეტები (ყიდვა და გაყიდვა ადგილს გაცვლია);
- დამრგვალება, რომელიც სიზუსტეს კარგავს (მაგალითად `0.06202` → `0.062`);
- არასწორი ერთეული (`weight`) დასახელებაში;
- API-ში არსებული ვალუტა, რომელიც ცხრილში არ ჩანს, ან ზედმეტი რიგი;
- არასწორად ნათარგმნი ან სხვა ენის დასახელება (API `ka-GE`-ით ითხოვს).

---

## Network Validation

**UI მოქმედება:** [NetworkValidationTest](src/test/java/tests/NetworkValidationTest.java)-ში მომხმარებელი კურსების ცხრილში EUR-ის რიგზე აჭერს. ეს ხსნის „კურსის ისტორიის“ ფანჯარას.

**მონიტორინგის request/response:** `POST https://apigw.tbcbank.ge/api/v1/commercialRates/list`, body:

```json
{"top":200,"skip":0,"queryModel":{"ccyFrom":["EUR"],"ccyTo":["GEL"],"startDate":"2026-09-21","endDate":"2026-09-28","sourcePage":1}}
```

**სინქრონიზაცია.** [RateHistoryNetworkSteps](src/main/java/steps/RateHistoryNetworkSteps.java) იძახებს `page.waitForResponse(predicate, () -> ratesPage.openHistory("EUR"))`-ს. listener-ი დაჭერამდე რეგისტრირდება, ამიტომ response არ გამოგვეპარება. predicate ზუსტად `/api/v1/commercialRates/list` path-ს ემთხვევა. ფიქსირებული ლოდინი ან polling არ არის.

**რა მოწმდება network-ის დონეზე.**

*Request:*
- host (`api.base.url`-იდან), endpoint, მეთოდი `POST`, `Content-Type: application/json`;
- `queryModel.ccyFrom == ["EUR"]` და `queryModel.ccyTo == ["GEL"]`;
- `endDate` დღევანდელი თარიღია (შუაღამესთან დამთხვევის შემთხვევაში გუშინდელიც მიიღება);
- `startDate` ზუსტად 7 დღით ადრეა.

*Response:*
- სტატუსი `200`, JSON სია დესერიალიზდება `RateHistoryEntry[]`-ში;
- ყოველ ჩანაწერში `ccyFrom = EUR` და `ccyTo = GEL`;
- `buy` და `sell` დადებითია, `buy < sell`;
- ჩანაწერები `startDate`-ით უახლესიდან ძველისკენაა დალაგებული.

**რა მოწმდება UI-ში, captured response-ის მიხედვით:**
- ფანჯარა ხილვადია, badge-ზე `EUR` წერია, სათაური ლოკალიზებულია („კურსის ისტორია“);
- ცხრილის რიგების რაოდენობა response-ის ჩანაწერების რაოდენობას უდრის (`hasCount(n)`), და შეუვსებელი რიგი აღარ დარჩა;
- პირველი რიგის თარიღი („28 სექტემბერი, 22:56“, ქართული თვით) უდრის პირველი ჩანაწერის `startDate`-ს წუთის სიზუსტით;
- პირველი რიგის ყიდვა და გაყიდვა ემთხვევა `buy`/`sell`-ს.

---

## Test Stability

**არჩეული სცენარი: SCRUM-T28** (`LocationsTest.filterLocationsByType`). ტესტი ATMs → Branches → CDMs ტაბებს რიგრიგობით ცვლის, ყოველ ჯერზე 60–900 ჩანაწერს ამოწმებს და ბოლოს რაოდენობების ჯამს „All“-ს ადარებს (906 + 130 + 63 = 1099).

**1. ძველი სია ტაბის შეცვლის შემდეგ (race condition).** ტაბზე დაჭერის შემდეგ ახალი სია ასინქრონულად იტვირთება და ძველი რიგები რამდენიმე წამს DOM-ში რჩება. Branches → CDMs გადასვლისას ეს განსაკუთრებით სახიფათოა: ორივე ტიპს ერთი და იგივე აიქონი აქვს (`bank-outlined`), ამიტომ „ATM აღარ ჩანს“ შემოწმება **ძველ** Branches სიაზეც გაივლიდა და ტესტი CDM-ის ნაცვლად 130-ს დათვლიდა.

*როგორ მოვაგვარე:* `LocationsPage.selectType()` დაჭერას `waitForResponse`-ში ახვევს და `POST /atmsAndBranches/list` response-ს ელოდება. `LocationsSteps.waitForRenderedList()` პასუხის JSON მასივის ზომას კითხულობს (მაგალითად 63) და ელოდება `assertThat(items).hasCount(63)`. შემოწმებები მხოლოდ მაშინ იწყება, როცა UI ზუსტად API-ის პასუხს აჩვენებს. ფიქსირებული ლოდინი არ არის.

**2. ბრაუზერის geolocation-ის მოთხოვნა.** ლოკაციების გვერდი მდებარეობას ითხოვს. ამ დროს შეიძლება გამოჩნდეს permission popup, რომელსაც Playwright ვერ დააჭერს, და სიის დალაგებაც (მანძილით) მანქანის მიხედვით განსხვავდება.

*როგორ მოვაგვარე:* `PlaywrightFactory` ყოველ context-ს წინასწარ აძლევს `geolocation` ნებართვას ფიქსირებული კოორდინატებით (თბილისი, `geo.latitude`/`geo.longitude` config-ში). ამიტომ popup არ ჩნდება და მოთხოვნა ყოველთვის ერთნაირია (`myLocation` body-ში).

**3. ელემენტები, რომლებიც დაჭერას ფარავს.** ორი ასეთი შემთხვევა იყო:
- cookie banner, რომელიც გვერდის ქვედა ნაწილს ფარავს;
- sticky quick-action ღილაკები, რომლებიც გაშლამდე ერთმანეთზეა დაწყობილი: Playwright-მა აჩვენა „element intercepts pointer events“.

*როგორ მოვაგვარე:*
- `HomeSteps.acceptCookies()` banner-ს ხურავს და `isHidden()`-ით ამოწმებს, რომ გაქრა.
- `QuickActionsComponent.expand()` kebab ღილაკს მხოლოდ მაშინ აჭერს, თუ მენიუ ჯერ ღია არ არის, და ელოდება `.tbcx-pw-sticky-actions--open`-ს. დაჭერა მხოლოდ ამის შემდეგ ხდება.
- `force: true` არ გამოიყენება, რადგან ის პრობლემას დამალავდა.

**4. პარალელური გაშვება.** Playwright-ის ობიექტები thread-safe არ არის. `PlaywrightFactory` ყოველ ტესტს საკუთარ `Playwright → Browser → Context → Page` ჯაჭვს აძლევს (`ThreadLocal`) და ტესტის ბოლოს ხურავს. cookie-ები, storage და მდებარეობა ტესტებს შორის არ ზიარდება. ტესტებს ერთმანეთზე დამოკიდებულება არ აქვს.

**5. საიტის WAF.** ბევრი სწრაფი მოთხოვნის შემდეგ tbcbank.ge აბრუნებს „Your request has been blocked“. retry ამ დროს მხოლოდ გაახანგრძლივებდა ბლოკს, ამიტომ retry analyzer არ გამოიყენება. სამაგიეროდ, `failIfBlockedBySite()` ტესტს მაშინვე მკაფიო მიზეზით აჩერებს. ადრე ამ შემთხვევაში შეცდომაში შემყვანი „element(s) not found“ ჩანდა. პარალელიზმი `-Dthreads`-ით რეგულირდება.

**სხვა სცენარების მიდგომა.** იგივე პრინციპებით არის დაწერილი დანარჩენი სცენარებიც:
- T29 ისტორიის ფანჯარაში ელოდება, სანამ შეუვსებელი რიგი აღარ დარჩება (`unfilledRows().hasCount(0)`), რადგან ag-grid რიგებს ჯერ ცარიელად ხატავს.
- T31/T33 კონვერტორის შედეგს `page.waitForFunction`-ით ელოდება. შედეგი ბრაუზერში ითვლება და network მოვლენა არ ხდება.
- regex-ები, რომლებიც ბრაუზერს გადაეცემა, `RegexUtils.escape()`-ით escape-დება. `Pattern.quote()`-ის `\Q…\E` JavaScript-ში არ მუშაობს.

**დარჩენილი რისკი.** T34 და T36 ცოცხალ კურსებს ადარებს: ორ API მოთხოვნას ერთმანეთს, ან API-ს UI-ს. თუ ბანკმა კურსი ზუსტად ამ ორ მოთხოვნას შორის განაახლა (რამდენიმე წამის ფანჯარა), ტესტი ჩავარდება. რისკის შესამცირებლად API და გვერდი ერთმანეთის მიყოლებით იძახება, და ჩავარდნის შეტყობინებაში ორივე მნიშვნელობა ჩანს. ასე ცხადია, რომ ეს კურსის განახლება იყო და არა ბაგი.

პროექტში არსად არ არის `Thread.sleep()`, `waitForTimeout()` ან TestNG retry analyzer.
