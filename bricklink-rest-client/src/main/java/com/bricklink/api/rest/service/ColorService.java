package com.bricklink.api.rest.service;

import com.bricklink.api.rest.client.BricklinkRestClient;
import com.bricklink.api.rest.model.v1.BricklinkResource;
import com.bricklink.api.rest.model.v1.Color;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class ColorService {
    private final BricklinkRestClient bricklinkRestClient;
    private boolean loaded = false;
    private ConcurrentHashMap<Integer, Color> colorsById = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Color> colorsByName = new ConcurrentHashMap<>();

    public Color getColorById(final Integer colorId) {
        loadColors();
        return Optional.ofNullable(colorsById.get(colorId))
                       .orElseThrow(() -> new RuntimeException("color_id [" + colorId + "} not found"));
    }

    public Color getColorByName(final String colorName) {
        loadColors();
        return Optional.ofNullable(colorsByName.get(colorName))
                       .orElseThrow(() -> new RuntimeException("color name [" + colorName + "} not found"));
    }

    private void loadColors() {
        if (!loaded) {
            BricklinkResource<List<Color>> meta = bricklinkRestClient.getColors();
            if (meta.getMeta()
                    .getCode()
                    .equals(200)) {
                List<Color> colors = meta.getData();
                colors.forEach(this::cacheColor);
                loaded = true;
            }
        }
    }

    private Optional<Color> lookupById(final Integer colorId) {
        BricklinkResource<Color> color = bricklinkRestClient.getColor(colorId);
        if (color.getMeta()
                 .getCode()
                 .equals(200)) {
            Color c = color.getData();
            cacheColor(c);
            return Optional.of(color.getData());
        } else {
            return Optional.empty();
        }
    }

    private void cacheColor(final Color c) {
        colorsById.put(c.getColor_id(), c);
        colorsByName.put(c.getColor_name(), c);
        colorsByName.put(StringUtils.replace(c.getColor_name(), "-", " "), c);
    }
}
