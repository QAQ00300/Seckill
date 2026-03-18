#!/bin/bash

# 数据库初始化脚本
# 用途：在 Docker 容器启动时自动执行 SQL 脚本

set -e

echo "🚀 开始初始化数据库..."

# 等待 MySQL 完全启动
echo "⏳ 等待 MySQL 服务就绪..."
while ! mysqladmin ping -h"localhost" -uroot -p"${MYSQL_ROOT_PASSWORD}" --silent; do
    sleep 2
done

echo "✅ MySQL 服务已就绪"

# 执行 SQL 脚本
SQL_DIR="/docker-entrypoint-initdb.d"

if [ -d "$SQL_DIR" ]; then
    echo "📂 发现 SQL 脚本目录：$SQL_DIR"

    # 按顺序执行阶段 1、阶段 2、阶段 3 的 SQL
    for sql_file in $(ls -1 "$SQL_DIR"/*.sql 2>/dev/null | sort); do
        if [ -f "$sql_file" ]; then
            echo "📝 执行 SQL 文件：$(basename $sql_file)"
            mysql -u root -p"${MYSQL_ROOT_PASSWORD}" "${MYSQL_DATABASE}" < "$sql_file"
            echo "✅ 执行完成：$(basename $sql_file)"
        fi
    done
else
    echo "⚠️ 未找到 SQL 脚本目录"
fi

echo "🎉 数据库初始化完成！"