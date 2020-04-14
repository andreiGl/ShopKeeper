package com.miwdemo.shop.model;

public class ItemSoldOutException extends RuntimeException {

    public ItemSoldOutException(Long id) {
        super("Item " + id + " is sold out.");
    }
}