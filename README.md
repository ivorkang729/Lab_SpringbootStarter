# Requirement

1. 開發一支 Filter，功能請自行訂定
2. 公司有 N 個應用，有些是 Spring 應用，有些是 Spring Boot 應用，這 N 個應用都需要這支 Filter 所提供的功能
3. 將這支 Filter 做成共用元件，以 Jar 的形式發佈在 Maven Repository，Jar 的 GAV 為 'com.example:common-filter:0.0.1'
4. 公司的 N 個應用，引用 common-filter 共用元件，就能獲得 Filter 功能
5. 若應用為 Spring 應用，引用此共用元件時，需自行撰寫裝配設定檔 (xml-based config 或 java-based config)
6. 提供 Spring Boot Starter 元件:
   - 對於 common-filter ，提供 Spring Boot Starter 的自動裝配元件，以 Jar 形式發佈在 Maven Repository，Jar 的 GAV 為 'com.example:common-filter-starter:0.0.1'
   - Spring Boot Auto-configuration 的職責：能 "依據條件，自動進行 @Bean @Configuration"
   - 因此 Spring Boot 應用，只要引入 common-filter-starter，就能獲得一致的裝配邏輯，節省 @Bean @Configuration 的工作，避免各系統存在重複的配置程式碼
7. 區分 Spring 和 Spring Boot:
   - 公司有 N 個應用，有些是 Spring 應用，有些是 Spring Boot 應用。
   - common-filter 可以依賴 Spring API 基礎功能[1][2][3][4]
   - 並非所有應用都是 Spring Boot 應用，因此 common-filter 不應依賴 Spring Boot API，以免間接引入 Spring Boot Auto-Config 高機率與現有配置衝突窘境(例如相同的依賴 重複引入)
8. 公司的 Spring boot 應用，只要引入 common-filter-starter，就能自動帶出 common-filter (無需特別引入 common-filter)

註[1] Spring Framework 提供功能:
Context 核心容器、DI 依賴注入、AOP 等基礎功能

- @Controller, @RestController, @Service, @Repository, @Component
- @RequestMapping, @GetMapping, @PostMapping, @PutMapping, @DeleteMapping
- @PathVariable, @RequestParam, @RequestBody, @ResponseBody
- @Autowired, @Qualifier
- @Value, @PropertySource
- @Bean, @Configuration, @ComponentScan
- @Profile
- @Transactional
- @Async, @EnableAsync
- @EnableWebMvc
- @ControllerAdvice, @ExceptionHandler
- @ResponseStatus
- @Valid, @Validated
- @Lazy
- @Scheduled, @EnableScheduling

註[2] spring-boot 提供功能:

- 運行的 "環境"

註[3] spring-boot-autoconfigure 提供功能:

- @EnableAutoConfiguration - 啟用自動配置的核心註解
- @ConditionalOnBean, @ConditionalOnMissingBean
- @ConditionalOnClass, @ConditionalOnMissingClass
- @ConditionalOnProperty
- @AutoConfigureAfter, @AutoConfigureBefore
- @SpringBootApplication (组合了 @EnableAutoConfiguration, @Configuration, @ComponentScan)
- @ConfigurationProperties

註[4] Spring Boot Starter:

- 不提供任何 Annotation
- 是一群 Bean Configurations，告訴 Spring "在特定條件下自動創建這些 Bean"

# 專案結構

## common-filter 核心模組
common-filter: 共用的自定義 Filter 的核心實現  
```
common-filter
|---build.gradle        <== 依賴設定
|---gradle.properties   <== 指定 Gradle 的 file-encoding=UTF-8
|---gradlew.bat
|---run-gradlew.bat     <== 指定 JAVA_HOME、GRADLE_USER_HOME
|---settings.gradle     <== 指定 rootProject.name
|
+---gradle
|---\---wrapper
|-----------gradle-wrapper.jar
|-----------gradle-wrapper.properties  <== 指定用哪個版本的 Gradle
|
\---src
----+---main
----|---+---java
----|---|---\---com
----|---|-------\---example
----|---|-----------\---common
----|---|---------------\---filter
----|---|-----------------------Filter101.java  <== 共用的自定義 Filter
```

