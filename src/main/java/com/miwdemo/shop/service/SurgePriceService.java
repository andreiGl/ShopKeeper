package com.miwdemo.shop.service;

import com.miwdemo.shop.model.Item;
import com.miwdemo.shop.model.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SurgePriceService {
    private static final Logger log = LoggerFactory.getLogger(SurgePriceService.class);

    private final static int SURGE_PRICE_VIEW_COUNT = 10 + 1;
    private final static int SURGE_PRICE_PERCENTAGE = 10;

    private final ItemRepository repository;

    public SurgePriceService(ItemRepository repository) {
        this.repository = repository;
    }

    public Item surgePriceAndUpdate(Item item) {
        if (item == null) {
            log.error("Got empty/null item.");
            return null;
        }

        List<Timestamp> views = item.getViews();
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        views.add(currentTimestamp);

        if (views.size() >= SURGE_PRICE_VIEW_COUNT) {

            views = views.stream()
                    .sorted()
                    .collect(Collectors.toList());

            if (views.size() > SURGE_PRICE_VIEW_COUNT)
                views = views.subList(1, views.size());

            //We have size==surgeCount.
            Timestamp oldestView = views.get(0);

            if (item.getSurgeTime() == null) {
                //No surge yet, setting surge:
                if (isWithinHour(oldestView)) {
                    item.setSurgeTime(currentTimestamp);
                    doPriceIncrease(item);
                }
            } else {
                //refresh surgeTime:
                if (isWithinHour(oldestView))
                    item.setSurgeTime(currentTimestamp);
                else {
                    //keeping surge only for 1 hour, regardless of viewCount:
                    if (!isWithinHour(item.getSurgeTime())) {
                        item.setSurgeTime(null);
                        doPriceRestore(item);
                    }
                }
            }
        }

        return repository.save(item);
    }

    private void doPriceIncrease(Item item) {
        item.setPrice(item.getBasePrice() * (100 + SURGE_PRICE_PERCENTAGE) / 100);
    }

    private void doPriceRestore(Item item) {
        item.setPrice(item.getBasePrice());
    }

    private boolean isWithinHour(Timestamp ts) {
        long timeDiff = System.currentTimeMillis() - ts.getTime();
        return (timeDiff <= 60 * 60 * 1000);
    }
}
