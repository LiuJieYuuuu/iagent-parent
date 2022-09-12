package com.iagent.scan.resolver;

import com.iagent.config.IagentConfiguration;
import com.iagent.annotation.IagentUrl;
import com.iagent.bean.IagentBean;
import com.iagent.bean.IagentBeanWrapper;
import com.iagent.bean.IagentParamBean;
import com.iagent.logging.LogFactory;
import com.iagent.logging.Logger;
import com.iagent.request.ApacheHttpClientExecutor;
import com.iagent.request.HttpExecutor;
import com.iagent.request.SimpleHttpExecutor;
import com.iagent.scan.resolver.annotation.ParameterResolver;
import com.iagent.util.ClassUtils;
import com.iagent.util.ResourceUtils;
import com.iagent.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author liujieyu
 * @date 2022/5/25 20:35
 * @desciption
 */
public class NativeInterfaceAnnotationResolver extends AbstractInterfaceAnnotationResolver {

    private static final Logger logger = LogFactory.getLogger(NativeInterfaceAnnotationResolver.class);

    public NativeInterfaceAnnotationResolver(IagentConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void handlerInterfaceMethod(IagentBean pBean, Method method) {
        // create new Object
        String parentUrl = pBean.getUrl();
        // resolver method annotation
        IagentUrl iagentUrl = method.getAnnotation(IagentUrl.class);
        // get result url
        String requestUrl = null;
        if (parentUrl.endsWith(ResourceUtils.URL_SEPARATOR) && iagentUrl.value().startsWith(ResourceUtils.URL_SEPARATOR)) {
            requestUrl = parentUrl.substring(0, parentUrl.length() - 1) + iagentUrl.value();
        } else if (!parentUrl.endsWith(ResourceUtils.URL_SEPARATOR) && !iagentUrl.value().startsWith(ResourceUtils.URL_SEPARATOR)) {
            requestUrl = parentUrl + ResourceUtils.URL_SEPARATOR + iagentUrl.value();
        } else {
            requestUrl = parentUrl + iagentUrl.value();
        }
        // create method iagent bean
        IagentBean methodBean = IagentBean.IagentBeanBuilder
                .create(getDefaultRequestConfig())
                .url(requestUrl)
                .requestType(iagentUrl.requestType())
                .contentType(iagentUrl.contentType())
                .connectionTime(iagentUrl.connectionTime())
                .readTime(iagentUrl.readTime())
                .build();
        //拿到当前接口执行器
        HttpExecutor httpExecutor = null;
        Class<? extends HttpExecutor> clazz = iagentUrl.httpExecutor();
        // 强制转换成Apache Executor
        if (SimpleHttpExecutor.class.isAssignableFrom(clazz)) {
            clazz = ApacheHttpClientExecutor.class;
        }
        if (containHttpExecutor(clazz)) {
            httpExecutor = getHttpExecutor(clazz);
        } else {
            httpExecutor = ClassUtils.newInstance(clazz, new Class<?>[]{IagentConfiguration.class},
                    new Object[]{getConfiguration()});
            //注册到执行器容器中
            registerHttpExecutor(clazz, httpExecutor);
        }
        Parameter[] parameters = method.getParameters();
        //Method 参数建造者
        IagentParamBean.IagentParamBeanBuilder builder = IagentParamBean.IagentParamBeanBuilder.create()
                .setParameterClass(ClassUtils.getMethodParameterClass(method));
        for (int i = 0; i < parameters.length; i++) {
            try {
                for (ParameterResolver parameterResolver : getParameterResolvers()) {
                    if (parameterResolver.isResolver(parameters[i])) { // 如果当前参数解析器能解析，则进行解析
                        parameterResolver.parameterHandle(parameters[i], builder, i);
                    }
                }
            } catch (Throwable throwable) {
                logger.error("Resolver Method [" + ClassUtils.getClassPathByMethod(method) + "] Error :" + throwable.getMessage());
                throw new IllegalArgumentException("Resolver Method [" + ClassUtils.getClassPathByMethod(method) + "] Error :" + throwable.getMessage());
            }
        };
        IagentParamBean paramBean = builder.build();
        // create wrapper method 对象，注册到容器中
        IagentBeanWrapper wrapper = new IagentBeanWrapper();
        wrapper.setExecutor(httpExecutor);
        wrapper.setParamBean(paramBean);
        wrapper.setBean(methodBean);
        //设置返回值
        wrapper.setReturnClassType(method.getReturnType());
        registerBeanWrapper(method, wrapper);
    }

    @Override
    public IagentBean handlerClassIagentBean(Class<?> tClass) {
        // get base info
        IagentUrl annotation = tClass.getAnnotation(IagentUrl.class);
        // create Iagent Bean Object
        IagentBean iagentBean = IagentBean.IagentBeanBuilder
                .create(getDefaultRequestConfig())
                .url(annotation.value())
                .requestType(annotation.requestType())
                .contentType(annotation.contentType())
                .connectionTime(annotation.connectionTime())
                .readTime(annotation.readTime())
                .build();
        // get http executor
        Class<? extends HttpExecutor> aClass = annotation.httpExecutor();
        // 强制转换成Apache Executor
        if (SimpleHttpExecutor.class.isAssignableFrom(aClass)) {
            aClass = ApacheHttpClientExecutor.class;
        }
        // register executor to container
        if (StringUtils.isEquals(ClassUtils.getClassPathByClass(aClass), ClassUtils.getClassPathByClass(HttpExecutor.class))) {
            // 如果是默认值HttpExecutor, 那么加载默认值
            registerHttpExecutor(aClass, ClassUtils.newInstance(getDefaultRequestConfig().getHttpExecutor(),
                    new Class<?>[]{IagentConfiguration.class}, new Object[]{getConfiguration()}));
        }

        return iagentBean;
    }

}
