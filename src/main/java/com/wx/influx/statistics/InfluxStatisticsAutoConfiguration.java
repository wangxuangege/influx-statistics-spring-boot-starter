package com.wx.influx.statistics;

import com.wx.influx.statistics.core.InfluxStatistics;
import com.wx.influx.statistics.core.InfluxStatisticsService;
import com.wx.influx.statistics.core.StatisticsService;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author xinquan.huangxq
 */
@Configuration
@EnableConfigurationProperties(InfluxStatisticsConfigurationProperties.class)
@ConditionalOnProperty(prefix = InfluxStatisticsConfigurationProperties.PREFIX, name = "enabled", matchIfMissing = true)
public class InfluxStatisticsAutoConfiguration {

    private static final String INFLUX_STATISTICS_SERVICE_NAME = "influxStatisticsService";

    private static final String STATISTICS_NAME = "influxStatistics";

    @Autowired
    private ApplicationContext applicationContext;

    @Bean(name = INFLUX_STATISTICS_SERVICE_NAME)
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = InfluxStatisticsConfigurationProperties.PREFIX, name = {"influxHostUrl", "influxUsername", "influxUserPwd", "influxDbName", "influxRetentionPolicy", "influxSendInterval"})
    public StatisticsService influxStatisticsService(InfluxStatisticsConfigurationProperties influxStatisticsConfigurationProperties) {
        InfluxDB influxDB = InfluxDBFactory.connect(influxStatisticsConfigurationProperties.getInfluxHostUrl(), influxStatisticsConfigurationProperties.getInfluxUsername(), influxStatisticsConfigurationProperties.getInfluxUserPwd());
        return new InfluxStatisticsService(influxDB, influxStatisticsConfigurationProperties.getInfluxDbName(), influxStatisticsConfigurationProperties.getInfluxRetentionPolicy(), influxStatisticsConfigurationProperties.getInfluxSendInterval());
    }

    @Bean(name = STATISTICS_NAME)
    @ConditionalOnMissingBean
    @ConditionalOnBean(name = INFLUX_STATISTICS_SERVICE_NAME)
    @ConditionalOnProperty(prefix = InfluxStatisticsConfigurationProperties.PREFIX, name = {"appName", "serverName", "statisticsSendInterval"})
    public InfluxStatistics influxStatistics(InfluxStatisticsConfigurationProperties influxStatisticsConfigurationProperties, StatisticsService statisticsService) {
        return new InfluxStatistics(statisticsService, influxStatisticsConfigurationProperties.getAppName(), influxStatisticsConfigurationProperties.getServerName(), influxStatisticsConfigurationProperties.getStatisticsSendInterval());
    }
}
