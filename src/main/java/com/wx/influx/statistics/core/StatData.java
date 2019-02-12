package com.wx.influx.statistics.core;

import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xinquan.huangxq
 */
@Data
public class StatData implements Serializable {

    private String key;

    private double count;

    private double min;

    private double max;

    private double total;

    private double average;

    private String appName, category, action, result;

    private transient ReentrantLock lock;

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(key);
        buf.append(" ");
        buf.append(count);
        buf.append(" ");
        buf.append(min);
        buf.append(" ");
        buf.append(max);
        buf.append(" ");
        buf.append(total);
        buf.append(" ");
        buf.append(average);
        return buf.toString();
    }

    public StatData(String key, String appName, String category, String action, String result, ReentrantLock lock) {
        this.lock = lock;
        this.appName = appName;
        this.category = category;
        this.action = action;
        this.result = result;

        this.key = key;
        count = 0;
        min = 0;
        max = 0;
        total = 0;
        average = 0;
    }

    public StatData(String key) {
        this.key = key;
        count = 0;
        min = 0;
        max = 0;
        total = 0;
        average = 0;
    }

    public void accumulate(long count, double cost) {
        if (lock != null)
            lock.lock();
        try {
            this.count += count;
            if (min < 0.00000001 || cost < min)
                min = cost;
            if (cost > max)
                max = cost;
            total += cost;
            if (this.count > 0)
                average = total / this.count;
        } finally {
            if (lock != null)
                lock.unlock();
        }
    }

    public void accumulate(StatData data) {
        if (lock != null)
            lock.lock();
        try {
            this.count += data.count;
            if (min < 0.00000001 || data.min < min)
                min = data.min;
            if (data.max > max)
                max = data.max;
            total += data.total;
            if (this.count > 0)
                average = total / this.count;
        } finally {
            if (lock != null)
                lock.unlock();
        }
    }
}
