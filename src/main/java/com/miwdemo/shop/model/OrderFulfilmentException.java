package com.miwdemo.shop.model;

public class OrderFulfilmentException extends RuntimeException {

    public OrderFulfilmentException(Long id) {
        super("Could not process a purchase order for item " + id);
    }
}