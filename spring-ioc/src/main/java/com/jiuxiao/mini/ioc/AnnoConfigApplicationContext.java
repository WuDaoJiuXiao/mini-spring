package com.jiuxiao.mini.ioc;

import com.jiuxiao.mini.annotation.*;
import com.jiuxiao.mini.exception.*;
import com.jiuxiao.mini.io.PropertyResolver;
import com.jiuxiao.mini.io.ResourceResolver;
import com.jiuxiao.mini.util.ClassUtil;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author 悟道九霄
 * @Date 2024/1/19 15:50
 * @Description 使用注解配置生成上下文
 */
public class AnnoConfigApplicationContext implements ConfigApplicationContext {

    /* 属性解析器 */
    protected final PropertyResolver propertyResolver;

    /* 加载的所有 Bean 实例，key 为字符传类型的 beanName，value 为 bean 详细信息 */
    protected final Map<String, BeanDefinition> beans;

    /* 每个 bean 的后处理器列表 */
    private final List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    /* 创建的 bean 的 name 集合 */
    private final Set<String> creatBeanNameSet;

    public AnnoConfigApplicationContext(Class<?> clazz, PropertyResolver propertyResolver) {
        ApplicationContextUtil.setApplicationContext(this);
        this.propertyResolver = propertyResolver;

        // 扫描并获取所有 bean 的 class 类型，然后创建所有的 bean 定义
        final Set<String> beanClassNameSet = scanForAllClassName(clazz);
        this.beans = createBeanDefinition(beanClassNameSet);

        // 创建 beanName 并检测循环依赖
        this.creatBeanNameSet = new HashSet<>();

        // 创建 @Configuration 类型的 Bean
        List<String> configurationList = this.beans.values().stream()
                .filter(this::isConfigurationBean).sorted()
                .map(beanDefinition -> {
                    createBeanAsEarlySingleton(beanDefinition);
                    return beanDefinition.getName();
                }).collect(Collectors.toList());
        this.creatBeanNameSet.addAll(configurationList);

        // 创建 BeanPostProcessor 类型的 bean
        List<BeanPostProcessor> processorBeanList = this.beans.values().stream()
                .filter(this::isPostProcessorBean).sorted()
                .map(beanDefinition -> ((BeanPostProcessor) createBeanAsEarlySingleton(beanDefinition)))
                .collect(Collectors.toList());
        this.beanPostProcessorList.addAll(processorBeanList);

        // 创建其他普通类型的 bean
        createNormalBeans();

        // 通过字段和 set 方式注入依赖但不使用 init 初始化
        this.beans.values().forEach(this::injectBean);

        // 使用 init 方法初始化 bean
        this.beans.values().forEach(this::initBean);
    }

    /**
     * @return: void
     * @description 创建其他普通类型的 bean
     * @date 2024/1/20 14:20
     */
    private void createNormalBeans() {
        List<BeanDefinition> collected = this.beans.values().stream()
                .filter(beanDefinition -> beanDefinition.getInstance() == null).sorted()
                .collect(Collectors.toList());
        collected.forEach(beanDefinition -> {
            // 某 bean 没有被创建，需要创建
            if (beanDefinition.getInstance() == null) {
                createBeanAsEarlySingleton(beanDefinition);
            }
        });
    }

    /**
     * @param name 要查找的 Bean 的名称
     * @return: boolean
     * @description 查找是否包含指定 name 的 Bean
     * @date 2024/1/22 16:06
     */
    @Override
    public boolean containsBean(String name) {
        return beans.containsKey(name);
    }

