package com.iagent.util;

/**
 * @author liujieyu
 * @date 2022/5/13 19:13
 * @desciption Assertion utility class that assists in validating arguments.
 */
public final class Assert {

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isInstanceOf(Class aClass, Object object) {
        notNull(aClass, " check class instance of is not null");
        if (!aClass.isInstance(object)) {
            throw new IllegalArgumentException("Object of class [" + object.getClass() + "] must be an instance of " + aClass);
        }
    }

}
