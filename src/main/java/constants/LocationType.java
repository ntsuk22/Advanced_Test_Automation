package constants;

public enum LocationType {
    ATM("atm-outlined"),
    BRANCH("bank-outlined"),
    CDM("bank-outlined");

    private final String icon;

    LocationType(String icon) {
        this.icon = icon;
    }

    public String icon() {
        return icon;
    }
}
