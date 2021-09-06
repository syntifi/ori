package com.syntifi.ori.converter;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * The actual LocalDateTimeFormat decorator. By detault the format is "yyyy-MM-dd'T'HH:mm:ss.SSS"
 * 
 * @author Andre Bertolace 
 * @since 0.0.1
 */
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
public @interface LocalDateTimeFormat {
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    String value() default DATE_FORMAT;
}