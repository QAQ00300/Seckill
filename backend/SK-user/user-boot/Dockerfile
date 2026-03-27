FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# 安装 tzdata 用于时区设置
RUN apk add --no-cache tzdata

# 复制 JAR 包
# ARG JAR_FILE=target/*.jar
COPY target/*.jar app.jar

# 设置时区
ENV TZ=Asia/Shanghai

# 暴露端口
EXPOSE 8082

# JVM 参数
ENV JAVA_OPTS="-Xmx512m -Xms512m -Duser.timezone=Asia/Shanghai"

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8082/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]