package com.wx.influx.statistics;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author xinquan.huangxq
 */
@ConfigurationProperties(InfluxStatisticsConfigurationProperties.PREFIX)
@Data
public class InfluxStatisticsConfigurationProperties {

    public static final String PREFIX = "influx.statistics";

    /**
     * 开关
     */
    private boolean enabled = true;

    /**
     * influx数据库名称
     */
    private String influxDbName;

    /**
     * 数据保留策略
     */
    private String influxRetentionPolicy;

    /**
     * 连接url
     */
    private String influxHostUrl;

    /**
     * 用户名
     */
    private String influxUsername;

    /**
     * 密码
     */
    private String influxUserPwd;

    /**
     * 统计上送间隔
     */
    private int statisticsSendInterval;

    /**
     * 统计数据上送到influxDB间隔
     */
    private int influxSendInterval;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 机器名称
     */
    private String serverName;
}
