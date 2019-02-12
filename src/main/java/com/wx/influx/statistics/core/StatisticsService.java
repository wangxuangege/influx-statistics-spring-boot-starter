package com.wx.influx.statistics.core;

import java.util.Map;

/**
 * @author xinquan.huangxq
 */
public interface StatisticsService {

    /**
     * 收集统计信息
     *
     * @param serverName 机器名称
     * @param dataMap 统计数据
     */
    void collect(String serverName, Map<String, StatData> dataMap);
}
