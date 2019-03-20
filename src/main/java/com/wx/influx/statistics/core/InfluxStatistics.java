package com.wx.influx.statistics.core;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public class InfluxStatistics {

    private final StatisticsService statisticsService;

    private final String appName;

    private final String serverName;

    private final int sendInterval;

    private ConcurrentHashMap<String, StatData> map = new ConcurrentHashMap<>();

    private Timer reportTimer;

    public InfluxStatistics(StatisticsService statisticsService, String appName, String serverName, int sendInterval) {
        this.statisticsService = statisticsService;
        this.appName = appName;
        this.serverName = serverName;
        this.sendInterval = sendInterval;
    }

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        reportTimer = new Timer(true);
        reportTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (map.size() > 0) {
                        Map<String, StatData> copy = new HashMap<String, StatData>(map.size());
                        copy.putAll(map);
                        map.clear();

                        if (statisticsService != null) {
                            collect(copy);
                        }
                    }
                } catch (Exception e) {
                    log.error("初始化统计模块失败", e);
                }
            }
        }, sendInterval, sendInterval);
    }

    private void collect(Map<String, StatData> copy) {
        statisticsService.collect(serverName, copy);
    }

    /**
     * 统计
     *
     * @param category 类别
     * @param action 行为动作
     * @param result 结果
     * @param count 计数
     * @param cost 耗时
     */
    public void accumulate(String category, String action, String result, long count, double cost) {
        if (statisticsService == null) {
            return;
        }

        try {
            String key = String.format("%s.%s.%s.%s", appName, category, action, result);
            StatData c = map.get(key);
            if (c == null) {
                StatData v = map.putIfAbsent(key, new StatData(key, appName, category, action, result, new ReentrantLock()));
                if (v == null) {
                    c = map.get(key);
                }
            }
            c.accumulate(count, cost);
        } catch (Exception e) {
            log.error("统计时出现异常", e);
        }
    }
}
