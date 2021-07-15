/*
 * Copyright (c) 2021 InTroubleDE.
 * All rights reserved.
 */

package me.lucacw.smartcaptcha.spring;

import lombok.Getter;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.beans.Introspector;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
public final class SpringInitializer {

    private static final Map<Class<?>, Object> KNOW_SERVICES = new HashMap<>();

    private static AutowireCapableBeanFactory beanFactory;
    private static BeanDefinitionRegistry beanDefinitionRegistry;
    @Getter
    private static ApplicationContext context;

    public SpringInitializer(final AnnotationConfigApplicationContext context) {
        setupContext(context);
        backwardsRegisterServices();
    }

    public static void registerServicePreLoad(Class<?> clazz, Object o) {
        KNOW_SERVICES.put(clazz, o);
    }

    public static void setupContext(ApplicationContext context) {
        SpringInitializer.context = context;
        beanFactory = context.getAutowireCapableBeanFactory();
        beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;
    }

    public static void setupContext(final ClassLoader loader) {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.setClassLoader(loader);
        context.register(SpringInitializer.class);
        context.scan("me.lucacw");
        context.refresh();
    }

    public static void backwardsRegisterServices() {
        KNOW_SERVICES.forEach((clazz, o) -> {
            final String beanName = getBeanName(clazz);
            if (!beanFactory.containsBean(beanName)) {
                registerProvider(clazz, o);
            } else {
                System.out.println("Skipping duplicate bean creation " + beanName + " " + clazz + " " + o);
            }
        });
    }

    public static void registerProvider(final Class<?> clazz, final Object o) {
        if (beanFactory == null) {
            return;
        }
        final GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(clazz);
        beanDefinition.setPrimary(true);
        beanDefinition.setAutowireCandidate(true);
        beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
        beanDefinition.setBeanClassName(clazz.getName());
        beanDefinition.setInstanceSupplier(() -> o);
        final String beanName = getBeanName(clazz);
        beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
        beanFactory.autowireBean(clazz);
    }

    public static String getBeanName(final Class<?> clazz) {
        return Introspector.decapitalize(clazz.getSimpleName());
    }

    public static <T> T getBean(final Class<T> clazz) {
        return context.getBean(clazz);
    }
}