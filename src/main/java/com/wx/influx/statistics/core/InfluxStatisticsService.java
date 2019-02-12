package com.wx.influx.statistics.core;

import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public class InfluxStatisticsService implements StatisticsService {

    private final InfluxDB influxDB;

    private final String influxDbName;

    private final String influxRetentionPolicy;

    private final int sendInterval;

    private List<Point> points = Collections.synchronizedList(new ArrayList<Point>());

    private Timer reportTimer;

    private ReentrantLock lock = new ReentrantLock();

    public InfluxStatisticsService(InfluxDB influxDB, String influxDbName, String influxRetentionPolicy, int sendInterval) {
        this.influxDB = influxDB;
        this.influxDbName = influxDbName;
        this.influxRetentionPolicy = influxRetentionPolicy;
        this.sendInterval = sendInterval;
    }

    /**
     * 初始化
     */
    public void init() {
        reportTimer = new Timer(true);
        reportTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Point[] pointArray;
                lock.lock();
                try {
                    pointArray = points.toArray(new Point[0]);
                    points.clear();
                } finally {
                    lock.unlock();
                }
                sendInflux(pointArray);
            }
        }, sendInterval, sendInterval);
    }

    private void sendInflux(Point[] pointArray) {
        if (pointArray == null || pointArray.length == 0)
            return;

        try {
            BatchPoints batchPoints = BatchPoints
                    .database(influxDbName)
                    .retentionPolicy(influxRetentionPolicy)
                    .consistency(InfluxDB.ConsistencyLevel.ALL)
                    .points(pointArray)
                    .build();
            influxDB.write(batchPoints);
        } catch (Exception e) {
            log.error("上送influx统计信息失败", e);
        }
    }


    public void collect(String serverName, Map<String, StatData> dataMap) {
        lock.lock();
        try {
            long now = System.currentTimeMillis();
            for (StatData data : dataMap.values()) {
                try {
                    Point point = Point.measurement("statistics")
                            .time(now, TimeUnit.MILLISECONDS)
                            .tag("server", serverName)
                            .tag("event", data.getKey())
                            .tag("app", data.getAppName())
                            .tag("category", data.getCategory())
                            .tag("action", data.getAction())
                            .tag("result", data.getResult())
                            .addField("count", data.getCount())
                            .addField("min", data.getMin())
                            .addField("max", data.getMax())
                            .addField("total", data.getTotal())
                            .addField("average", data.getAverage())
                            .build();
                    points.add(point);
                } catch (Exception e) {
                    log.error("收集统计信息异常: ", e);
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
