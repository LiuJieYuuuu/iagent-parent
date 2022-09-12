package com.iagent.util;

import com.iagent.logging.LogFactory;
import com.iagent.logging.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * class util
 */
public class ClassUtils {

    private static final Logger logger = LogFactory.getLogger(ClassUtils.class);

    /**
     * get default class loader
     * @return
     */
    public static ClassLoader getClassLoader () {
        ClassLoader classLoader = null;
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
        } catch (Throwable t) {
            //Cannot get thread context ClassLoader
        }
        if (classLoader == null) {
            // No thread context class loader -> use class loader of this class.
            classLoader = ClassUtils.class.getClassLoader();
            if (classLoader == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    classLoader = ClassLoader.getSystemClassLoader();
                }
                catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return classLoader;
    }

    /**
     * get CLass instance by class name and class have annotation
     * @param className
     * @param annotation
     * @return
     */
    public static Class getInterfaceClass(String className, Class annotation) {
        try {
            Assert.notNull(className, "ClassName is Null");
            Class<?> aClass = Class.forName(className);
            if (Modifier.isInterface(aClass.getModifiers()) && aClass.getDeclaredAnnotation(annotation) != null) {
                return aClass;
            }
            return null;
        } catch (Throwable e) {
            logger.error("The [" + className + "] is Not Loader ");
        }
        return null;
    }

    /**
     * get class path #like com.iagent.util.ClassUtils
     * @param aClass
     * @return
     */
    public static String getClassPathByClass(Class aClass) {
        Assert.notNull(aClass, "Class is Null");
        return aClass.getName();
    }

    /**
     * get Class Path of Method #like java.lang.Object.wait(long,int)
     * @param method
     * @return
     */
    public static String getClassPathByMethod(Method method) {
        Assert.notNull(method, "Method is Null");
        String classPath = getClassPathByClass(method.getDeclaringClass());
        Class<?>[] parameterTypes = method.getParameterTypes();
        String methodName = classPath + "." + method.getName() + "(";
        for (Class<?> parameterType : parameterTypes) {
            methodName += getClassPathByClass(parameterType) + ",";
        }
        if (parameterTypes != null && parameterTypes.length > 0) {
            methodName = methodName.substring(0, methodName.length() - 1);
        }
        methodName += ")";
        return methodName;
    }

    /**
     * 使用构造函数创建对象
     * @param tClass
     * @param classes
     * @param args
     * @param <T>
     * @return
     */
    public static <T> T newInstance(Class<T> tClass, Class<?>[] classes, Object[] args) {
        try {
            Constructor<T> constructor = tClass.getConstructor(classes);
            T t = constructor.newInstance(args);
            return t;
        } catch (Throwable throwable) {
            logger.error("Cannot Load [" + tClass + "] Constructor, Error Message :", throwable);
            throw new IllegalArgumentException(throwable.getMessage());
        }
    }

    /**
     * create new instance
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T newInstance(Class<T> tClass) {
        try {
            return tClass.newInstance();
        } catch (Throwable throwable) {
            logger.error("Cannot Load [" + tClass + "] Constructor, Error Message :" + throwable.getMessage());
            throw new IllegalArgumentException(throwable.getMessage());
        }
    }

    /**
     * 返回指定函数的所有参数类型
     * @param method
     * @return
     */
    public static Class<?>[] getMethodParameterClass(Method method) {
        return method.getParameterTypes();
    }
}
