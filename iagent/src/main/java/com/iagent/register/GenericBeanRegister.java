package com.iagent.register;

/**
 * @author liujieyu
 * @date 2022/5/25 19:16
 * @desciption
 */
public class GenericBeanRegister<T> extends AbstractBeanRegister<T> {

    public GenericBeanRegister(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public <V> V getBeanByClass(String beanName, Class<V> tClass) {
        Object bean = this.getBeanObject(beanName);
        if (tClass.isInstance(bean)) {
            return (V) bean;
        }
        return null;
    }
}
