package constants;

public enum AppLocale {
    KA("ka", "ka-GE"),
    EN("en", "en-US");

    private final String path;
    private final String apiLocale;

    AppLocale(String path, String apiLocale) {
        this.path = path;
        this.apiLocale = apiLocale;
    }

    public String path() {
        return path;
    }

    public String apiLocale() {
        return apiLocale;
    }
}
