package com.syntifi.ori.converter;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * The actual LocalDateTimeFormat decorator. By detault the format is "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZ"
 * 
 * @author Andre Bertolace 
 * @since 0.1.0
 */
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
public @interface LocalDateTimeFormat {
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZ";

    String value() default DATE_FORMAT;
}