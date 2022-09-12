package com.iagent.util;

import com.iagent.logging.LogFactory;
import com.iagent.logging.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * @author liujieyu
 * @date 2022/5/13 17:29
 * @desciption
 */
public class BeanUtils {

    private static final Logger logger = LogFactory.getLogger(BeanUtils.class);

    /**
     * create new instance by class
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T instanceClass(Class<T> cls) {
        try {
            Constructor<?> ctor = cls.getDeclaredConstructor();
            if ((!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) && !ctor.isAccessible()) {
                ctor.setAccessible(true);
            }
            T t = (T) ctor.newInstance();
            return t;
        } catch (Throwable throwable) {
            logger.error("Cannot Load [" + cls + "] Constructor, Error Message :" + throwable.getMessage());
            throw new IllegalArgumentException(throwable.getMessage());
        }
    }

    /**
     * create new instance by class name
     * @param className
     * @param <T>
     * @return
     */
    public static <T> T instance(String className, Class<T> interfaceClass) {
        try {
            Class<?> aClass = Class.forName(className, false, ClassUtils.getClassLoader());
            if (interfaceClass.isAssignableFrom(aClass)) {
                return (T) instanceClass(aClass);
            }
            return null;
        } catch (Throwable throwable) {
            logger.error("Cannot Load [" + interfaceClass + "] Class, Error Message :" + throwable.getMessage());
            throw new IllegalArgumentException(throwable.getMessage());
        }
    }

    /**
     * reflect to Object
     * @param source
     * @param <T>
     * @return
     */
    public static <T> T copyProperties(T source) {
        T target = (T) instanceClass(source.getClass());
        Field[] declaredFields = source.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            ReflectUtils.setFieldValue(target, field, ReflectUtils.getFieldValueByObject(field, source));
        }
        return target;
    }

}
