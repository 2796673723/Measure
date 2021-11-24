package com.scalelable.demo.config;

import com.scalelable.demo.interceptor.PersonInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    private final PersonInterceptor personInterceptor;

    @Autowired
    public InterceptorConfig(PersonInterceptor personInterceptor) {
        this.personInterceptor = personInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(personInterceptor)
                .excludePathPatterns("/client/login")
                .addPathPatterns("/client/**");

    }
}
