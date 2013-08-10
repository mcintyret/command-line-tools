package com.mcintyret.commandline;

import java.util.Map;
import java.util.Set;

/**
 * User: mcintyret2
 * Date: 10/08/2013
 */
public final class Utils {

    private Utils() {

    }

    public static void checkArgument(boolean valid) {
        checkArgument(valid, "Invalid Argument");
    }

    public static void checkArgument(boolean valid, String message) {
        if (!valid) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <K, V> void putIfAbsent(Map<K, V> map, K key, V value, String message) {
        if (map.containsKey(key)) {
            throw new IllegalArgumentException(message);
        }
        map.put(key, value);
    }

    public static <K, V> void putIfAbsent(Map<K, V> map, K key, V value) {
        putIfAbsent(map, key, value, "Map already contains key '" + key + "', mapped to '" + map.get(key) + "'");
    }

    public static <T> void addIfAbsent(Set<T> set, T item) {
        addIfAbsent(set, item, "Set already contains item '" + item + "'");
    }

    public static <T> void addIfAbsent(Set<T> set, T item, String message) {
        if (!set.add(item)) {
            throw new IllegalArgumentException(message);
        }
    }

}
