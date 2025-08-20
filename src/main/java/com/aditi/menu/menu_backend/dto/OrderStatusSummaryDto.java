package com.aditi.menu.menu_backend.dto;

public class OrderStatusSummaryDto {
    private long preparingCount;
    private long receivedCount; // Assuming 2 is 'Received'
    private long completedCount;
    private long canceledCount;

    public OrderStatusSummaryDto() {
    }

    public OrderStatusSummaryDto(long preparingCount, long receivedCount, long completedCount, long canceledCount) {
        this.preparingCount = preparingCount;
        this.receivedCount = receivedCount;
        this.completedCount = completedCount;
        this.canceledCount = canceledCount;
    }

    // Getters and Setters
    public long getPreparingCount() {
        return preparingCount;
    }

    public void setPreparingCount(long preparingCount) {
        this.preparingCount = preparingCount;
    }

    public long getReceivedCount() {
        return receivedCount;
    }

    public void setReceivedCount(long receivedCount) {
        this.receivedCount = receivedCount;
    }

    public long getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(long completedCount) {
        this.completedCount = completedCount;
    }

    public long getCanceledCount() {
        return canceledCount;
    }

    public void setCanceledCount(long canceledCount) {
        this.canceledCount = canceledCount;
    }
}
