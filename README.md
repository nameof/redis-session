# RedisSSO
基于Redis封装自定义HttpSession，实现跨域单点登录和Session共享，支持android客户端扫码登陆，并使用redis模拟消息队列进行注销消息的发送。

----------------------

# 使用
启动cas服务器端前需开启连接redis，默认是连接本地的6379端口的redis

----------------------
# TODO
- 服务器客户端的安全通信
- token等各种安全机制   
- 代码DEBUG和设计优化   
- 功能扩充
