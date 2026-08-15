package com.dentalclinic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC Configuration for Static Resources.
 * Ensures CSS, JS, and image assets are served reliably across JSP and REST views.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/", "/css/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/", "/js/");
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/", "/images/");
    }
}
