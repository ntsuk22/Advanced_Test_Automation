package constants;

/**
 * Location categories on the ATMs &amp; Branches page and the list icon each one is rendered with.
 * Branches and CDMs share the same "bank-outlined" icon on the site.
 */
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
