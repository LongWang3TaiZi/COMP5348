package com.usyd.deliveryCo.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    private static final String SPLIT_CHAR = ";";

    // covert the List string to a string split with ;
    @Override
    public String convertToDatabaseColumn(List<String> stringList) {
        if (stringList == null || stringList.isEmpty()) {
            return "";
        }
        return String.join(SPLIT_CHAR, stringList);
    }

    // convert the string split with ; back a list of string 
    @Override
    public List<String> convertToEntityAttribute(String string) {
        if (string == null || string.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(string.split(SPLIT_CHAR))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}