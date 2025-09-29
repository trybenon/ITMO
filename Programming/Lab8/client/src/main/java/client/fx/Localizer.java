package client.fx;


import client.fx.locales.EnglishIndia;
import client.fx.locales.Russian;
import client.fx.locales.Serbian;
import client.fx.locales.Swedish;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

public class Localizer {
    private static final Map<Locale, Map<String, String>> resources = new HashMap<>();
    private Locale currentLocale;

    static {
        resources.put(new Locale("ru"), Russian.getMap());
        resources.put(new Locale("sr", "RS"), Serbian.getMap());
        resources.put(new Locale("sv", "SE"), Swedish.getMap());
        resources.put(new Locale("en", "IN"), EnglishIndia.getMap());
    }

    public Localizer(Locale defaultLocale) {
        this.currentLocale = defaultLocale != null && resources.containsKey(defaultLocale) ? defaultLocale : new Locale("ru");
    }

    public void setLocale(Locale locale) {
        this.currentLocale = locale != null && resources.containsKey(locale) ? locale : this.currentLocale;
    }

    public String getKeyString(String key) {
        return resources.get(currentLocale).getOrDefault(key, "[" + key + "]");
    }

    public String getDate(LocalDateTime date) {
        if (date == null) return "None";
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(currentLocale);
        return date.format(formatter);
    }
}