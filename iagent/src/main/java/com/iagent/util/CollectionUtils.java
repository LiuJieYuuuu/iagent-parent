package com.iagent.util;

import com.iagent.annotation.Order;
import com.iagent.constant.HttpConstant;

import java.util.*;

/**
 * @author liujieyu
 * @date 2022/5/13 19:10
 * @desciption
 */
public class CollectionUtils {

    /**
     * Check whether it is empty
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * Check whether it is not empty
     * @param collection
     * @return
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * <b>get Map'value by Map'key</b>
     * @param map
     * @param key
     * @return
     */
    public static String getNotNullString(Map map, String key){
        return map == null ? "" : map.get(key) == null ? "" : map.get(key).toString();
    }

    /**
     * 根据@Order()注解进行排序
     */
    public static <T> void sortByOrder(List<Class<T>> clazzList) {
        if (clazzList == null || clazzList.isEmpty()) {
            return;
        }
        int len = clazzList.size();
        int[] array = new int[len];
        for (int i = 0; i < len; i++) {
            Order order = clazzList.get(i).getAnnotation(Order.class);
            int value = HttpConstant.MIN_ORDER;
            if (order != null) {
                value = order.value();
            }
            array[i] = value;
        }
        // array = {1, 25, 50, 75, 100}
        quickSort(array, 0, len - 1);
        for (int i = 0; i < len; i++) {
            int val = array[i];
            for (int j = i; j < len; j++) {
                Order order = clazzList.get(j).getAnnotation(Order.class);
                int value = HttpConstant.MIN_ORDER;
                if (order != null) {
                    value = order.value();
                }
                if (val == value) {
                    if (i == j) continue;
                    Class<T> clazz = clazzList.get(i);
                    clazzList.set(i, clazzList.get(j));
                    clazzList.set(j, clazz);
                    continue;
                }
            }

        }
    }

    /**
     * 快速排序
     * 1.选定一个基准值，array[low]
     * 2.右指针从右向左遍历high--，查找比基准值小的数据，左指针从左向右low++，查找比基准值大的数据
     * 3.如果指针未相遇，交换左右两值位置，如果指针相遇，则替换基准值的位置
     * 4.左递归，右递归
     * @param array
     * @param low
     * @param high
     */
    public static void quickSort(int[] array, int low, int high) {

        // 方法退出条件,指针相遇或错过
        if (low >= high) {
            return;
        }
        // 1. 指定基准值和左右指针记录位置
        int pivot = array[low];
        int l = low;
        int r = high;
        int temp = 0;
        // 2. 遍历条件，左右指针位置
        while (l < r) {
            // 2.1 右侧遍历
            while (l < r && array[r] >= pivot) {
                r--;
            }
            // 2.2 左侧遍历
            while (l < r && array[l] <= pivot) {
                l++;
            }
            // 2.3 l指针还在r指针左侧，尚未相遇
            if (l < r) {
                temp = array[l];
                array[l] = array[r];
                array[r] = temp;
            }
        }
        // 3. 当左右指针相遇，交换基准值位置
        array[low] = array[l];
        array[l] = pivot;
        // 4. 根据条件左侧递归遍历
        if (low < l) {
            quickSort(array, low, l - 1);
        }
        // 5. 根据条件右侧递归遍历
        if (r < high) {
            quickSort(array, r + 1, high);
        }

    }

}