# common-filter-starter 模組
common-filter-starter: 依賴於 common-filter，提供自動配置功能  
```
common-filter-starter
|---build.gradle
|---gradle.properties
|---gradlew.bat
|---run-gradlew.bat
|---settings.gradle
|
+---gradle
|---\---wrapper
|-----------gradle-wrapper.jar
|-----------gradle-wrapper.properties
|
\---src
----+---main
----|---+---java
----|---|---\---com
----|---|-------\---example
----|---|-----------\---common
----|---|---------------\---filter
----|---|-------------------\---starter
----|---|---------------------------Filter101Properties.java        <== 對應到 application.yml
----|---|---------------------------FilterAutoConfiguration.java    <== 自行開發的自動裝配類
----|---|
----|---\---resources
----|-------+---META-INF
----|-------|---\---spring
----|-------|-----------org.springframework.boot.autoconfigure.AutoConfiguration.imports   <== Spring Boot 3.x 用於自動發現配置類的機制
```

# my-app 應用
Spring Boot Based 應用，引入 common-filter-starter、設定好 application.yml 就能獲得 Filter 所提供的功能  
```
my-app
|---build.gradle
|---gradle.properties
|---gradlew.bat
|---run-gradlew.bat
|---settings.gradle
|
+---gradle
|---\---wrapper
|-----------gradle-wrapper.jar
|-----------gradle-wrapper.properties
|
\---src
----+---main
----|---+---java
----|---|---\---com
----|---|-------\---example
----|---|-----------\---app
----|---|-------------------ApplicationContext.java
----|---|-------------------HelloController.java        <== HTTP GET /api/v1/hello、/api/v2/hello
----|---|
----|---\---resources
----|-------|---application.yml
```

# 如何運行

cd <專案資料夾>  
cd D:\Side_Project\Lab_SpringbootStarter

- Gradle 編譯打包 common-filter.jar  
  cd .\common-filter; .\run-gradlew.bat clean build publish; cd ..

- Gradle 編譯打包 common-filter-starter.jar  
  cd .\common-filter-starter; .\run-gradlew.bat clean build publish; cd ..

- Gradle 編譯打包 my-app.jar  
  cd .\my-app; .\run-gradlew.bat clean build; cd ..

- 檢查 Local Maven Repositoy 裡面的 jars  
  在 C:\Users\{User-Name}\.m2\repository\com\example\  應能看到兩個目錄：  
   .\common-filter\  
   .\common-filter-starter\  

- 壓出 Docker Image  
  cd .\my-app\; docker image build -t lab-springboot-starter:latest .;cd ..

- 檢查目前的容器  
  docker container --help  
  docker container ls -a  
  docker container rename lab-springboot-starter lab-springboot-starter-xxx  

- 檢查目前的 Images  
  docker image ls  
  docker image rm 5eed9667e35e 1de6465dd550  

- 建立 & 啟動 Container  
  docker run -it -p 8080:8080 --name lab-springboot-starter lab-springboot-starter:latest  

- 重新啟動 Container  
  docker container start -i lab-springboot-starter  

- Postman 呼叫 API:  
  http://host.docker.internal:8080/api/v1/hello  
  http://host.docker.internal:8080/api/v2/hello  

# my-app Dependency Tree

```
productionRuntimeClasspath
+----org.springframework.boot:spring-boot-starter-web-->-3.3.10
|----+----org.springframework.boot:spring-boot-starter:3.3.10   <== my-app 具備 spring-boot 環境
|----|----+----org.springframework.boot:spring-boot:3.3.10
|----|----|----+----org.springframework:spring-core:6.1.18
|----|----|----\----org.springframework:spring-context:6.1.18
|----|----|---------+----org.springframework:spring-aop:6.1.18
|----|----|---------|----+----org.springframework:spring-beans:6.1.18
|----|----|---------+----org.springframework:spring-expression:6.1.18
|----|----+----org.springframework.boot:spring-boot-autoconfigure:3.3.10
|----+----org.springframework.boot:spring-boot-starter-json:3.3.10
|----+----org.springframework.boot:spring-boot-starter-tomcat:3.3.10
|----+----org.springframework:spring-web:6.1.18-(*)
|----\----org.springframework:spring-webmvc:6.1.18
\----com.example:common-filter-starter:0.0.1-SNAPSHOT	 <== my-app 依賴 common-filter-starter
-----\----com.example:common-filter:0.0.1-SNAPSHOT			<== common-filter-starter 依賴 common-filter
----------+----jakarta.servlet:jakarta.servlet-api:6.0.0	   <== common-filter 不依賴 spring-boot
----------+----org.slf4j:slf4j-api:2.0.17
```
