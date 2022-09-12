package com.iagent.util;

import com.iagent.logging.LogFactory;
import com.iagent.logging.Logger;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

/**
 * reflect util function
 */
public class ReflectUtils {

    private static final Logger logger = LogFactory.getLogger(ReflectUtils.class);

    /**
     * 给指定对象的某个属性赋值
     * @param target
     * @param field
     * @param value
     */
    public static void setFieldValue(Object target, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            logger.error("Cannot set the Field of Object [" + target + "]; set value [" + value + "]; error:" + e.getMessage());
        }
    }

    /**
     * 获取对象中的某个属性值
     * @param field
     * @param target
     * @return
     */
    public static Object getFieldValueByObject(Field field, Object target) {
        try {
            field.setAccessible(true);
            Object value = field.get(target);
            return value;
        } catch (IllegalAccessException e) {
            logger.error("Cannot get the Field [" + field.getName() +"] of Object [" + target + "]; error:" + e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取对象中的某个属性值
     * @param fieldName
     * @param target
     * @return
     */
    public static Object getFieldValueByObject(String fieldName, Object target) {
        try {
            return getFieldValueByObject(getField(target.getClass(), fieldName), target);
        } catch (Throwable e) {
            logger.error("Cannot get the Field [" + fieldName +"] of Object [" + target + "]; error:" + e.getMessage());
        }
        return null;
    }

    /**
     * 将对象转换成 key=value&key=value 格式
     * 1：判断是否满足数组，集合以及对象可转换的要求
     * 2：针对数组，集合类型
     * 3：针对key-value类型
     * 4：针对基础类型
     * 最终都是以常规类型结束
     * @param object 需要解析的对象
     * @param recursion 是否为递归
     * @return map
     */
    public static Map<String, Object> getKeyValueByObject(Object object, boolean recursion) {
    	Map<String, Object> result = new HashMap<>(16);
    	if (isBasicGenericType(object.getClass())) {
    		return Collections.EMPTY_MAP;
    	}
        List<String> fieldNames = getFieldsName(object.getClass());
        // 按每个字段进行遍历
        for (String fieldName : fieldNames) {
        	// 拿到属性Class对象
        	Class<?> fieldClass = getFieldClass(object.getClass(), fieldName);
        	if (fieldClass == null || getFieldValueByObject(fieldName, object) == null) {
        		continue;
        	}
    		Field field = getField(object.getClass(), fieldName);
    		// 处理字段
        	if (isCollectOrMapOrArrayClass(fieldClass)) {
        		// 集合类型
        		if (field == null) {
        			continue;
        		}
                Map<String, Object> objectMap = handleCollectMapArray(field, object, fieldClass, getFieldValueByObject(field, object));
                for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
                    result.put(fieldName + entry.getKey(), entry.getValue());
                }
            } else if (isBasicGenericType(fieldClass)) {
            	// 常规基础类型
        		handleBasicType(field, object, fieldName, result, recursion);
        	} else {
            	// 对象类型,递归获取
        		Map<String, Object> childResultMap = getKeyValueByObject(getFieldValueByObject(field, object), true);
        		if (!childResultMap.isEmpty()) {
        			for (Map.Entry<String, Object> entry : childResultMap.entrySet()) {
        				result.put(fieldName + entry.getKey(), entry.getValue());
        			}
        		}
        	}
        }
        return result;
    }

    /**
     * 处理 集合,key-value以及数组类型
     * @param field 属性Field对象
     * @param target 整体对象
     * @param clazz 属性对应的Class对象
     * @param fieldObject 属性本身对象
     * @return result
     */
    private static Map<String, Object> handleCollectMapArray(Field field, Object target, Class<?> clazz, Object fieldObject) {
        if (fieldObject == null) {
            return Collections.EMPTY_MAP;
        }
    	Map<String, Object> result = new HashMap<>(8);
		if (clazz.isArray()) {// 优先处理数组类型
			// 当前数组组件类型
			Class<?> componentType = clazz.getComponentType();
			Object arrayValue = getFieldValueByObject(field, target);
			if (arrayValue == null) {
				return Collections.EMPTY_MAP;
			}
			int arrayLen = Array.getLength(arrayValue);
			for (int i = 0;i < arrayLen; i ++) {
                if (isBasicGenericType(componentType)) {
                    // 如果是基础类型
                    result.put("[" + i + "]", Array.get(arrayValue, i));
                } else {
                    Map<String, Object> objectMap = getKeyValueByObject(Array.get(arrayValue, i), true);
                    for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
                        result.put("[" + i + "]" + entry.getKey(), entry.getValue());
                    }
                }
            }
		} else if (fieldObject instanceof Collection) {
		    // 如果是 集合类型
            Collection collection = (Collection) fieldObject;
            Iterator iterator = collection.iterator();
            int index = 0;
            while(iterator.hasNext()) {
                Object nextObject = iterator.next();
                if (isBasicGenericType(nextObject.getClass())) {
                    result.put("[" + index + "]", nextObject);
                } else {
                    Map<String, Object> objectMap = getKeyValueByObject(nextObject, true);
                    for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
                        result.put("[" + index + "]" + entry.getKey(), entry.getValue());
                    }
                }

                index ++;
            }
        } else if (fieldObject instanceof Map) {
            // 如果是 Map 类型
            Map<?, ?> map = (Map) fieldObject;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = String.valueOf(entry.getKey());
                if (isBasicGenericType(entry.getValue().getClass())) {
                    result.put("." + key, entry.getValue());
                } else {
                    Map<String, Object> objectMap = getKeyValueByObject(entry.getValue(), true);
                    for (Map.Entry<String, Object> objectEntry : objectMap.entrySet()) {
                        result.put("." + key + objectEntry.getKey(), objectEntry.getValue());
                    }
                }
            }
        } else {
		    logger.warn("not found class type!");
        }

		return result;
    }
    /**
     * 处理常规基础类型
     * @param field
     * @param target
     * @param fieldName
     * @param result
     */
    private static void handleBasicType(Field field, Object target, String fieldName, Map<String, Object> result, boolean recursion) {
    	Assert.notNull(result, "result collect is null!");
    	Object value = getFieldValueByObject(field, target);
    	if (recursion) {
    	    // 递归获取数据是需要带上.前缀
            result.put("." + fieldName, value);
        } else {
            result.put(fieldName, value);
        }
    }
    
    /**
     * 判断是否为基本类型
     * like String,int,long ... Date
     * @param clazz
     * @return
     */
    private static boolean isBasicGenericType(Class<?> clazz) {
    	        // String类型
    	return (String.class.isAssignableFrom(clazz) || 
    			// 八大基本数据类型包装类，以及BigDecimal,AtomicInteger等数据类型
    			Number.class.isAssignableFrom(clazz) || Boolean.class.isAssignableFrom(clazz) || Character.class.isAssignableFrom(clazz) ||
    			// 时间类型
    			Date.class.isAssignableFrom(clazz) || java.security.Timestamp.class.isAssignableFrom(clazz) ||
    			// 八大基本数据类型
    			int.class.isAssignableFrom(clazz) || double.class.isAssignableFrom(clazz) ||
                float.class.isAssignableFrom(clazz) || short.class.isAssignableFrom(clazz) ||
    			boolean.class.isAssignableFrom(clazz) || char.class.isAssignableFrom(clazz) ||
                long.class.isAssignableFrom(clazz) || byte.class.isAssignableFrom(clazz));
    }
    /**
     * 获取Class对象的所有属性名称集合
     * @param clazz
     * @return
     */
    public static List<String> getFieldsName(Class<?> clazz) {
        if (clazz.getSuperclass() == null) {
            // 说明为Object.class
            return Collections.EMPTY_LIST;
        }
        List<String> fieldList = new LinkedList<>();
        // 拿到当前所有 属性值，包括私有和保护 默认
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            fieldList.add(field.getName());
        }
        // 父类为Object.class说明 没有父类，有判断是否为Object.class是有问题的，所以多次判断父类父类是否为空
        if (clazz.getSuperclass() != Object.class && clazz.getSuperclass().getSuperclass() != null) {
            // 说明有父类
            List<String> fieldsName = getFieldsName(clazz.getSuperclass());
            fieldList.addAll(fieldsName);
        }

        return fieldList;
    }

    /**
     * 获取到指定属性名称的 class 类型，不带泛型
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Class<?> getFieldClass(Class<?> clazz, String fieldName) {
        // 到 Object 都没有找到
        if (clazz.isAssignableFrom(Object.class) || clazz.getSuperclass() == null) {
            return null;
        }
        try {
            Field declaredField = clazz.getDeclaredField(fieldName);
            return declaredField == null ? null : declaredField.getType();
        } catch (Throwable e) {
            if (NoSuchFieldException.class.isAssignableFrom(e.getClass())) {
                // 说明当前类未找到，在从父类里面找
                return getFieldClass(clazz.getSuperclass(), fieldName);
            } else {
                // 其他异常
                logger.error("get class field error", e);
                return null;
            }
        }
    }
    
    /**
     * 获取到指定属性名称的 Field 对象
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        // 到 Object 都没有找到
        if (clazz.isAssignableFrom(Object.class) || clazz.getSuperclass() == null) {
            return null;
        }
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (Throwable e) {
            if (NoSuchFieldException.class.isAssignableFrom(e.getClass())) {
                // 说明当前类未找到，在从父类里面找
                return getField(clazz.getSuperclass(), fieldName);
            } else {
                // 其他异常
                logger.error("get class field", e);
                return null;
            }
        }
    }

    /**
     * 判断指定class是否为collection或者为Map或者为Array数组
     * @param clazz
     * @return
     */
    private static boolean isCollectOrMapOrArrayClass(Class<?> clazz) {
    	return (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz) || clazz.isArray());
    }

}

