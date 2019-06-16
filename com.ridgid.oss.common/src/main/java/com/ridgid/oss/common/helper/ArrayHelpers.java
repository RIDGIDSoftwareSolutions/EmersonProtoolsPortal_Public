package com.ridgid.oss.common.helper;

/**
 *
 */
public final class ArrayHelpers {

    private ArrayHelpers() {
    }

    /**
     * @param chars
     */
    public static void reverseArray(char[] chars) {
        for (int i = 0; i < chars.length / 2; i++) {
            char t = chars[i];
            chars[i] = chars[chars.length - 1 - i];
            chars[chars.length - 1 - i] = t;
        }
    }

    /**
     * @param bytes
     */
    public static void reverseArray(byte[] bytes) {
        for (int i = 0; i < bytes.length / 2; i++) {
            byte t = bytes[i];
            bytes[i] = bytes[bytes.length - 1 - i];
            bytes[bytes.length - 1 - i] = t;
        }
    }

    /**
     * @param items
     * @param <T>
     */
    public static <T> void reverseArray(T[] items) {
        for (int i = 0; i < items.length / 2; i++) {
            T t = items[i];
            items[i] = items[items.length - 1 - i];
            items[items.length - 1 - i] = t;
        }
    }
}
