# 2018-03-26  
&emsp;尚存一些设计缺陷和TODO，此repo只在已有基础上修复BUG，后续更新和new feture等参见https://github.com/nameof/ex-session-sso

----------------------

# RedisSSO
&emsp;默认基于Redis封装自定义HttpSession（不仅局限于redis，提供了接口，可以扩展CacheDao将Session数据托管到各种缓存，memcached、mongodb、ehcache，好像项目名没取好= =），实现全局Session共享，提取出Session的好处是显而易见的，会话数据不再是一个黑盒，我们可以对Session进行监控和自由访问，例如集成WebSocket，实现跨域单点登录，本repo还提供支持android客户端扫码登陆，并使用redis模拟消息队列进行注销消息的发送。   

&emsp;实现思路与[spring-session](http://projects.spring.io/spring-session/)一致，也存在同样的缺陷，例如丧失了HttpSession相关Listener的处理能力。

----------------------

# 使用
&emsp;启动cas服务器端前需开启连接redis，默认是连接本地的6379端口的redis  。

----------------------

# 说明
&emsp;整体逻辑是cas认证中心作为全局登录和注销控制中心，通过servlet拦截器将默认的HttpSession替换为缓存实现（默认使用redis）的Session，达到会话统一控制。

&emsp;附带的一个简易客户端站点的实现是仍然使用局部Session，登陆时通过重定向到cas进行统一登录，登录成功cas则携带token返回到客户端站点，客户端确认token后，cas将用户会话数据发送给客户端站点，客户端站点再将其存储到局部会话中；  

&emsp;注销过程类似，定向到cas注销，完成后cas销毁全局会话，并通过redis消息队列通知各个客户端站点销毁局部会话。  

&emsp;整个认证过程可以看作实现了简易的[CAS协议](https://apereo.github.io/cas/4.2.x/protocol/CAS-Protocol.html)。

&emsp;如果要实现客户端和服务器站点真正的所有Session会话数据全局统一共享和管理，那么可以将cas的缓存Session涉及到的API打包到客户端，客户端采用同样的方式去访问会话数据。此处没有提供实现，大家可以自己尝试，或者后面有时间会提供实现案例=_= 。
  
&emsp;扫码登录采用轮询实现，可延伸方案有websocket。


----------------------
# TODO
- Session的相关Listener的实现
- Servlet API中LastAccessedTime的实现
- 服务器客户端的安全通信
- token等各种安全机制   
- 代码DEBUG和设计优化   
- 功能扩充
