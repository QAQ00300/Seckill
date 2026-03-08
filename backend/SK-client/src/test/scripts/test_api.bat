@echo off
title API测试脚本
color 0A

set HOST=localhost
set PORT=8080
set BASE_URL=http://%HOST%:%PORT%

echo ============================================
echo    API测试脚本
echo ============================================
echo.

:: 测试ping接口
echo 测试1: 网关连通性
curl -s %BASE_URL%/gateway/test/ping
echo.
echo.

:: 测试info接口
echo 测试2: 网关信息
curl -s %BASE_URL%/gateway/test/info
echo.
echo.

:: 测试metrics接口
echo 测试3: 获取stage1指标
curl -s %BASE_URL%/gateway/test/metrics/stage1
echo.
echo.

:: 测试慢请求
echo 测试4: 慢请求测试
curl -s %BASE_URL%/gateway/test/slow?delay=1000
echo.
echo.

echo ===== 测试完成 =====
pause