#模块结构
#实体模块结构
- AO：用于存放dto等传输对象
    - REQ：请求体
    - RES：返回结果
    - DTO：数据传输工具类（一般用于外部传输）
- API：用于存放api接口
- BIZ：用于存放业务代码
- BO：用于存放eo实体类
    - EO：数据库数据实体类
- CONSTANT：用于存放枚举类
- BOOT：微服务启动模块

#core模块结构
- sk-core-seckill: 秒杀核心流程控制
- sk-core-stock: 库存管理和扣减逻辑
- sk-core-cache: 缓存处理，利用 Redis 优化性能
- sk-core-mq: 消息队列集成，异步处理秒杀请求