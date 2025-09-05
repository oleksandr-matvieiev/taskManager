package org.example.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

public class DateUtils {
    private static final List<DateTimeFormatter> FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("d.M.yy"),
            DateTimeFormatter.ofPattern("d.M.yyyy"),
            DateTimeFormatter.ofPattern("dd.MM.yy"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd"),
            DateTimeFormatter.ofPattern("ddMMyyyy"),
            DateTimeFormatter.ofLocalizedDate(java.time.format.FormatStyle.SHORT).withLocale(Locale.getDefault())
    );

    public static LocalDate parseDate(String input) {
        String normalized = input.trim()
                .replace("-", ".")
                .replace("/", ".")
                .replace("\\", ".");

        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDate.parse(normalized, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }

        throw new IllegalArgumentException("Invalid date format: " + input);
    }
}
