@echo off

@rem 用哪個 JDK 來啟動 Gradle 本身
SET JAVA_HOME="D:/Applications/Java/java-17-openjdk-17.0.14.0.7-1.win.jdk.x86_64"


@rem ***沒有 GRADLE_HOME 設置----------
@rem 因為我們用的是 gradlew, 意思就是要各專案有自己的 gradle 版本做構建, 
@rem 使用哪個版本的 gradle, 在 專案路徑\gradle\wrapper\gradle-wrapper.properties 裡面的 distributionUrl 指定


@rem ***告訴 Gradle 將儲存庫設置在哪裡--------

@rem 方案一、使用全域的儲存庫(本機開發這樣就夠了)
@rem SET GRADLE_USER_HOME=D:/.gradle-user-home

@rem 方案二、專案有自己的儲存庫(CI/CD時比較需要, 不會互相干擾)
SET GRADLE_USER_HOME=./.gradle-user-home

call gradlew.bat %*