package com.bricklink.api.rest.client;

import java.util.HashMap;
import java.util.Map;

public class ParamsBuilder {
    private Map<String, Object> params = new HashMap<>();

    public ParamsBuilder of(String key, Object value) {
        params.put(key, value);
        return this;
    }

    public Map<String, Object> get() {
        return params;
    }
}
