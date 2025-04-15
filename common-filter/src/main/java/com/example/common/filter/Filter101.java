package com.example.common.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>HTTP Filter。
 * <p>檢測 request 參數中是否包含 "code" 並將其印出
 * 
 * <p># 技巧: 繼承 OncePerRequestFilter: 
 * <br> - 在複雜的 Spring 應用中，特別是使用了轉發 (forward) 或包含 (include) 等操作時，普通的 servelt.Filter 可能會被多次調用
 * <br> - 通過使用請求屬性來標記已處理過的請求，確保同一個請求的過濾邏輯只執行一次。
 */
public class Filter101 extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(Filter101.class);
    
    private final String logPrefix;
    private final boolean enabled;
    
    // 用傳統的方式從建構式帶入參數
    public Filter101(String logPrefix, boolean enabled) {
        this.logPrefix = logPrefix;
        this.enabled = enabled;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) 
                                   throws ServletException, IOException {
        
        if (enabled) {
            // 檢查是否包含 "code" 參數
            String codeValue = request.getParameter("code");
            
            if (codeValue != null && !codeValue.isEmpty()) {
                // 將 code 值印出來
                logger.info("{} - Code parameter found: {}", logPrefix, codeValue);
            } else {
                logger.debug("{} - No code parameter found in request", logPrefix);
            }
        }
        
        // 繼續請求處理鏈
        filterChain.doFilter(request, response);
    }
}
