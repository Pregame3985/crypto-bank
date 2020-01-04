package com.mcb.address.manager.util;

import java.util.Collection;

/**
 * @author william
 */
public abstract class CommonUtils {

    public static boolean isEmpty(String str) {
        return (str == null || "".equals(str.trim()));
    }

    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }
}
