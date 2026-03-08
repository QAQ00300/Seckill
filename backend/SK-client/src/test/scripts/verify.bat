@echo off
title 服务验证脚本
color 0A

echo ============================================
echo    服务验证脚本
echo ============================================
echo.

:: 检查端口
echo 检查端口 8080 是否监听...
netstat -ano | findstr :8080 >nul
if %errorlevel% equ 0 (
    echo [OK] 端口 8080 已监听
) else (
    echo [错误] 端口 8080 未监听
)

:: 检查网关服务
echo.
echo 检查网关服务...
curl -s http://localhost:8080/actuator/health >nul
if %errorlevel% equ 0 (
    echo [OK] 网关服务正常
) else (
    echo [错误] 网关服务异常
)

:: 检查测试接口
echo.
echo 检查测试接口...
curl -s http://localhost:8080/gateway/test/ping >nul
if %errorlevel% equ 0 (
    echo [OK] 测试接口正常
) else (
    echo [错误] 测试接口异常
)

echo.
echo ===== 验证完成 =====
pause