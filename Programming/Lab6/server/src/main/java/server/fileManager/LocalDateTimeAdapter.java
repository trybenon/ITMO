package server.fileManager;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Адаптер для сериализации и десериализации {@link LocalDateTime} в JAXB.
 */
public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public LocalDateTime unmarshal(String v) {
        return LocalDateTime.parse(v, FORMATTER);
    }

    @Override
    public String marshal(LocalDateTime v) {
        return v.format(FORMATTER);
    }
}
