package com.example.common.filter.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Filter101 的配置屬性類, 對應到 application.yml
 */
@ConfigurationProperties(prefix = "cust.filter101")
public class Filter101Properties {
    
    /**
     * 是否啟用過濾器
     */
    private boolean enabled = true;
    
    /**
     * 日誌前綴，用於識別日誌來源
     */
    private String logPrefix = "[this-is-default-prefix-filter101]";
    
    /**
     * 什麼樣的 URL 會套用這個 Filter
     * */
    private String[] urlPatterns = {"/*"};
    
    /**
     * Filter 的順序，數字越小優先級越高
     */
    private int order = 0;
    
    

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getLogPrefix() {
        return logPrefix;
    }

    public void setLogPrefix(String logPrefix) {
        this.logPrefix = logPrefix;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

	public String[] getUrlPatterns() {
		return urlPatterns;
	}

	public void setUrlPatterns(String[] urlPatterns) {
		this.urlPatterns = urlPatterns;
	}
    
}
