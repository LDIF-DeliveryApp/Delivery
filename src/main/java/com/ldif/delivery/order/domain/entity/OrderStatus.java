package com.ldif.delivery.order.domain.entity;

public enum OrderStatus {
    PENDING,
    ACCEPTED,
    COOKING,
    DELIVERING,
    COMPLETED,
    CANCELED;

    public boolean canTransitionTo(OrderStatus next) {
        return switch (this) {
            case PENDING -> next == ACCEPTED || next == CANCELED;
            case ACCEPTED -> next == COOKING;
            case COOKING -> next == DELIVERING;
            case DELIVERING -> next == COMPLETED;
            default -> false; // COMPLETED, CANCELED 이후 변경 불가
        };
    }
}
