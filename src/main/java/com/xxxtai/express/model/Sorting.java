package com.xxxtai.express.model;

public class Sorting extends Base{
    private long totalTime;
    private long totalQuantity;
    private long perHour;

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public long getPerHour() {
        return perHour;
    }

    public void setPerHour(long perHour) {
        this.perHour = perHour;
    }
}
