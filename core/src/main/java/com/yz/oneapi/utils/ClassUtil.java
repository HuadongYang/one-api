package com.yz.oneapi.utils;

import com.sun.xml.internal.ws.util.UtilException;
import com.yz.oneapi.config.OneApiException;
import sun.misc.ClassLoaderUtil;

import java.lang.reflect.Array;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassUtil {
    /**
     * 数组类的结尾符: "[]"
     */
    private static final String ARRAY_SUFFIX = "[]";
    /**
     * 内部数组类名前缀: "["
     */
    private static final String INTERNAL_ARRAY_PREFIX = "[";
    /**
     * 内部非原始类型类名前缀: "[L"
     */
    private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";
    /**
     * 包名分界符: '.'
     */
    private static final char PACKAGE_SEPARATOR = CharPool.DOT;
    /**
     * 内部类分界符: '$'
     */
    private static final char INNER_CLASS_SEPARATOR = '$';

    private static final Map<String, Class<?>> PRIMITIVE_TYPE_NAME_MAP = new ConcurrentHashMap<>(32);

    /**
     * 加载类，通过传入类的字符串，返回其对应的类名<br>
     * 此方法支持缓存，第一次被加载的类之后会读取缓存中的类<br>
     * 加载失败的原因可能是此类不存在或其关联引用类不存在<br>
     * 扩展{@link Class#forName(String, boolean, ClassLoader)}方法，支持以下几类类名的加载：
     *
     * <pre>
     * 1、原始类型，例如：int
     * 2、数组类型，例如：int[]、Long[]、String[]
     * 3、内部类，例如：java.lang.Thread.State会被转为java.lang.Thread$State加载
     * </pre>
     *
     * @param name          类名
     * @param classLoader   {@link ClassLoader}，{@code null} 则使用{@link #getClassLoader()}获取
     * @param isInitialized 是否初始化类（调用static模块内容和初始化static属性）
     * @return 类名对应的类
     * @throws UtilException 包装{@link ClassNotFoundException}，没有类名对应的类时抛出此异常
     */
    public static Class<?> loadClass(String name, ClassLoader classLoader, boolean isInitialized) {

        // 自动将包名中的"/"替换为"."
        name = name.replace(CharPool.SLASH, CharPool.DOT);
        // 加载原始类型和缓存中的类
        Class<?> clazz = loadPrimitiveClass(name);
        if(null == classLoader){
            classLoader = getClassLoader();
        }

        if (clazz == null) {
            clazz = doLoadClass(name, classLoader, isInitialized);
        }
        return clazz;
    }
    public static Class<?> loadClass(String name) {

        return loadClass(name, null, false);
    }

    /**
     * 加载非原始类类，无缓存
     * @param name 类名
     * @param classLoader {@link ClassLoader}
     * @param isInitialized 是否初始化
     * @return 类
     */
    private static Class<?> doLoadClass(String name, ClassLoader classLoader, boolean isInitialized){
        Class<?> clazz;
        if (name.endsWith(ARRAY_SUFFIX)) {
            // 对象数组"java.lang.String[]"风格
            final String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
            final Class<?> elementClass = loadClass(elementClassName, classLoader, isInitialized);
            clazz = Array.newInstance(elementClass, 0).getClass();
        } else if (name.startsWith(NON_PRIMITIVE_ARRAY_PREFIX) && name.endsWith(";")) {
            // "[Ljava.lang.String;" 风格
            final String elementName = name.substring(NON_PRIMITIVE_ARRAY_PREFIX.length(), name.length() - 1);
            final Class<?> elementClass = loadClass(elementName, classLoader, isInitialized);
            clazz = Array.newInstance(elementClass, 0).getClass();
        } else if (name.startsWith(INTERNAL_ARRAY_PREFIX)) {
            // "[[I" 或 "[[Ljava.lang.String;" 风格
            final String elementName = name.substring(INTERNAL_ARRAY_PREFIX.length());
            final Class<?> elementClass = loadClass(elementName, classLoader, isInitialized);
            clazz = Array.newInstance(elementClass, 0).getClass();
        } else {
            // 加载普通类
            if (null == classLoader) {
                classLoader = getClassLoader();
            }
            try {
                clazz = Class.forName(name, isInitialized, classLoader);
            } catch (ClassNotFoundException ex) {
                // 尝试获取内部类，例如java.lang.Thread.State =》java.lang.Thread$State
                clazz = tryLoadInnerClass(name, classLoader, isInitialized);
                if (null == clazz) {
                    throw new OneApiException(ex);
                }
            }
        }
        return clazz;
    }

    /**
     * 尝试转换并加载内部类，例如java.lang.Thread.State =》java.lang.Thread$State
     *
     * @param name          类名
     * @param classLoader   {@link ClassLoader}，{@code null} 则使用系统默认ClassLoader
     * @param isInitialized 是否初始化类（调用static模块内容和初始化static属性）
     * @return 类名对应的类
     * @since 4.1.20
     */
    private static Class<?> tryLoadInnerClass(String name, ClassLoader classLoader, boolean isInitialized) {
        // 尝试获取内部类，例如java.lang.Thread.State =》java.lang.Thread$State
        final int lastDotIndex = name.lastIndexOf(PACKAGE_SEPARATOR);
        if (lastDotIndex > 0) {// 类与内部类的分隔符不能在第一位，因此>0
            final String innerClassName = name.substring(0, lastDotIndex) + INNER_CLASS_SEPARATOR + name.substring(lastDotIndex + 1);
            try {
                return Class.forName(innerClassName, isInitialized, classLoader);
            } catch (ClassNotFoundException ex2) {
                // 尝试获取内部类失败时，忽略之。
            }
        }
        return null;
    }
    public static ClassLoader getClassLoader() {
        ClassLoader classLoader = getContextClassLoader();
        if (classLoader == null) {
            classLoader = ClassLoaderUtil.class.getClassLoader();
            if (null == classLoader) {
                classLoader = getSystemClassLoader();
            }
        }
        return classLoader;
    }
    public static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        } else {
            // 绕开权限检查
            return AccessController.doPrivileged(
                    (PrivilegedAction<ClassLoader>) () -> Thread.currentThread().getContextClassLoader());
        }
    }

    /**
     * 获取系统{@link ClassLoader}
     *
     * @return 系统{@link ClassLoader}
     * @see ClassLoader#getSystemClassLoader()
     * @since 5.7.0
     */
    public static ClassLoader getSystemClassLoader() {
        if (System.getSecurityManager() == null) {
            return ClassLoader.getSystemClassLoader();
        } else {
            // 绕开权限检查
            return AccessController.doPrivileged(
                    (PrivilegedAction<ClassLoader>) ClassLoader::getSystemClassLoader);
        }
    }

    /**
     * 加载原始类型的类。包括原始类型、原始类型数组和void
     *
     * @param name 原始类型名，比如 int
     * @return 原始类型类
     */
    public static Class<?> loadPrimitiveClass(String name) {
        Class<?> result = null;
        if (OneApiUtil.isNotBlank(name)) {
            name = name.trim();
            if (name.length() <= 8) {
                result = PRIMITIVE_TYPE_NAME_MAP.get(name);
            }
        }
        return result;
    }
}
