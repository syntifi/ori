package com.syntifi.ori.converter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ParamConverter;

import com.syntifi.ori.exception.ORIException;


/**
 * Custom LocalDateTimeConverter implementing JAX-RS ParamConverter interface. This class
 * is the one handling the conversion from/to LocalDatetime/String
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace 
 * @since 0.1.0
 */
public class LocalDateTimeConverter implements ParamConverter<LocalDateTime> {
  
    private static final String DATE_FORMAT = LocalDateTimeFormat.DATE_FORMAT;

    private DateTimeFormatter formatter;

    /**
     * Sets up the custom formatter or falls back to the default one from LocalDateTimeFormat
     * 
     * @param format
     */
    public void setCustomDateFormat(String format) {
        this.formatter = DateTimeFormatter.ofPattern(format==null ? DATE_FORMAT : format);
        formatter.withZone(ZoneId.of("GMT"));
    }

    /**
     * Parses a string to a LocalDateTime. Note, the string should follow the specified DATE_FORMAT
     * 
     * @param value
     * @return LocalDateTime
     * @throws ORIException
     */
    @Override
    public LocalDateTime fromString(String value) {
        LocalDateTime date; 
        if (value == null)
            date = LocalDateTime.now();
        else {
            try {
                date = LocalDateTime.parse(value, formatter);
            } catch (DateTimeParseException e) {
                throw new ORIException("Wrong DateTime format, please follow " + DATE_FORMAT, Status.BAD_REQUEST.getStatusCode());
            }
        }
        return date;
    }

    /**
     * Converts a LocalDateTime to a string according to the given DATE_FORMAT
     * 
     * @param value
     * @return String 
     */
    @Override
    public String toString(LocalDateTime value) {
        if (value == null)
            return null;
        return value.format(formatter);
    }

}