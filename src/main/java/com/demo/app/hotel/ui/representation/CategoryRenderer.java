package com.demo.app.hotel.ui.representation;

/*
 * This Renderer takes category id as input and returns category name as output.
 */

import com.demo.app.hotel.backend.Category;
import com.vaadin.ui.renderers.TextRenderer;
import elemental.json.Json;
import elemental.json.JsonValue;

public class CategoryRenderer extends TextRenderer {

    @Override
    public JsonValue encode(Object value) {
        Category c = (Category) value;
        if (c == null) {
            return Json.create("NOT SPECIFIED");
        } else {
            return Json.create(c.getName());
        }
    }
}
