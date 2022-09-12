# IAGENT

接口注解声明式HTTP请求，针对大部分对接第三方接口场景，便于统一管理第三方接口



## 简单入门

### 使用Iagent

#### Maven

```xml
<dependency>
    <groupId>com.iagent</groupId>
    <artifactId>iagent</artifactId>
    <version>2.0.1.SNAPSHOT</version>
</dependency>
```

#### 代码示例

* 接口类

  ```java
  @IagentUrl(value = "http://127.0.0.1:8999/fegin")
  public interface IagentDao {
  
      @IagentUrl(value = "/getAsyncUserInfo")
      Map<String, Object> getAsyncUserInfo();
  
  }
  ```

* 使用示例

  ```Java
  IagentConfiguration configuration = new IagentConfiguration();
  configuration.setBasePackages(new String[]{"org.example.idao"});
  AbstractIagentFactory iagentFactory = new DefaultIagentFactory(configuration);
  IagentDao iagentDao = iagentFactory.getProxy(IagentDao.class);
  Map<String, Object> result = iagentDao.getAsyncUserInfo();
  System.out.println("result:" + result);
  ```

* 执行结果

  ```
  result:{name=Netty, id=123, timestamp=1662554680100}
  ```

  

### Iagent集成Spring

#### Maven

```xml
<dependency>
    <groupId>com.iagent</groupId>
    <artifactId>iagent</artifactId>
    <version>2.0.1.SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>com.iagent</groupId>
    <artifactId>iagent-spring</artifactId>
    <version>1.0.1.SNAPSHOT</version>
</dependency>
```

#### 代码示例

* Spring配置

  ```java
  @IagentComponentScan(value = "org.example.idao")
  @Configuration
  public class IagentInfoConfig  {
      
  }
  ```

* 使用示例

  ```java
  @Autowired
  IagentDao iagentDao;
  
  @Test
  public void contextTest() {
  	Map<String, Object> result = iagentDao.getAsyncUserInfo();
  	System.out.println("result:" + result);
  }
  ```

* 执行结果

  ```
  result:{name=Netty, id=123, timestamp=1662554680100}
  ```

  

### Iagent集成SpringBoot

#### Maven

```xml
<dependency>
    <groupId>com.iagent</groupId>
    <artifactId>iagent-spring-boot-starter</artifactId>
    <version>1.0.1.SNAPSHOT</version>
</dependency>
```

#### 使用示例

* SpringBoot配置

  ```java
  @EnabledIagent(value = {"org.example.idao"})
  @SpringBootApplication
  public class SpringBootApp {
  
      public static void main(String[] args) {
          SpringApplication.run(SpringBootApp.class, args);
      }
  
  }
  ```

* 使用示例

  ```java
  @Autowired
  IagentDao iagentDao;
  
  @Test
  public void contextTest() {
  	Map<String, Object> result = iagentDao.getAsyncUserInfo();
  	System.out.println("result:" + result);
  }
  ```

* 执行结果

  ```
  result:{name=Netty, id=123, timestamp=1662554680100}
  ```


## 详细说明文档

### 生成AbstractIagentFactory对象

* 使用扫描包路径创建对象

  ```java
  AbstractIagentFactory iagentFactory = new DefaultIagentFactory(new String[]{"org.example.iagent"});
  ```

* 通过创建IagentConfiguration对象并传入

  ```
  IagentConfiguration configuration = new IagentConfiguration("org.example.iagent");
  AbstractIagentFactory iagentFactory = new DefaultIagentFactory(configuration);
  ```

包路径下只会扫描带有@IagentUrl注解的接口

详细IagentConfiguration对象参数信息见 附录一

### 注解解析

#### @IagentUrl

该注解可以声明在类以及函数上，如果类上和函数上都有，则最终的HTTP url值为两者合并。

示例：

```java
@IagentUrl(value = "http://127.0.0.1:8999/fegin")
public interface ObjectIDao {

    @IagentUrl(value = "/object/getStringData")
    String getStringData();
}
```

该注解值具有继承特性：即类上设置的参数，在函数上并未设置时则函数的参数继承类上的参数值；类上的参数未设置的值默认采用ReqesutConfig的配置

详细参数见 附录一 注解IagentUrl 详细参数

#### @ParamKey

标记为普通参数；即跟在url的参数，如果设置为表单请求，则放在表单里面

示例：

```java
 @IagentUrl(value = "/object/getStringData")
 String getStringData(@ParamKey("name") String name);
```

参数：

| 参数名称 | 描述            | 备注           |
| -------- | --------------- | -------------- |
| value    | HTTP的参数key值 | 该参数为必填项 |

#### @ParamBody

标记为放置在Body里面的参数，支持两种参数内容类型（application/json，multipart/form-data）

示例：

