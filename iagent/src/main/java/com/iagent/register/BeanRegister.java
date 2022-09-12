package com.iagent.register;

/**
 * 公有注册接口
 */
public interface BeanRegister<T> {

    /**
     * 注册到map容器中
     * @param beanName key
     * @param target value
     * @return
     */
    boolean registerBean(String beanName, T target);

    /**
     * 根据名称从容器中获取，更具不同的Class装换成不同对象
     * @param beanName
     * @param tClass
     * @param <V>
     * @return
     */
    <V> V getBeanByClass(String beanName, Class<V> tClass);

    /**
     * 根据名称获取Object对象
     * @param beanName
     * @return
     */
    T getBeanObject(String beanName);

    /**
     * 是否包含bean的名称
     * @param beanName
     * @return
     */
    boolean containBeanName(String beanName);

    /**
     * 根据名称删除容器容器中的数据
     * @param beanName
     * @return
     */
    boolean removeBean(String beanName);

    /**
     * 获取到所有得注册名称
     * @return
     */
    String[] getAllBeanNames();

    /**
     * get size
     * @return
     */
    int size();
}
