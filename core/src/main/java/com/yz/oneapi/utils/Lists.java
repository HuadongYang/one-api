package com.yz.oneapi.utils;



import java.lang.reflect.Array;
import java.util.*;

public class Lists {
    private Lists() {
    }

    public static <T> T[] sub(T[] array, int start, int end) {
        int length = length(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return newArray(array.getClass().getComponentType(), 0);
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return newArray(array.getClass().getComponentType(), 0);
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    public static <T> T[] newArray(Class<?> componentType, int newSize) {
        return (T[]) Array.newInstance(componentType, newSize);
    }

    public static int length(Object array) throws IllegalArgumentException {
        if (null == array) {
            return 0;
        }
        return Array.getLength(array);
    }

    public static <T> boolean contains( T[] array, final T element) {
        if (array == null) {
            return false;
        }
        return Arrays.stream(array).anyMatch(x -> ObjectUtil.nullSafeEquals(x, element));
    }

    public static <T> ArrayList<T> toList(T... values) {
        return (ArrayList<T>) toList(false, values);
    }

    public static <E> ArrayList<E> newArrayList(E... elements) {
        Preconditions.checkNotNull(elements);
        int capacity = computeArrayListCapacity(elements.length);
        ArrayList<E> list = new ArrayList(capacity);
        Collections.addAll(list, elements);
        return list;
    }

    public static <E> ArrayList<E> fill(E elements, int size) {
        ArrayList<E> list = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            list.add(elements);
        }
        return list;
    }

    static int computeArrayListCapacity(int arraySize) {
        CollectPreconditions.checkNonnegative(arraySize, "arraySize");
        return Ints.saturatedCast(5L + (long) arraySize + (long) (arraySize / 10));
    }

    public static <E> List<E> toList(Iterable<E> iter) {
        if (null == iter) {
            return null;
        }
        return toList(iter.iterator());
    }

    public static <E> List<E> toList(Iterator<E> iter) {
        return toArrayList(iter);
    }

    public static <T> ArrayList<T> toArrayList(Iterator<T> iterator) {
        return (ArrayList<T>) list(false, iterator);
    }

    public static <T> List<T> list(boolean isLinked, Iterator<T> iter) {
        final List<T> list = list(isLinked);
        if (null != iter) {
            while (iter.hasNext()) {
                list.add(iter.next());
            }
        }
        return list;
    }

    public static <T> List<T> list(boolean isLinked) {
        return isLinked ? new LinkedList<>() : new ArrayList<>();
    }

    public static String join(Collection<?> coll) {
        return collectionToDelimitedString(coll, ",");
    }


    public static String collectionToDelimitedString( Collection<?> coll, String delim) {
        return collectionToDelimitedString(coll, delim, "", "");
    }

    public static String collectionToDelimitedString(
             Collection<?> coll, String delim, String prefix, String suffix) {

        if (OneApiUtil.isEmpty(coll)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        Iterator<?> it = coll.iterator();
        while (it.hasNext()) {
            sb.append(prefix).append(it.next()).append(suffix);
            if (it.hasNext()) {
                sb.append(delim);
            }
        }
        return sb.toString();
    }

}
