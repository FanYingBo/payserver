package com.last.pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
/**
 *  pay server 
 * @author Administrator
 *
 */
@ComponentScan(basePackages = {"com.last.model.*","com.last.redis.*","com.last.pay.*"})
@SpringBootApplication
@EnableScheduling
public class PayApplication extends SpringBootServletInitializer{
	
	public static void main(String[] args) {
		SpringApplication.run(PayApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(PayApplication.class);
	}

	@Bean
    public FreeMarkerViewResolver freeMarkerViewResolver() {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
        resolver.setPrefix("");
        resolver.setSuffix(".ftl");
        resolver.setContentType("text/html;charset=UTF-8");
        resolver.setRequestContextAttribute("rc");
        return resolver;       
 
     }
}
