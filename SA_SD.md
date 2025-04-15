# common-filter 核心模組

[`build.gradle`]

設計原則要求，核心程式不能依賴 spring-boot

```gradle
// 不使用 boot
plugins {
    //id 'org.springframework.boot' version '3.3.10'
}

dependencies {
    // 不使用 starter
    //implementation 'org.springframework.boot:spring-boot-starter-web'

    // 手動添加依賴 Srping、Servlet、self4J、Spring Context
    implementation 'org.springframework:spring-web:6.1.8'
    implementation 'jakarta.servlet:jakarta.servlet-api:6.0.0'
    implementation 'org.slf4j:slf4j-api:2.0.17'
    implementation 'org.springframework:spring-context:6.1.18'
}

//bootJar {
//    enabled = false
//}

// 發佈
publishing {
    publications {
        mavenJava(MavenPublication) {
            groupId = project.group
            artifactId = project.name
            version = project.version

            from components.java

	        versionMapping {
	            usage('java-api') {
	                fromResolutionOf('runtimeClasspath')
	            }
	            usage('java-runtime') {
	                fromResolutionResult()
	            }
	        }

	        pom {
	            name = 'Common Filters'
	            description = 'Common Filters in Example Compony.'
	        }
        }
    }

    // 這裡可以設定 repositories
    repositories {
        mavenLocal() // 這會發布到本機的 Maven 倉庫
    }
```

[`Filter101`]

```java
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
```

核心程式完全沒有依賴 spring-boot !!

[`Filter101`]

```java
public class Filter101 extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(Filter101.class);

    private final String logPrefix;
    private final boolean enabled;

    public Filter101(String logPrefix, boolean enabled) {
        this.logPrefix = logPrefix;
        this.enabled = enabled;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
                                   throws ServletException, IOException {...}
```

這裡用這邊用傳統的方式從建構式帶入參數，無使用 `@ConfigurationProperties`
`@ConfigurationProperties` 是 spring-boot auto-configuation 提供的功能，但前面設計原則要求，核心程式不能依賴 spring-boot，故不使用。

# common-filter-starter 模組

[`build.gradle`]

並非全部，僅列出重點

```gradle
plugins {
    id 'java-library'
    id 'maven-publish'
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-web'
	api 'com.example:common-filter:0.0.1-SNAPSHOT'  // 用 api 引入 Filter !!
}

// 阻止 Gradle 在建構過程中生成可獨立運行的 Spring Boot JAR 檔案。不需包含嵌入式伺服器
bootJar {
    enabled = false
}
// 讓這個專案能成為一個可發布的 jar 包
jar {
    enabled = true
}
```

這裡有個有趣的地方

```gradle
	api 'com.example:common-filter:0.0.1-SNAPSHOT'
```

使用 api 而非 implementation 宣告 common-filter 依賴，common-filter 的相關依賴都會被引入 common-filter-starter 模組內。
如果用的是 implementation，則 common-filter-starter 只會引入 common-filter 本身，但再後面的其他依賴不會被引入 common-filter-starter 模組內。

使用端(my-app)若想使用 Filter101.java，則在 `application.yml` 指定參數:

```yml
cust:
  filter101:
    enabled: true
    urlPatterns:
      - /api/v1/*
      - /api/v2/*
    order: 10
    logPrefix: THIS_IS_FILTER_101
```

[`Filter101Properties.java`]

```java
/**
 * Filter101 的配置屬性類, 對應到 application.yml
 */
@ConfigurationProperties(prefix = "cust.filter101")
public class Filter101Properties {
    private boolean enabled = true;
    private String[] urlPatterns = {"/*"};
    private int order = 0;
    private String logPrefix = "[this-is-default-prefix-filter101]";
    ...
}
```

[`FilterAutoConfiguration.java`]

```java
@Configuration
@EnableConfigurationProperties(Filter101Properties.class)
public class FilterAutoConfiguration {
	private Filter101Properties filter101Properties;

	public FilterAutoConfiguration(Filter101Properties filter101Properties) {
		this.filter101Properties = filter101Properties;
	}

    ...
}
```

用 `@EnableConfigurationProperties` 引入 yml 設定內容

[`FilterAutoConfiguration.java`]

```java
@Configuration
@EnableConfigurationProperties(Filter101Properties.class)
public class FilterAutoConfiguration {
    ...

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnWebApplication
	@ConditionalOnProperty(name = "cust.filter101.enabled", havingValue = "true", matchIfMissing = false)
	public FilterRegistrationBean<Filter101> filter101(){...}
```

進行 "若滿足什麼條件，則自動配置"。

auto-configuration 條件通常是:

1. bean 存在與否
2. class 存在與否
3. application.yml 設定檔

這邊還有一個特別的是 ‵@ConditionalOnWebApplication`，必須是 WebApplication 才能創建 bean

[`FilterAutoConfiguration.java`]

```java
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
```

用 `FilterRegistrationBean` 註冊 Filter 實作，比起傳統的 Servlet Filter 更有彈性。

[`/common-filter-starter/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`]

```txt
com.example.common.filter.starter.FilterAutoConfiguration
```

這是 Spring Boot 3.x 用於自動發現配置類的機制，將所需的 \*AutoConfiguration 類別，一行寫一個，Spring boot 就會逐一去找實作並進行自動配置了。

# my-app

[`application.yml`]

```yml
cust:
  filter101:
    enabled: true
    urlPatterns:
      - /api/v1/*
      - /api/v2/*
    order: 10
    logPrefix: THIS_IS_FILTER_101
```

[`build.gradle`]

```
repositories {
    mavenLocal() 	// 首先檢查本機 Maven 倉庫
    mavenCentral() 	// 然後檢查 Maven Central
}
dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-web'
	implementation 'com.example:common-filter-starter:0.0.1-SNAPSHOT'
}
```

my-app 引入 common-filter-starter 即可，運行後將自動裝配，無需重複撰寫 @Configuration、@Bean 等配置文件