```java
@IagentUrl(value = "/collection/getListData", requestType = HttpEnum.POST, contentType = HttpConstant.APPLICATION_JSON)
List getListData(@ParamBody Map params);
```

参数：

| 参数名称 | 描述                 | 备注                        |
| -------- | -------------------- | --------------------------- |
| required | 要求该参数是否为必填 | 默认值为false，该参数非必填 |

对于application/json，则将参数使用适配JSON框架进行序列化

对于multipart/form-data，则将参数解析成key-value的表单形式提交

#### @PathKey

标记为url上的路径参数

示例：

```java
@IagentUrl(value = "/collection/{url}", requestType = HttpEnum.POST)
List getListDataUrl( @PathKey("url") String urlKey);

```

参数：

| 参数名称 | 描述                 | 备注           |
| -------- | -------------------- | -------------- |
| value    | 与url上的 {key} 映射 | 该参数为必填项 |

#### @ParamHeader

标记为请求头的参数

示例：

```java
@IagentUrl(value = "/object/getResultData", requestType = HttpEnum.POST)
ReturnResult getResultData(@ParamKey("data") String data, @ParamHeader("token") String token);
```

参数：

| 参数名称 | 描述              | 备注           |
| -------- | ----------------- | -------------- |
| value    | HTTP请求头的key值 | 该参数为必填项 |

### 自定义HTTP执行器

IAGENT框架提供自定义HTTP执行器功能，默认使用Apache Http Client实现，使用PoolManager形式提升HTTP请求性能。

自定义执行器目前提供两种实现方式

#### 继承AbstractHttpExecutor抽象类

继承该类可以拿到IagentConfiguration对象，并且可以拿到默认结果解析器（ResultParser），通过自定义扩展结果解析器实现解析HTTP请求返回结果。

```java
public class MyExecutor extends AbstractHttpExecutor {

    public MyExecutor(IagentConfiguration configuration) {
        super(configuration);
    }

    public Object sendHttp(IagentBeanWrapper bean, Object[] args) throws Exception {
        return new HashMap<String, Object>(){{
            put("code", 0);
            put("msg", "no message");
        }};
    }
}
```

如果想使用框架内的结果解析器解析，建议使用这种方法

#### 实现HttpExecutor接口

通过实现该接口，可完全自主实现HTTP请求方式，更加灵活的操作

```java
public class MyHttpExecutor implements HttpExecutor {

    public Object sendHttp(IagentBeanWrapper bean, Object[] args) {
        return new HashMap<String, Object>(){{
            put("code", 0);
            put("msg", "no message");
        }};
    }
}
```

不过使用该方法在服务启动时会提示警告

> ```
> [warn] The Http Executor is Not extends AbstractHttpExecutor！
> ```

IagentBeanWrapper类详细参数见 附录一

### 配置项别名集合

为了方便操作，在IAGENT框架里面使用了别名记录各种配置项，所有的配置均从配置项别名集合获取（除了日志和JSON适配器），如果需要进行配置则增加或减少配置项即可。

目前默认的配置项别名集合有：

```java
// 日志适配器
aliasRegister.registerAlias("SLF4J", Slf4jImpl.class);
aliasRegister.registerAlias("COMMONS_LOGGING", JakartaCommonsLoggingImpl.class);
aliasRegister.registerAlias("LOG4J", Log4jImpl.class);
aliasRegister.registerAlias("LOG4J2", Log4j2Impl.class);
aliasRegister.registerAlias("JDK_LOGGING", Jdk14LoggingImpl.class);
aliasRegister.registerAlias("STDOUT_LOGGING", ConsoleImpl.class);
aliasRegister.registerAlias("NO_LOGGING", NoLoggingImpl.class);
// JSON适配器
aliasRegister.registerAlias("FASTJSON", FastJsonSupport.class);
aliasRegister.registerAlias("GSON", GsonSupport.class);
aliasRegister.registerAlias("JACKSON", JacksonSupport.class);
// 注解解析器
aliasRegister.registerAlias("Native", NativeInterfaceAnnotationResolver.class);
// 参数解析器
aliasRegister.registerAlias("PathKeyParameterResolver", PathKeyParameterResolver.class);
aliasRegister.registerAlias("GenericParameterResolver", GenericParameterResolver.class);
aliasRegister.registerAlias("HeaderParameterResolver", HeaderParameterResolver.class);
aliasRegister.registerAlias("BodyParameterResolver", BodyParameterResolver.class);
// 结果解析器
aliasRegister.registerAlias("ObjectResultParser", ObjectResultParser.class);
aliasRegister.registerAlias("BinaryResultParser", BinaryResultParser.class);
aliasRegister.registerAlias("NumberResultParser", NumberResultParser.class);
```



### 集成Spring

#### 注解解析

##### @IagentComponentScan

