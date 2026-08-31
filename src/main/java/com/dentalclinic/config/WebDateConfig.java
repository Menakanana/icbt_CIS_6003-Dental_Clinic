package com.dentalclinic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.Printer;
import org.springframework.format.Parser;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Global Web MVC Date Configuration for standardizing date formatting and parsing
 * across all controllers, form submissions, and query parameters.
 * Primary format: DD/MM/YYYY (e.g. 06/09/2026).
 */
@Configuration
public class WebDateConfig implements WebMvcConfigurer {

    private static final DateTimeFormatter PRIMARY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter[] FALLBACK_FORMATTERS = new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("MM-dd-yyyy")
    };

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new org.springframework.format.Formatter<LocalDate>() {
            @Override
            public String print(LocalDate object, Locale locale) {
                return object != null ? object.format(PRIMARY_FORMATTER) : "";
            }

            @Override
            public LocalDate parse(String text, Locale locale) throws ParseException {
                if (text == null || text.trim().isEmpty()) {
                    return null;
                }
                String trimmed = text.trim();
                for (DateTimeFormatter formatter : FALLBACK_FORMATTERS) {
                    try {
                        return LocalDate.parse(trimmed, formatter);
                    } catch (Exception ignored) {
                    }
                }
                throw new ParseException("Unable to parse date: " + text + ". Expected format: DD/MM/YYYY", 0);
            }
        });
    }
}
