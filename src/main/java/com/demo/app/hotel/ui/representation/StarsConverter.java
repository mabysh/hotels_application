package com.demo.app.hotel.ui.representation;

/*
 * Purpose of the class is to convert representation of hotel rating between Integer in class field
 * and star characters in Hotel FORM
 */

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

public class StarsConverter implements Converter<String, Integer>{

    private final String STAR = "\u2605";

    @Override
    public Result<Integer> convertToModel(String value, ValueContext context) {
        if (value == null)
            return Result.ok(null);
        return Result.ok(value.length());
    }

    @Override
    public String convertToPresentation(Integer value, ValueContext context) {
        if (value == null)
            return null;
        StringBuilder str = new StringBuilder("");
        for (int i = 0; i < value; i++) {
            str.append(STAR);
        }
        return str.toString();
    }
}
