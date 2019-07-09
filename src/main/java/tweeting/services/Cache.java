package tweeting.services;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class Cache<T> {

    private HashMap<String, List<T>> cache;

    public Cache() {
        cache = new HashMap<>();
    }

    public void invalidate() {
        cache.clear();
    }

    public void cacheItems(String key, List<T> items) {
        cache.put(key, items);
    }

    public Optional<List<T>> getCachedItems(String key) {
        return Optional.ofNullable(cache.get(key));
    }

}
