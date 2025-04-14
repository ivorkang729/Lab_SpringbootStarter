package com.example.common.filter.starter;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.common.filter.Filter101;

@Configuration
@EnableConfigurationProperties(Filter101Properties.class)
public class FilterAutoConfiguration {
	
	private Filter101Properties filter101Properties;
	
	public FilterAutoConfiguration(Filter101Properties filter101Properties) {
		this.filter101Properties = filter101Properties;
	}
	
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnWebApplication
	@ConditionalOnProperty(name = "cust.filter101.enabled", havingValue = "true", matchIfMissing = false)
	public FilterRegistrationBean<Filter101> filter101(){
		// 創建 Filter 實例
		var filter = new Filter101(
				filter101Properties.getLogPrefix()
				, filter101Properties.isEnabled()
		);
		
		// 註冊 Filter
		FilterRegistrationBean<Filter101> registration = new FilterRegistrationBean<>(filter);
		
		// 設置 Filter 應用於 URL 
		//registration.addUrlPatterns("/api/critical/*", "/public/*", "/auth/*");
		Arrays.stream(filter101Properties.getUrlPatterns()).forEach((String urlPattern) -> registration.addUrlPatterns(urlPattern));
		
		// 設置 Filter 順序 (數字越小越先執行)
		registration.setOrder(filter101Properties.getOrder());
		
		return registration;
	}
	
}
