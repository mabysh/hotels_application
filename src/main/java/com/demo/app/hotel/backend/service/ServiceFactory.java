package com.demo.app.hotel.backend.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ServiceFactory implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static ApplicationServiceImpl getApplicationServiceImpl() {
        ApplicationServiceImpl service = applicationContext.getBean("applicationService",
                ApplicationServiceImpl.class);
        return service;
    }
}
