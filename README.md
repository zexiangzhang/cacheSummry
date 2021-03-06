# 常见的缓存策略和缓存算法

## 一、缓存

    一般而言，现在互联网应(网站或App)的整体流程，可以概括为一句话：

    用户请求从界面(浏览器或App界面)到网络转发、应用服务再到存储(数据库或文件系统),然后返回到界面呈现内容

    随着互联网的普及，内容信息越来越复杂，用户数和访问量越来越大，我们的应用需要支撑更多的并发量，同时我们的应用服务器和数据库服务器所做的计算也越来越多
    但是往往我们的应用服务器资源是有限的，且技术变革是缓慢的，数据库每秒能接受的请求次数也是有限的（或者文件的读写也是有限的），如何能够有效利用有限的资源来提供尽可能大的吞吐量？
    一个有效的办法就是引入缓存，打破标准流程，每个环节中请求可以从缓存中直接获取目标数据并返回

目的就是：***减少计算量，提高响应速度，用有限的资源服务更多的用户***

    

## 二、缓存的一些场景

2.1 热点数据

    用户经常访问的一些数据，包括但不仅限于图片、视频、html页面等

2.2 排行榜
    
    在小规模数据的情况下，使用Mysql实现排行榜没有多少问题，但是一旦数据量上去了，那么持续的进行Mysql读写将会成为瓶颈
    因此可以使用redis提供的zset数据结构来更便捷的实现排行榜

    例如根据点赞量做人员排行榜、根据访问次数做商家排行榜

###### [代码示例](https://github.com/zexiangzhang/cacheSummry/blob/master/rankList/src/main/java/com/zzx/ranklist/user/service/impl/UserServiceImpl.java)

2.3 计数器/限数器

    由于redis是单线程的，每次都必须前一个指令执行完，再执行下一个指令
    这样就保证不会同时执行多条指令，也就是说不会出现并发问题

    计数器的场景例如统计点赞数量
    限数器的场景例如限制同一ip的访问次数

2.4 数据交集

    利用Redis提供的Set数据结构的求交集操作sinter可以更加便捷地求两个Set集合的交集

    例如某一场景为查询A用户和B用户的共同好友，在用户数量巨大的情况下，使用数据库的连表查询将造成性能的开销很多

2.5 session共享

    Session是用来记录是用户是谁
    当在应用使用集群方式部署的时候，我们需要一个统一管理session的地方，可以使用数据库来记录session
    但是这时对数据库的性能要求较高，此外session通常是具有时效性的，这段逻辑我们需要在代码中实现
    如果使用Redis来共享session，那么不会出现这样的问题

2.6 消息队列

    redis提供发布/订阅的功能，但这只是一个简单的消息系统，只能用在一些不需要高可靠的场景下

## 三、什么时候进行缓存

    大部分缓存算法使用预取策略来提前将部分数据放入缓存,加大缓存命中率
    通过记录、分析以往的数据请求模式来预测将来可能被请求到的数据段,将访问可能性大的数据段放入缓存

## 四、缓存的数据分割

***一般需要对缓存中数据做数据分割操作的场景在一些大文件的高频次访问场景下，比如一些影片文件等***

4.1 首部缓存

    首部缓存将影片文件开始的一部分放入缓存以减小点播用户的启动延迟,对于影片文件其他部分的访问需要直接读取磁盘

4.2 分块缓存

    分块缓存通过将影片文件切分成小块,以块为单位进行缓存操作,分块缓存分为定长分块与变长分块
    
    定长分块将文件切分为大小相同的块

    变长分块,变长算法是基于影片文件越靠后的部分被访问的概率越低的推断,将文件按照首尾位置分块,各块大小按指数递增

4.3 隐患及解决方案

    影片文件会存在一些“热点片段”而这些热点片段并不均处于影片首部

    同一影片内“热点片段”的热度会随着时间不断改变,不同影片的热度也随时间不断变化

    因此需要设计良好的算法自适应影片热点的不同位置与变化

## 五、缓存策略及算法

5.1 基于访问时间

    按各缓存项的被访问时间来组织缓存队列,决定替换对象

    如LRU算法

###### [代码示例](https://github.com/zexiangzhang/cacheSummry/blob/master/algorithm/src/BaseOnVisitTime/)

5.2 基于访问频率

    用缓存项的被访问频率来组织缓存

    如LFU算法

###### [代码示例](https://github.com/zexiangzhang/cacheSummry/blob/master/algorithm/src/BaseOnVisitFrequency/LFU.java)

5.3 访问时间与频率兼顾

    通过兼顾访问时间与频率,使得在数据访问模式变化时缓存策略仍有较好性能

    多数此类算法具有一个可调或自适应参数,通过该参数的调节使缓存策略在基于访问时间与频率间取得一定平衡

    如FBR、LRFU算法

###### [代码示例](https://github.com/zexiangzhang/cacheSummry/blob/master/algorithm/src/VisitTimeAndFrequency/LRFU.java)

5.4 基于访问模式

    某些应用有较明确的的数据访问特点,进而产生与其相适应的缓存策略

    如专为VoD(视频点播)系统设计的A&L缓存策略,同时适应随机、顺序两种访问模式的SARC策略