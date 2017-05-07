package com.demo.app.hotel;

/*
 * Purpose of the class is to convert representation of hotel rating between Integer in class field
 * and star characters in Hotel GRID
 */

import com.vaadin.ui.renderers.TextRenderer;
import elemental.json.Json;
import elemental.json.JsonValue;

public class StarsRenderer extends TextRenderer {

    private final String STAR = "\u2605";

    @Override
    public JsonValue encode(Object value) {
        if (value == null) {
            return super.encode(null);
        } else {
            try {
                int count = (Integer) value;
                StringBuilder str = new StringBuilder("");
                for (int i = 0; i < count; i++) {
                    str.append(STAR);
                }
                return Json.create(str.toString());
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
            return Json.create("");
        }
    }
}
