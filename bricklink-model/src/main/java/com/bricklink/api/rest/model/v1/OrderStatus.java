package com.bricklink.api.rest.model.v1;

public enum OrderStatus {
    CANCELLED,
    PENDING,
    UPDATED,
    PROCESSING,
    READY,
    PAID,
    PACKED,
    SHIPPED,
    RECEIVED,
    COMPLETED;

    public final String label;

    OrderStatus() {
        this.label = this.name().toLowerCase();
    }
}