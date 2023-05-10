package dev.kejona.crossplatforms.resolver;

import dev.kejona.crossplatforms.Logger;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class MapResolver implements Resolver {

    private final Map<String, String> map;

    @Override
    public String apply(final String s) {
        if (s == null) {
            return null;
        }

        String result = s;
        for (String key : map.keySet()) {
            result = result.replace(key, map.get(key));
        }
        if (Logger.get().isDebug()) {
            Logger.get().info("Result of map resolver: " + result);
        }
        return result;
    }
}
