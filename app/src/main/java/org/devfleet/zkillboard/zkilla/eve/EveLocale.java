package org.devfleet.zkillboard.zkilla.eve;

import java.util.Locale;

public enum EveLocale {

    EN("en-us", Locale.ENGLISH),
    FR("fr", Locale.FRENCH),
    RU("ru", localeOf("ru")),
    DE("de", localeOf("de")),
    ZH("zh", Locale.CHINESE);

    private final String eveId;
    private final Locale locale;

    EveLocale(final String eveId, final Locale locale) {
        this.eveId = eveId;
        this.locale = locale;
    }

    public static EveLocale getDefault() {
        return EN;
    }

    public String getEveLocale() {
        return eveId;
    }

    public Locale getLocale() {
        return locale;
    }

    private static Locale localeOf(final String id) {
        for (Locale l: Locale.getAvailableLocales()) {
            if (l.getLanguage().equalsIgnoreCase(id)) {
                return l;
            }
        }
        return Locale.ENGLISH;
    }
}
