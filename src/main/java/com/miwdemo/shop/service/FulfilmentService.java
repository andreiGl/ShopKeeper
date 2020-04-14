package com.miwdemo.shop.service;

import com.miwdemo.shop.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FulfilmentService {
    private static final Logger log = LoggerFactory.getLogger(FulfilmentService.class);

    public FulfilmentService() {
    }

    public boolean processOrder(Item item) {
        log.info("Purchase fulfilment placeholder... Ordering item {}", item.getId());

        item.setQuantity(item.getQuantity()-1);

        return true;
    }

}
