influx-statistics-spring-boot-starter
======================================================================

支持jdk版本为1.7或者1.7+

### 如何使用influx统计计数

* 添加依赖:

```xml
   <dependency>
     <groupId>com.github.wangxuangege</groupId>
     <artifactId>influx-statistics-spring-boot-starter</artifactId>
     <version>1.0</version>
   </dependency>
```

* 在application.properties添加统计的相关配置信息，样例配置如下:

```properties
influx.statistics.enabled=true

influx.statistics.appName=${appName}
influx.statistics.serverName=${ServerName}

influx.statistics.influxHostUrl=http://${IP}:${Port}
influx.statistics.influxDbName=${InfluxDb}
influx.statistics.influxUsername=${UserName}
influx.statistics.influxUserPwd=${UserPwd}
influx.statistics.influxRetentionPolicy=${RetentionPolicy}

influx.statistics.influxSendInterval=${InfluxSendInterval}
influx.statistics.statisticsSendInterval=${StatisticsSendInterval}
```

注：这个配置中必须都明文配置，其中：1）enabled为开关配置；2）appName和serverName为统计分组；3）influxHostUrl、influxDbName、influxUsername、influxUserPwd、influxRetentionPolicy为influxDb的配置信息；4）influxSendInterval、statisticsSendInterval分别决定统计精度和统计上送延迟控制。

* 接下来在Spring Boot Application的上引用统计服务类：

```java
@Autowired
private InfluxStatistics influxStatistics;
```

* 接下来在Spring Boot Application的上需要埋点统计的地方添加相关埋点统计逻辑：
```java
long startTime = System.currentTimeMillis();

// 执行逻辑
......

// category和action记录操作类型，result记录操作结果，count用来记录操作计数，最后一个参数用来记录操作耗时
influxStatistics.accumulate(${category}, ${action}, ${result}, ${count}, System.currentTimeMillis() - startTime);
```

### influx统计数据（可以使用grafana图标组件展示）

* influx记录在statistics中，记录行数据格式如下：

| 字段   |      描述      |
|----------|-------------|
| server | 机器名称 |
| event | 由多个字段拼接而成，包括appName、category、action、result |
| app | 应用名称 |
| category | 类别 |
| action | 动作行为 |
| result | 结果 |
| count | 计数 |
| min | 耗时最小值，某一个上送influx周期内记录耗时的最小值 |
| max | 耗时最大值，某一个上送influx周期内记录耗时的最大值 |
| total | 总共计数，某一个上送influx周期内记录总数 |
| average | 耗时平均值，某一个上送influx周期内记录耗时的平均值 |