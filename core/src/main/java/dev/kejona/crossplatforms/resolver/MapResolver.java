package dev.kejona.crossplatforms.resolver;

import java.util.HashMap;
import java.util.Map;

public class MapResolver implements Resolver {

    private final Map<String, String> map;

    public MapResolver(Map<String, String> map) {
        this.map = map;
    }

    public MapResolver(String keyTemplate, String[] values) {
        this.map = generateMap(keyTemplate, values);
    }

    @Override
    public String apply(final String s) {
        if (s == null) {
            return null;
        }

        String result = s;
        for (String key : map.keySet()) {
            result = result.replace(key, map.get(key));
        }
        return result;
    }

    public static Map<String, String> generateMap(String keyTemplate, String[] values) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            map.put("%" + keyTemplate + "_" + i + "%", values[i]);
        }
        return map;
    }
}
