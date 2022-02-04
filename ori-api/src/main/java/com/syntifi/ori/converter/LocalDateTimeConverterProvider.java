package com.syntifi.ori.converter;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

/**
 * Class to register the LocalDateTimeConverter. The @Provider 
 * decorator makes sure to forward the parameter to the actual converter.
 * 
 * @author Andre Bertolace 
 * @since 0.1.0
 */
@Provider
public class LocalDateTimeConverterProvider implements ParamConverterProvider {

    @Override
    @SuppressWarnings("unchecked")
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType,
            Annotation[] annotations) {
        if (rawType.equals(LocalDateTime.class)) {
            final LocalDateTimeConverter converter = new LocalDateTimeConverter(); 
            if (annotations.length == 0) {
                converter.setCustomDateFormat(null);
            } else {
                for (Annotation annotation : annotations) {
                    if (LocalDateTimeFormat.class.equals(annotation.annotationType())) {
                          converter.setCustomDateFormat(((LocalDateTimeFormat) annotation).value());
                    }
                }
            }            
            return (ParamConverter<T>) converter;
        }
        return null;
    }

}