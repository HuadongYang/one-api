package com.yz.oneapi.utils;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ArrayUtil {

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     * @since 3.0.7
     */
    public static boolean contains(char[] array, char value) {
        return indexOf(array, value) > -1;
    }

    public static int indexOf(char[] array, char value) {
        if (isNotEmpty(array)) {
            for (int i = 0; i < array.length; i++) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return -1;
    }
    public static boolean isNotEmpty(char[] array) {
        return false == isEmpty(array);
    }
    public static boolean isEmpty(char[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(byte[] array) {
        return false == isEmpty(array);
    }
    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    /**
     *
     * @param array 数组
     * @param value 被检查的元素
     * @since 3.0.7
     */
    public static int indexOf(byte[] array, byte value) {
        if (isNotEmpty(array)) {
            for (int i = 0; i < array.length; i++) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return -1;
    }
    /**
     * 获取数组对象的元素类型
     *
     * @param array 数组对象
     * @return 元素类型
     * @since 3.2.2
     */
    public static Class<?> getComponentType(Object array) {
        return null == array ? null : array.getClass().getComponentType();
    }
    public static int length(Object array) throws IllegalArgumentException {
        if (null == array) {
            return 0;
        }
        return Array.getLength(array);
    }

    /**
     * 获取数组对象的元素类型
     *
     * @param arrayClass 数组类
     * @return 元素类型
     * @since 3.2.2
     */
    public static Class<?> getComponentType(Class<?> arrayClass) {
        return null == arrayClass ? null : arrayClass.getComponentType();
    }

    /**
     * 新建一个空数组
     *
     * @param <T>           数组元素类型
     * @param componentType 元素类型
     * @param newSize       大小
     * @return 空数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(Class<?> componentType, int newSize) {
        return (T[]) Array.newInstance(componentType, newSize);
    }
    /**
     * 根据数组元素类型，获取数组的类型<br>
     * 方法是通过创建一个空数组从而获取其类型
     *
     * @param componentType 数组元素类型
     * @return 数组类型
     * @since 3.2.2
     */
    public static Class<?> getArrayType(Class<?> componentType) {
        return Array.newInstance(componentType, 0).getClass();
    }
    /**
     * 对象是否为数组对象
     *
     * @param obj 对象
     * @return 是否为数组对象，如果为{@code null} 返回false
     */
    public static boolean isArray(Object obj) {
        return null != obj && obj.getClass().isArray();
    }

    /**
     * 数组或集合转String
     *
     * @param obj 集合或数组对象
     * @return 数组字符串，与集合转字符串格式相同
     */
    public static String toString(Object obj) {
        if (null == obj) {
            return null;
        }

        if (obj instanceof long[]) {
            return Arrays.toString((long[]) obj);
        } else if (obj instanceof int[]) {
            return Arrays.toString((int[]) obj);
        } else if (obj instanceof short[]) {
            return Arrays.toString((short[]) obj);
        } else if (obj instanceof char[]) {
            return Arrays.toString((char[]) obj);
        } else if (obj instanceof byte[]) {
            return Arrays.toString((byte[]) obj);
        } else if (obj instanceof boolean[]) {
            return Arrays.toString((boolean[]) obj);
        } else if (obj instanceof float[]) {
            return Arrays.toString((float[]) obj);
        } else if (obj instanceof double[]) {
            return Arrays.toString((double[]) obj);
        } else if (isArray(obj)) {
            // 对象数组
            try {
                return Arrays.deepToString((Object[]) obj);
            } catch (Exception ignore) {
                //ignore
            }
        }

        return obj.toString();
    }
}
