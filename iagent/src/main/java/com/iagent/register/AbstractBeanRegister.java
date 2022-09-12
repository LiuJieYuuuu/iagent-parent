package com.iagent.register;

import com.iagent.logging.LogFactory;
import com.iagent.logging.Logger;
import com.iagent.util.Assert;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 抽象的注册器，编写公用方法
 */
public abstract class AbstractBeanRegister<T> implements BeanRegister<T> {

    private static final Logger logger = LogFactory.getLogger(AbstractBeanRegister.class);

    /**
     * 注册器核心容器
     */
    private ConcurrentHashMap<String, Object> coreContainer = null;

    public AbstractBeanRegister() {
        super();
        coreContainer = new ConcurrentHashMap<>(16);
    }

    public AbstractBeanRegister(int initialCapacity) {
        super();
        coreContainer = new ConcurrentHashMap<>(initialCapacity);
    }

    /**
     * 注册Bean到容器中
     * @param beanName key
     * @param target value
     * @return
     */
    public boolean registerBean(String beanName, T target) {
        Assert.notNull(beanName, "Register Container bean name is Null!");
        Assert.notNull(target, "Register Container target is Null!");
        try {
            coreContainer.put(beanName, target);
            return true;
        } catch (Throwable t) {
            logger.error("Register Container is Error, Message:" + t.getMessage());
            return false;
        }
    }

    /**
     * 根据名称获取容器中的对象
     * @param beanName
     * @return
     */
    public T getBeanObject(String beanName) {
        Assert.notNull(beanName, "The BeanName of Get Object By Container is Null!");
        return (T) coreContainer.get(beanName);
    }

    /**
     * 判断这个名称在容器中是否包含对应的数据
     * @param beanName
     * @return
     */
    public boolean containBeanName(String beanName) {
        return coreContainer.containsKey(beanName);
    }

    /**
     * 删除容器中指定的名称
     * @param beanName
     * @return
     */
    public boolean removeBean(String beanName) {
        Assert.notNull(beanName, "Delete Container bean name is Null!");
        try {
            coreContainer.remove(beanName);
            return true;
        } catch (Throwable t) {
            logger.error("Delete from Container By BeanName is Error, BeanName:" + beanName);
            return false;
        }
    }

    @Override
    public String[] getAllBeanNames() {
        String[] keys = new String[coreContainer.size()];
        Enumeration<String> enumeration = coreContainer.keys();
        int index = 0;
        while (enumeration.hasMoreElements()) {
            keys[index] = enumeration.nextElement();
            index ++;
        }
        return keys;
    }

    @Override
    public int size() {
        return coreContainer.size();
    }
}
