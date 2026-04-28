package com.ldif.delivery.order.exception;

import java.util.UUID;

public class OrderNotFoundException extends IllegalArgumentException{
    public OrderNotFoundException(UUID orderId){
        super("존재하지 않거나 삭제된 주문입니다. orderId=" + orderId);
    }
}
