package com.example.ioccontainer;

import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MyIocContainer {

    private final Map<String, Object> beans = new HashMap<>();
    private final Properties properties = new Properties();

    public static void main(String[] args) throws IOException {
        MyIocContainer iocContainer = new MyIocContainer();
        iocContainer.init();
        OrderService orderService = (OrderService) iocContainer.getBean("orderService");
        orderService.createOrder();
    }

    // 启动容器，从 beans.properties 配置文件中加载 bean 的定义
    public void init() throws IOException {
        properties.load(MyIocContainer.class.getResourceAsStream("/beans.properties"));
    }

    // 从 IoC 容器中获取一个 bean
    // 实质是工厂模式
    public Object getBean(String beanName) {
        if (beans.get(beanName) != null)
            return beans.get(beanName);

        try {
            Class<?> aClass = Class.forName(properties.getProperty(beanName));
            Object beanInstance = aClass.getConstructor().newInstance();
            // DI
            dependencyInject(aClass, beanInstance);
            return beanInstance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // DI
    private void dependencyInject(Class<?> aClass, Object beanInstance) throws Exception {
        Field[] declaredFields = aClass.getDeclaredFields();

        List<Field> fieldsToBeAutowired = Stream.of(declaredFields)
                .filter(field -> field.getAnnotation(Autowired.class) != null)
                .collect(Collectors.toList());

        for (Field field : fieldsToBeAutowired) {
            String fieldName = field.getGenericType().getTypeName();
            Object dependencyBeanInstance = beans.get(fieldName);
            if (dependencyBeanInstance == null) {
                Class<?> fieldClass = Class.forName(fieldName);
                dependencyBeanInstance = fieldClass.getConstructor().newInstance();
                dependencyInject(fieldClass, dependencyBeanInstance);
            }
            field.setAccessible(true);
            field.set(beanInstance, dependencyBeanInstance);
            beans.put(fieldName, dependencyBeanInstance);
        }
    }
}
