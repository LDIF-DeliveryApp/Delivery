package com.ldif.delivery.order.exception;

public class OrderBusinessException extends IllegalStateException{
    public OrderBusinessException(String message) {
        super(message);
    }
}
