package com.iagent.register;

import com.iagent.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * 别名注册器
 */
public class AliasNameRegister<T> extends AbstractBeanRegister<T> {

    public AliasNameRegister(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public <V> V getBeanByClass(String beanName, Class<V> tClass) {
        Assert.isInstanceOf(tClass, this.getBeanObject(beanName));
        return (V) this.getBeanObject(beanName);
    }

    /**
     * 注册别名
     * @param beanName
     * @param target
     */
    public void registerAlias(String beanName, T target) {
        this.registerBean(beanName, target);
    }

    /**
     * 获取别名对应的数据
     * @param beanName
     * @return
     */
    public T getAlias(String beanName) {
        return this.getBeanObject(beanName);
    }

    /**
     * 拿到所有为V以及它得子类的 Class 集合
     * @param vClass
     * @param <V>
     * @return
     */
    public <V> List<Class<V>> getAliasClassListByClass(Class<V> vClass) {
        List<Class<V>> resultList = new ArrayList<>(16);
        for (String key : getAllBeanNames()) {
            T alias = this.getAlias(key);
            if (alias instanceof Class &&
                    vClass.isAssignableFrom((Class<?>) alias)) {
                resultList.add((Class<V>) alias);
            }
        }

        return resultList;
    }
}
