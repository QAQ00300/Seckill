@echo off
title SK-Gateway Service
color 0A

echo ============================================
echo    SK-Gateway 启动脚本 (Windows版)
echo ============================================
echo.

:: 设置变量
set APP_NAME=sk-gateway
set JAR_FILE=..\target\%APP_NAME%.jar
set LOG_DIR=..\logs

:: 检查Java环境
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] Java未安装或未配置环境变量
    pause
    exit /b 1
)
echo [OK] Java环境正常

:: 检查JAR文件
if not exist "%JAR_FILE%" (
    echo [错误] JAR文件不存在: %JAR_FILE%
    echo 请先执行: mvn clean package
    pause
    exit /b 1
)
echo [OK] JAR文件存在

:: 创建日志目录
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

:: 选择阶段
echo.
echo 请选择测试阶段:
echo 1) stage1 - 基础架构
echo 2) stage2 - Redis缓存
echo 3) stage3 - 消息队列
echo.

set /p choice="请输入选择 [1-3]: "

if "%choice%"=="1" set STAGE=stage1&set PORT=8080
if "%choice%"=="2" set STAGE=stage2&set PORT=8081
if "%choice%"=="3" set STAGE=stage3&set PORT=8082

:: 启动服务
echo.
echo 正在启动 %STAGE% 阶段网关服务...
echo.

set JAVA_OPTS=-Xms512m -Xmx1024m
set JAVA_OPTS=%JAVA_OPTS% -Dspring.profiles.active=%STAGE%
set JAVA_OPTS=%JAVA_OPTS% -Dserver.port=%PORT%

start "SK-Gateway" java %JAVA_OPTS% -jar %JAR_FILE%

:: 等待启动
echo 等待服务启动...
timeout /t 10

:: 验证服务
echo.
echo ===== 服务验证 =====

curl -s http://localhost:%PORT%/gateway/test/ping > %LOG_DIR%\ping.log
findstr /C:"ok" %LOG_DIR%\ping.log >nul
if %errorlevel% equ 0 (
    echo [OK] 网关测试通过
) else (
    echo [错误] 网关测试失败
)

echo.
echo ====================
echo 服务已启动!
echo 阶段: %STAGE%
echo 端口: %PORT%
echo 日志: %LOG_DIR%
echo.
echo 访问地址: http://localhost:%PORT%/gateway/test/ping
echo ====================

pause