    /**
     * @param name 要查找的 bean 名称
     * @return: T
     * @description 通过 name 查找对应的 Bean，不存在抛异常
     * @date 2024/1/22 15:50
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(String name) {
        BeanDefinition beanDefinition = beans.get(name);
        if (beanDefinition == null) {
            throw new NoSuchBeanDefinitionException(String.format("No bean defined with name %s", name));
        }
        return ((T) beanDefinition.getRequiredInstance());
    }

    /**
     * @param name         要查找的 bean 名称
     * @param requiredType 要查找的 bean 类型
     * @return: T
     * @description 通过 name 和 type 查找对应的 Bean，不存在、存在 type 不匹配均抛异常
     * @date 2024/1/22 15:52
     */
    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        T bean = findBean(name, requiredType);
        if (bean == null) {
            throw new NoSuchBeanDefinitionException(String.format(
                    "No bean defined with name %s and type %s", name, requiredType
            ));
        }
        return bean;
    }

    /**
     * @param requiredType 要查找的 bean 类型
     * @return: T
     * @description 通过 type 查找 bean,不存在抛出异常；存在多个但缺少唯一 @Primary 标注，抛出异常
     * @date 2024/1/22 16:04
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        BeanDefinition beanDefinition = findBeanDefinition(requiredType);
        if (beanDefinition == null) {
            throw new NoSuchBeanDefinitionException(String.format(
                    "No bean defined with type %s", requiredType
            ));
        }
        return ((T) beanDefinition.getRequiredInstance());
    }

    /**
     * @param requiredType 要查找的 Bean 类型
     * @return: java.util.List<T>
     * @description 通过指定的 type 查找所有的 Bean
     * @date 2024/1/22 16:08
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getBeans(Class<T> requiredType) {
        List<BeanDefinition> definitionList = findBeanDefinitions(requiredType);
        if (definitionList.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<T> list = new ArrayList<>(definitionList.size());
        for (BeanDefinition definition : definitionList) {
            list.add(((T) definition.getRequiredInstance()));
        }
        return list;
    }

    /**
     * @param type 要查找的 Bean 类型
     * @return: java.util.List<com.jiuxiao.mini.ioc.BeanDefinition>
     * @description 根据 type 查找若干个 BeanDefinition，返回结果为所有查找到的总集合
     * @date 2024/1/22 15:34
     */
    @Override
    public List<BeanDefinition> findBeanDefinitions(Class<?> type) {
        return beans.values().stream()
                .filter(beanDefinition -> type.isAssignableFrom(beanDefinition.getBeanClass()))
                .sorted().collect(Collectors.toList());
    }

    /**
     * @param type 要查找的 Bean 类型
     * @return: com.jiuxiao.mini.ioc.BeanDefinition
     * @description 根据 type 查找对应的 BeanDefinition 对象，如果不存在返回 null，如果存在则返回使用 @Primary 标注的一个
     * 如果有多个 @Primary 标注或者没有标注，均抛出异常
     * @date 2024/1/22 15:37
     */
    @Nullable
    @Override
    public BeanDefinition findBeanDefinition(Class<?> type) {
        List<BeanDefinition> beanDefinitionList = findBeanDefinitions(type);
        if (beanDefinitionList.isEmpty()) {
            return null;
        }
        if (beanDefinitionList.size() == 1) {
            return beanDefinitionList.get(0);
        }

        // 超过一个就需要找使用 @Primary 标注的，并且被 @Primary 标注的也只能有一个
        List<BeanDefinition> primaryBeanList = beanDefinitionList.stream().filter(BeanDefinition::isPrimary).collect(Collectors.toList());
        if (primaryBeanList.isEmpty()) {
            throw new NoUniqueBeanDefinitionException(String.format(
                    "Multiple bean with type %s found, but no @Primary specified", type.getName()
            ));
        } else if (primaryBeanList.size() == 1) {
            return primaryBeanList.get(0);
        } else {
            throw new NoUniqueBeanDefinitionException(String.format(
                    "Multiple bean with type %s found, but multiple @Primary specified", type.getName()
            ));
        }
    }

    /**
     * @param name 要查找的 BeanDefinition 对象名称
     * @return: com.jiuxiao.mini.ioc.BeanDefinition
     * @description 根据 name 查找对应的 BeanDefinition 对象，若 name 不存在返回 null
     * @date 2024/1/22 15:27
     */
    @Nullable
    @Override
    public BeanDefinition findBeanDefinition(String name) {
        return beans.get(name);
    }

    /**
     * @param name         要查找的 BeanDefinition 对象名称
     * @param requiredType 要查找的 BeanDefinition 对象类型
     * @return: com.jiuxiao.mini.ioc.BeanDefinition
     * @description 根据 name 和 type 查找对应的 BeanDefinition 对象，若 name 不存在返回 null；name 存在但是 type 不匹配，抛出异常
     * @date 2024/1/22 15:29
     */
    @Nullable
    @Override
    public BeanDefinition findBeanDefinition(String name, Class<?> requiredType) {
        BeanDefinition beanDefinition = findBeanDefinition(name);
        if (beanDefinition == null) {
            return null;
        }
        if (!requiredType.isAssignableFrom(beanDefinition.getBeanClass())) {
            throw new BeanNotRequiredTypeException(String.format(
                    "Autowired required type %s, but bean %s has actual type is %s",
                    requiredType.getName(), name, beanDefinition.getBeanClass().getName()
            ));
        }
        return beanDefinition;
    }

    /**
     * @param def Bean的定义对象
     * @return: java.lang.Object
     * @description 创建一个Bean，然后使用BeanPostProcessor处理，但不进行字段和方法级别的注入
     * 如果创建的Bean不是Configuration 或 BeanPostProcessor，则在构造方法中注入，依赖Bean会自动创建
     * @date 2024/1/20 14:25
     */
    @Override
    public Object createBeanAsEarlySingleton(BeanDefinition def) {
        if (!this.creatBeanNameSet.add(def.getName())) {
            throw new UnsatisfiedDependencyException(
                    String.format("Circular dependency detected when create bean %s", def.getName())
            );
        }

        // Step 1 : 初始化 Bean 创造器
        Executable createExecutor;
        if (def.getFactoryName() == null) {
            createExecutor = def.getConstructor();
        } else {
            createExecutor = def.getFactoryMethod();
        }
        assert createExecutor != null;
        final Parameter[] parameters = createExecutor.getParameters();
        final Annotation[][] parameterAnnotations = createExecutor.getParameterAnnotations();
        Object[] objectArgs = new Object[parameters.length];

        // Step 2 : 校验初始化相关的参数及其格式
        for (int i = 0; i < parameters.length; i++) {
            // 获取需要校验的有关参数
            final Parameter param = parameters[i];
            final Annotation[] paramAnnotation = parameterAnnotations[i];
            final Value value = ClassUtil.getAnnotation(paramAnnotation, Value.class);
            final Autowired autowired = ClassUtil.getAnnotation(paramAnnotation, Autowired.class);
            boolean isConfiguration = isConfigurationBean(def);

            // @Configuration 类型的 bean 是 bean 工厂，不能使用 @Autowired 创建
            if (isConfiguration && autowired != null) {
                throw new BeanCreationException(String.format(
                        "Cannot specify @Autowired when create @Configuration bean %s : %s.",
                        def.getName(), def.getBeanClass().getName()
                ));
            }

            // BeanPostProcessor 不能依赖其他 Bean，且不能使用 @Autowired 创建
            boolean isProcessor = isPostProcessorBean(def);
            if (isProcessor && autowired != null) {
                throw new BeanCreationException(String.format(
                        "Cannot specify @Autowired when create BeanPostProcessor %s : %s.",
                        def.getName(), def.getBeanClass().getName()
                ));
            }

            // 参数需要 @Value 或 @Autowired 两者之一即可，拥有两个属于错误情况
            if (value != null && autowired != null) {
                throw new BeanCreationException(String.format(
                        "Cannot specify both @Autowired and @Value when create bean %s : %s.",
                        def.getName(), def.getBeanClass().getName()
                ));
            }

            // 参数需要 @Value 或 @Autowired 两者之一即可，一个都不拥有属于错误情况
            if (value == null && autowired == null) {
                throw new BeanCreationException(String.format(
                        "Must specify @Autowired or @Value when create bean %s : %s.",
                        def.getName(), def.getBeanClass().getName()
                ));
            }

            // 参数类型校验
            final Class<?> paramType = param.getType();
            if (value != null) { // 参数类型是 @Value，直接使用属性解析器获取值
                objectArgs[i] = this.propertyResolver.getRequiredProperty(value.value(), paramType);
            } else {
                // 参数是 @Autowired
                String name = autowired.name();
                boolean required = autowired.value();
                // 获取 @Autowired 注解的 BeanDefinition
                BeanDefinition dependBean = name.isEmpty() ? findBeanDefinition(paramType) : findBeanDefinition(name, paramType);
                // 当 required == true 时，所依赖的 bean 必须不为空
                if (required && dependBean == null) {
                    throw new BeanDefinitionException(String.format(
                            "Missing autowired bean with type %s when create bean %s : %s.",
                            paramType.getName(), def.getName(), def.getBeanClass().getName()
                    ));
                }
                // 当依赖的 bean 不为空时，获取依赖的 bean
                if (dependBean != null) {
                    Object beanInstance = dependBean.getInstance();
                    // 当前依赖的 bean 尚未进行初始化，递归调用进行初始化
                    if (beanInstance != null) {
                        beanInstance = createBeanAsEarlySingleton(dependBean);
                    }
                    objectArgs[i] = beanInstance;
                } else {
                    objectArgs[i] = null;
                }
            }
        }

        // Step 3 : 正式创建 Bean 的实例
        Object instance;
        String factoryName = def.getFactoryName();
        if (factoryName == null) { // 工厂方法为空，则使用构造方法创建；否则使用 @Bean 方式创建
            try {
                assert def.getConstructor() != null;
                instance = def.getConstructor().newInstance(objectArgs);
            } catch (Exception e) {
                throw new BeanCreationException(String.format(
                        "Exception then create bean %s : %s", def.getName(), def.getBeanClass().getName()
                ), e);
            }
        } else {
            Object bean = getBean(def.getFactoryName());
            try {
                assert def.getFactoryMethod() != null;
                instance = def.getFactoryMethod().invoke(bean, objectArgs);
            } catch (Exception e) {
                throw new BeanCreationException(String.format(
                        "Exception then create bean %s : %s", def.getName(), def.getBeanClass().getName()
                ), e);
            }
        }
        def.setInstance(instance);

        // Step 4 : 调用 PostProcessor 对 Bean 进行一些后处理
        for (BeanPostProcessor processor : beanPostProcessorList) {
            Object processed = processor.postProcessBeforeInitialization(def.getInstance(), def.getName());
            if (processed == null) {
                throw new BeanCreationException(String.format(
                        "PostBeanProcessor returns null when process bean %s by %s", def.getName(), processor
                ));
            }
            // 如果一个 BeanPostProcessor 替换了原始的 Bean，则更新 Bean 的引用
            if (def.getInstance() != processed) {
                def.setInstance(processed);
            }
        }
        return def.getInstance();
    }

    /**
     * @param beanDefinition 需要注入的 BeanDefinition 对象
     * @return: void
     * @description 注入依赖但是不调用 init 方法初始化
     * @date 2024/1/21 15:29
     */
    private void injectBean(BeanDefinition beanDefinition) {
        final Object beanInstance = getProxiedInstance(beanDefinition);
        try {
            injectProperties(beanDefinition, beanDefinition.getBeanClass(), beanInstance);
        } catch (ReflectiveOperationException e) {
            throw new BeanCreationException(e);
        }
    }

    /**
     * @param beanDefinition 需要注入的 BeanDefinition 对象
     * @return: void
     * @description 注入依赖，并且调用 init 方法初始化
     * @date 2024/1/21 15:58
     */
    private void initBean(BeanDefinition beanDefinition) {
        final Object beanInstance = getProxiedInstance(beanDefinition);
        callInitMethod(beanInstance, beanDefinition.getInitMethod(), beanDefinition.getInitMethodName());
        // 调用 BeanPostProcessor.postProcessAfterInitialization()
        beanPostProcessorList.forEach(beanPostProcessor -> {
            Object processor = beanPostProcessor.postProcessAfterInitialization(beanDefinition.getInstance(), beanDefinition.getName());
            beanDefinition.setInstance(processor);
        });
    }

    /**
     * @param beanDefinition 要获取代理实例的 BeanDefinition 对象
     * @return: java.lang.Object
     * @description 获取某个 BeanDefinition 的代理对象
     * @date 2024/1/23 14:25
     */
    private Object getProxiedInstance(BeanDefinition beanDefinition) {
        Object instance = beanDefinition.getInstance();
        // 如果代理改变了原始的 bean，又希望注入到原始的 bean，则交由 BeanProcessor 指定原始的 bean
        ArrayList<BeanPostProcessor> beanPostProcessors = new ArrayList<>(beanPostProcessorList);
        Collections.reverse(beanPostProcessors);
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            Object postedInstance = beanPostProcessor.postProcessOnSetProperty(instance, beanDefinition.getName());
            if (postedInstance != instance) {
                instance = postedInstance;
            }
        }
        return instance;
    }

    /**
     * @param beanInstance   bean实例
     * @param initMethod     初始化方法
     * @param initMethodName 初始化方法名
     * @return: void
     * @description 调用初始化方法
     * @date 2024/1/23 14:25
     */
    private void callInitMethod(Object beanInstance, Method initMethod, String initMethodName) {
        if (initMethod != null) {
            try {
                initMethod.invoke(beanInstance);
            } catch (ReflectiveOperationException e) {
                throw new BeanCreationException(e);
            }
        } else if (initMethodName != null) {
            Method namedMethod = ClassUtil.getNamedMethod(beanInstance.getClass(), initMethodName);
            namedMethod.setAccessible(true);
            try {
                namedMethod.invoke(beanInstance);
            } catch (ReflectiveOperationException e) {
                throw new BeanCreationException(e);
            }
        }
    }

    /**
     * @param beanDefinition 需要注入的 BeanDefinition 对象
     * @param beanClass      注入的 bean 类型
     * @param beanInstance   注入的 bean 实例
     * @return: void
     * @description 注入 Bean
     * @date 2024/1/21 15:42
     */
    private void injectProperties(BeanDefinition beanDefinition, Class<?> beanClass, Object beanInstance) throws ReflectiveOperationException {
        // 在当前类查找 Field 和 Method 并注入
        for (Field field : beanClass.getDeclaredFields()) {
            tryInjectProperties(beanDefinition, beanClass, beanInstance, field);
        }
        for (Method method : beanClass.getDeclaredMethods()) {
            tryInjectProperties(beanDefinition, beanClass, beanInstance, method);
        }

        // 去父类查找 Field 和 Method 并注入
        Class<?> superclass = beanClass.getSuperclass();
        if (superclass != null) {
            injectProperties(beanDefinition, superclass, beanInstance);
        }
    }

    /**
     * @param beanDef      需要注入的 BeanDefinition 对象
     * @param clazz        注入的 bean 类型
     * @param beanInstance 注入的 bean 实例
     * @param acc          可访问的对象
     * @return: void
     * @description 注入单个 bean
     * @date 2024/1/21 15:51
     */
    private void tryInjectProperties(BeanDefinition beanDef, Class<?> clazz, Object beanInstance, AccessibleObject acc) throws ReflectiveOperationException {
        Value value = acc.getAnnotation(Value.class);
        Autowired autowired = acc.getAnnotation(Autowired.class);
        if (value == null && autowired == null) {
            return;
        }

        Field field = null;
        Method method = null;
        // 如果是字段则直接注入
        if (acc instanceof Field) {
            Field accField = (Field) acc;
            checkFieldOrMethod(accField);
            accField.setAccessible(true);
            field = accField;
        }
        // 方法使用 setter 注入时必须有且仅有一个参数
        if (acc instanceof Method) {
            Method accMethod = (Method) acc;
            checkFieldOrMethod(accMethod);
            if (accMethod.getParameters().length != 1) {
                throw new BeanDefinitionException(String.format(
                        "Cannot inject a non-setter method %s for bean %s : %s",
                        accMethod.getName(), beanDef.getName(), beanDef.getBeanClass().getName()
                ));
            }
            accMethod.setAccessible(true);
            method = accMethod;
        }

        // 只能使用一种方法进行注入
        String accessibleName;
        if (field != null) {
            accessibleName = field.getName();
        } else {
            assert method != null;
            accessibleName = method.getName();
        }
        Class<?> accessibleType = field != null ? field.getType() : method.getParameterTypes()[0];
        if (value != null && autowired != null) {
            throw new BeanCreationException(String.format(
                    "Cannot specify both @Autowired and @Value when inject %s.%s for bean %s : %s",
                    clazz.getSimpleName(), accessibleName, beanDef.getName(), beanDef.getBeanClass().getName()
            ));
        }

        // @Value 方式注入
        if (value != null) {
            Object property = propertyResolver.getRequiredProperty(value.value(), accessibleType);
            if (field != null) {
                field.set(beanInstance, property);
            }
            if (method != null) {
                method.invoke(beanInstance, property);
            }
        }

        // @Autowired 方式注入
        if (autowired != null) {
            String name = autowired.name();
            boolean required = autowired.value();
            Object depends = name.isEmpty() ? findBean(accessibleType) : findBean(name, accessibleType);
            if (required && depends == null) {
                throw new UnsatisfiedDependencyException(String.format(
                        "Dependency bean not found when inject %s.%s for bean %s : %s",
                        clazz.getSimpleName(), accessibleName, beanDef.getName(), beanDef.getBeanClass().getName()
                ));
            }
            if (depends != null) {
                if (field != null) {
                    field.set(beanInstance, depends);
                }
                if (method != null) {
                    method.invoke(beanInstance, depends);
                }
            }
        }
    }

    /**
     * @param requiredType 要查找的 bean 的类型
     * @return: T
     * @description 根据 type 查找对应的 bean
     * @date 2024/1/22 15:58
     */
    @Nullable
    @SuppressWarnings("unchecked")
    protected <T> T findBean(Class<T> requiredType) {
        BeanDefinition beanDefinition = findBeanDefinition(requiredType);
        if (beanDefinition == null) {
            return null;
        }
        return ((T) beanDefinition.getRequiredInstance());
    }

    /**
     * @param name         要查找的 bean 的名称
     * @param requiredType 要查找的 bean 的类型
     * @return: T
     * @description 根据 name 和 type 查找对应的 bean
     * @date 2024/1/22 15:58
     */
    @Nullable
    @SuppressWarnings("unchecked")
    protected <T> T findBean(String name, Class<T> requiredType) {
        BeanDefinition beanDefinition = findBeanDefinition(name, requiredType);
        if (beanDefinition == null) {
            return null;
        }
        return ((T) beanDefinition.getRequiredInstance());
    }

    /**
     * @param requiredType 要查找的 bean 的类型
     * @return: T
     * @description 根据 type 查找对应的所有 bean
     * @date 2024/1/23 14:36
     */
    @Nullable
    @SuppressWarnings("unchecked")
    protected <T> List<T> findBeans(Class<T> requiredType) {
        return findBeanDefinitions(requiredType).stream()
                .map(beanDefinition -> (T) beanDefinition.getRequiredInstance())
                .collect(Collectors.toList());
    }

    /**
     * @param member 可接受的对象
     * @return: void
     * @description 校验字段或者方法是不能为静态的
     * @date 2024/1/21 15:58
     */
    private void checkFieldOrMethod(Member member) {
        int modifiers = member.getModifiers();
        if (Modifier.isStatic(modifiers)) {
            throw new BeanDefinitionException("Cannot inject static field : " + member.getName());
        }
        if (Modifier.isFinal(modifiers)) {
            if (member instanceof Field) {
                Field field = (Field) member;
                throw new BeanDefinitionException("Cannot inject final field : " + field.getName());
            }
            if (member instanceof Method) {
                Method method = (Method) member;
                throw new BeanDefinitionException("Cannot inject final method : " + method.getName());
            }
        }
    }

    /**
     * @param beanDefinition 需要判断类型的 BeanDefinition 对象
     * @return: boolean
     * @description 判断当前对象是否为 PostProcessor 类型
     * @date 2024/1/22 15:24
     */
    private boolean isPostProcessorBean(BeanDefinition beanDefinition) {
        return BeanPostProcessor.class.isAssignableFrom(beanDefinition.getBeanClass());
    }

    /**
     * @param beanDefinition 需要判断类型的 BeanDefinition 对象
     * @return: boolean
     * @description 判断当前对象是否为 Configuration 类型
     * @date 2024/1/22 15:51
     */
    private boolean isConfigurationBean(BeanDefinition beanDefinition) {
        return ClassUtil.findAllAnnotation(beanDefinition.getBeanClass(), Configuration.class) != null;
    }

    /**
     * @param beanClassNameSet 所有扫描到的 className 集合
     * @return: java.util.Map<java.lang.String, com.jiuxiao.mini.ioc.BeanDefinition>
     * @description 根据扫描到的所有 className 创建与之对应的 BeanDefinition
     * @date 2024/1/20 16:22
     */
    private Map<String, BeanDefinition> createBeanDefinition(Set<String> beanClassNameSet) {
        HashMap<String, BeanDefinition> definitionHashMap = new HashMap<>();
        for (String className : beanClassNameSet) {
            // 尝试加载 class 文件到内存中
            Class<?> clazz;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException cfe) {
                throw new BeanCreationException(cfe);
            }

            // 注解、枚举、接口、记录类型无需创建 BeanDefinition, jdk 8 中需要根据 hashCode()、toString()等方法来判断是否为记录类型
            if (clazz.isAnnotation() || clazz.isEnum() || clazz.isInterface() || isRecordType(clazz)) {
                continue;
            }

            // 是否标注有 @Component 注解
            Component component = ClassUtil.findAllAnnotation(clazz, Component.class);
            if (component != null) {
                int modifiers = clazz.getModifiers();
                if (Modifier.isAbstract(modifiers)) {
                    throw new BeanCreationException(String.format("@Component class %s must be abstract.", clazz.getName()));
                }
                if (Modifier.isPrivate(modifiers)) {
                    throw new BeanCreationException(String.format("@Component class %s must be not private.", clazz.getName()));
                }
            }

            // 正式创建 BeanDefinition
            String beanName = ClassUtil.getBeanName(clazz);
            BeanDefinition beanDefinition = new BeanDefinition(
                    beanName,
                    clazz,
                    getSuitableConstructor(clazz),
                    getOrder(clazz),
                    clazz.isAnnotationPresent(Primary.class),
                    null,
                    null,
                    ClassUtil.findAnnotationMethod(clazz, PostConstruct.class),
                    ClassUtil.findAnnotationMethod(clazz, PreDestroy.class)
            );
            // 将创建的 BeanDefinition 添加到结果集合中
            addBeanDefinition(definitionHashMap, beanDefinition);

            // 如果有 @Configuration 注解，表示使用工厂方法创建 BeanDefinition
            Configuration configuration = ClassUtil.findAllAnnotation(clazz, Configuration.class);
            if (configuration != null) {
                if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                    throw new BeanDefinitionException(String.format("@Configuration class %s cannot be BeanPostProcessor.", clazz.getName()));
                }
                // 扫描 @Configuration 注解所标注的类的工厂方法，创建后添加到结果集合中
                scanFactoryMethods(beanName, clazz, definitionHashMap);
            }
        }
        return definitionHashMap;
    }

    /**
     * @param clazz 要获取构造函数的 class 对象
     * @return: java.lang.reflect.Constructor<?>
     * @description 获取 class 对象的构造函数
     * @date 2024/1/22 14:32
     */
    private Constructor<?> getSuitableConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();
        if (constructors.length == 0) {
            constructors = clazz.getDeclaredConstructors();
            if (constructors.length != 1) {
                throw new BeanDefinitionException("More than one constructor found in class " + clazz.getName());
            }
        }
        if (constructors.length != 1) {
            throw new BeanDefinitionException("More than one public constructor found in class " + clazz.getName());
        }
        return constructors[0];
    }

    /**
     * @param method 要获取优先级的方法对象
     * @return: int
     * @description 根据方法对象获取 bean 对应的优先级
     * @date 2024/1/22 15:03
     */
    private int getOrder(Method method) {
        Order order = method.getAnnotation(Order.class);
        return order == null ? Integer.MAX_VALUE : order.value();
    }

    /**
     * @param clazz 要获取优先级的 class 对象
     * @return: int
     * @description 根据 class 对象获取 bean 对应的优先级
     * @date 2024/1/22 15:03
     */
    private int getOrder(Class<?> clazz) {
        Order order = clazz.getAnnotation(Order.class);
        return order == null ? Integer.MAX_VALUE : order.value();
    }

    /**
     * @param definitionHashMap 最后所有的 BeanDefinition 所在集合
     * @param beanDefinition    当前的 DeanDefinition 对象
     * @return: void
     * @description 校验当前 BeanDefinition 并将其加入结果集
     * @date 2024/1/22 14:55
     */
    private void addBeanDefinition(HashMap<String, BeanDefinition> definitionHashMap, BeanDefinition beanDefinition) {
        if (definitionHashMap.put(beanDefinition.getName(), beanDefinition) != null) {
            throw new BeanDefinitionException("Duplicate bean name : " + beanDefinition.getName());
        }
    }

    /**
     * @param factoryName       工厂 bean 的方法名
     * @param clazz             要加载的 class 对象
     * @param definitionHashMap Bean定义的集合
     * @return: void
     * @description 扫描使用 @Bean 注解标注的工厂方法
     * @date 2024/1/22 14:37
     */
    private void scanFactoryMethods(String factoryName, Class<?> clazz, HashMap<String, BeanDefinition> definitionHashMap) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            Bean bean = method.getAnnotation(Bean.class);
            Class<?> returnType = method.getReturnType();
            // 校验并过滤不符合要求的 @Bean 类型及其不符合的返回值
            if (bean != null) {
                int modifiers = method.getModifiers();
                if (Modifier.isAbstract(modifiers)) {
                    throw new BeanDefinitionException("@Bean method " + clazz.getName() + "." + method.getName() + " must not be abstract");
                }
                if (Modifier.isFinal(modifiers)) {
                    throw new BeanDefinitionException("@Bean method " + clazz.getName() + "." + method.getName() + " must not be final");
                }
                if (Modifier.isPrivate(modifiers)) {
                    throw new BeanDefinitionException("@Bean method " + clazz.getName() + "." + method.getName() + " must not be private");
                }
                if (returnType.isPrimitive()) {
                    throw new BeanDefinitionException("@Bean method " + clazz.getName() + "." + method.getName() + " must not return primitive type");
                }
                if (returnType == void.class || returnType == Void.class) {
                    throw new BeanDefinitionException("@Bean method " + clazz.getName() + "." + method.getName() + " must not return void");
                }
                BeanDefinition beanDefinition = new BeanDefinition(
                        ClassUtil.getBeanName(method),
                        returnType,
                        factoryName,
                        method,
                        getOrder(method),
                        method.isAnnotationPresent(Primary.class),
                        bean.initMethod().isEmpty() ? null : bean.initMethod(),
                        bean.destroyMethod().isEmpty() ? null : bean.destroyMethod(),
                        null, null);
                addBeanDefinition(definitionHashMap, beanDefinition);
            }
        }
    }

    /**
     * @param clazz 要进行扫描的 class 对象
     * @return: java.util.Set<java.lang.String>
     * @description 模拟 @ComponentScan 注解扫描并返回所有的 class 名称
     * @date 2024/1/22 15:07
     */
    private Set<String> scanForAllClassName(Class<?> clazz) {
        ComponentScan componentScan = ClassUtil.findAllAnnotation(clazz, ComponentScan.class);
        final String[] scanPackages = (componentScan == null || componentScan.value().length == 0)
                ? new String[]{clazz.getPackage().getName()}
                : componentScan.value();
        HashSet<String> classNameSet = new HashSet<>();
        boolean scanJar = true;

        // 扫描 @ComponentScan 注解中指定的所有包，包括 jar 包之下的所有 class 文件，添加到最终结果集合中
        for (String scanPackage : scanPackages) {
            ResourceResolver resolver = new ResourceResolver(scanPackage);
            List<String> clazzList = resolver.findClass(resource -> {
                String name = resource.getName();
                if (name.endsWith(".class")) {
                    return name.substring(0, name.length() - 6).replace("\\", ".").replace("/", ".");
                }
                return null;
            }, scanJar);
            classNameSet.addAll(clazzList);
        }

        //查找 @Import 注解所导入的 class 文件，也加入结果集合中
        Import anImport = clazz.getAnnotation(Import.class);
        if (anImport != null) {
            for (Class<?> aClass : anImport.value()) {
                String name = aClass.getName();
                classNameSet.add(name);
            }
        }
        return classNameSet;
    }

    /**
     * @return: void
     * @description 关闭所有的 bean 并执行 destroy 方法
     * @date 2024/1/23 14:21
     */
    @Override
    public void close() {
        beans.values().forEach(beanDefinition -> {
            final Object instance = getProxiedInstance(beanDefinition);
            callInitMethod(instance, beanDefinition.getDestroyMethod(), beanDefinition.getDestroyMethodName());
        });
        beans.clear();
        ApplicationContextUtil.setApplicationContext(null);
    }

    /**
     * @param clazz 要判断的 class 对象
     * @return: boolean
     * @description 判断某个类是否为 JDK 14 之后的 Record 类型
     * @date 2024/1/21 15:24
     */
    private boolean isRecordType(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        return Arrays.stream(methods).anyMatch(method -> {
            String methodName = method.getName();
            return methodName.equals("toString") || methodName.equals("equals") || methodName.equals("hashCode");
        });
    }
}