该注解使用了Spring的@Import注解实现，实际将IagentConfiguration配置的参数放置在注解上

具体参数见 附录一 



## 附录一

### IagentConfiguration 详细参数

| 参数名称             | 类型                             | 描述信息                                                     | 默认值                                                       |
| -------------------- | -------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| basePackages         | String[]                         | 扫描包路径，支持模糊扫描包；例如：org.example.*。默认值根类路径 | ""                                                           |
| logImpl              | Class<? extends Logger>          | 日志适配器，需指定使用哪种日志框架，目前支持<br />org.slf4j.Logger<br />org.apache.logging.log4j.Logger<br />org.apache.log4j.Logger<br />java.util.logging.Logger<br />org.apache.commons.logging.Log<br />五种日志框架以及System.out打印； | 未设置则按照一下顺序自动适配<br />SLF4J -> COMMONS_LOG -> LOG4J -> LOG4J2 -> JDK -> CONSOLE -> NO_LOG |
| jsonSupport          | Class<? extends JSONSupport>     | JSON解析框架适配器，需指定使用JSON框架<br />目前支持FastJson,Gson,Jackson<br />目前强制要求使用其中的一种，否则JSON解析报错 | 未设置则按照一下顺序自动适配<br />FastJson -> Gson -> Jackson |
| handlerName          | String                           | 注解解析器，目前仅支持Native解析器                           | Native                                                       |
| defaultRequestConfig | com.iagent.request.RequestConfig | 默认请求参数                                                 | 见附录一RequestConfig详细参数                                |

### RequestConfig 详细参数

| 参数名称       | 类型                          | 描述信息                              | 默认值                            |
| -------------- | ----------------------------- | ------------------------------------- | --------------------------------- |
| requestType    | com.iagent.constant.HttpEnum  | 请求类型，目前支持GET/POST/PUT/DELETE | HttpEnum.GET                      |
| contentType    | String                        | 请求参数内容类型                      | application/x-www-form-urlencoded |
| connectionTime | int                           | 连接服务时间（毫秒）                  | 2000                              |
| readTime       | int                           | 读取接口内容时间（毫秒）              | 10000                             |
| httpExecutor   | Class<? extends HttpExecutor> | HTTP执行器                            | ApacheHttpClientExecutor.class    |

### 注解IagentUrl 详细参数

| 参数名称       | 类型                          | 描述信息                              |
| -------------- | ----------------------------- | ------------------------------------- |
| value          | String                        | HTTP请求url                           |
| requestType    | com.iagent.constant.HttpEnum  | 请求类型，目前支持GET/POST/PUT/DELETE |
| contentType    | String                        | 请求参数内容类型                      |
| connectionTime | int                           | 连接服务时间（毫秒）                  |
| readTime       | int                           | 读取接口内容时间（毫秒）              |
| httpExecutor   | Class<? extends HttpExecutor> | 指定HTTP执行器                        |

### IagentBeanWrapper 详细参数

| 参数名称        | 类型                            | 描述信息                                  |
| --------------- | ------------------------------- | ----------------------------------------- |
| bean            | com.iagent.bean.IagentBean      | 注解@IagentUrl标识的所有信息              |
| executor        | com.iagent.request.HttpExecutor | 当前HTTP执行器                            |
| paramBean       | com.iagent.bean.IagentParamBean | 所有的参数信息，见IagentParamBean参数解析 |
| returnClassType | Class<?>                        | 接口返回值类型                            |

### IagentParamBean 参数解析

| 参数名称         | 类型                 | 描述信息                                         |
| ---------------- | -------------------- | ------------------------------------------------ |
| parametersClass  | Class<?>[]           | 参数Class对象集合                                |
| paramIndex       | Map<String, Integer> | @ParamKey标识的参数集合，参数名称对应参数下标    |
| pathIndex        | Map<String, Integer> | @PathKey标识的参数集合，参数名称对应参数下标     |
| headerIndex      | Map<String, Integer> | @ParamHeader标识的参数集合，参数名称对应参数下标 |
| bodyIndex        | Integer              | @ParamBody标识的参数位置，由于Body参数只能有一个 |
| inputStreamIndex | Map<String, Integer> | @ParamKey标识参数并且Class文件类型或IO流类型     |

### 注解IagentComponentScan 参数解析

| 参数名称      | 类型                              | 描述信息                                     |
| ------------- | --------------------------------- | -------------------------------------------- |
| value         | String[]                          | 包扫描路径                                   |
| jsonSupport   | String                            | 使用哪种JSON进行适配                         |
| logImpl       | String                            | 使用哪种日志框架进行适配                     |
| requestConfig | IagentComponentScan.RequestConfig | 请求参数默认值，见RequestConfig 详细参数即可 |
| resolver      | String                            | 注解解析器，目前仅支持Native解析器           |

