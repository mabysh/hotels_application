package com.demo.app.hotel.ui.representation;

/*
 *  This class make representation of days more readable in Hotel Grid
 */

import com.vaadin.ui.renderers.TextRenderer;
import elemental.json.Json;
import elemental.json.JsonValue;

public class OperatesFromRenderer extends TextRenderer {

    @Override
    public JsonValue encode(Object value) {
        if (value == null) {
            return super.encode(null);
        } else {
            String result = value.toString() + " days";
            return Json.create(result);
        }
    }
}
