package com.tokimi.common;

import java.util.Collection;
import java.util.Map;

/**
 * @author william
 */
public abstract class Utils {

    public static boolean isEmpty(String str) {
        return (str == null || "".equals(str.trim()));
    }

    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }
}
