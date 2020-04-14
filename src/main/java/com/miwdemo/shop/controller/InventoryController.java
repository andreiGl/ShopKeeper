package com.miwdemo.shop.controller;

import com.miwdemo.shop.model.*;
import com.miwdemo.shop.service.FulfilmentService;
import com.miwdemo.shop.service.SurgePriceService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class InventoryController {

    private final ItemRepository repository;
    private final SurgePriceService surgePriceService;
    private final FulfilmentService fulfilmentService;

    public InventoryController(ItemRepository repository, SurgePriceService surgePriceService, FulfilmentService fulfilmentService) {
        this.repository = repository;
        this.surgePriceService = surgePriceService;
        this.fulfilmentService = fulfilmentService;
    }

    @GetMapping("/items")
    CollectionModel<ItemSummary> all() {
        List<Item> items = repository.findAll();

        List<ItemSummary> itemSummaries = new ArrayList<>();
        for (Item item : items) {
            //skip soldout items:
            if (item.getQuantity() < 1)
                continue;

            Long id = item.getId();
            ItemSummary summary = new ItemSummary(item);
            summary.add(linkTo(methodOn(InventoryController.class).one(id)).withSelfRel());
            summary.add(linkTo(methodOn(InventoryController.class).buy(id)).withSelfRel());
            itemSummaries.add(summary);
        }
        return new CollectionModel<>(itemSummaries, linkTo(methodOn(InventoryController.class).all()).withSelfRel());
    }

    //TODO remove...
    @Deprecated
    @GetMapping("/itemsDetailed")
    CollectionModel<Item> allDetailed() {
        List<Item> items = repository.findAll();

        for (Item item : items) {
            Long id = item.getId();
            item.add(linkTo(methodOn(InventoryController.class).one(id)).withSelfRel());
            item.add(linkTo(methodOn(InventoryController.class).buy(id)).withSelfRel());
        }
        return new CollectionModel<>(items, linkTo(methodOn(InventoryController.class).all()).withSelfRel());
    }


    //TODO remove...
    @Deprecated
    @PostMapping("/items")
    EntityModel<Item> newItem(@RequestBody Item newItem) {
        Item item = repository.save(newItem);

        return new EntityModel<>(item,
                linkTo(methodOn(InventoryController.class).one(item.getId())).withSelfRel(),
                linkTo(methodOn(InventoryController.class).all()).withRel("items"),
                linkTo(methodOn(InventoryController.class).buy(item.getId())).withSelfRel()
        );
    }

    // Single item
    @GetMapping("/items/{id}")
    EntityModel<ItemSummary> one(@PathVariable Long id) {
        Item item = repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));

        if (item.getQuantity() < 1)
            throw new ItemSoldOutException(id);

        ItemSummary itemSummary = new ItemSummary(surgePriceService.surgePriceAndUpdate(item));

        return new EntityModel<>(itemSummary,
                linkTo(methodOn(InventoryController.class).one(id)).withSelfRel(),
                linkTo(methodOn(InventoryController.class).all()).withRel("items"),
                linkTo(methodOn(InventoryController.class).buy(id)).withSelfRel()
        );
    }

    @PostMapping("/buy/{id}")
    EntityModel<ItemSummary> buy(@PathVariable Long id) {
        Item item = repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));

        if (item.getQuantity() < 1)
            throw new ItemSoldOutException(id);

        //purchase processing ...
        if (!fulfilmentService.processOrder(item)) {
            throw new OrderFulfilmentException(id);
        }

        //Surge price (if needed) after purchase (same as view); save item.
        ItemSummary itemSummary = new ItemSummary(surgePriceService.surgePriceAndUpdate(item));

        return new EntityModel<>(itemSummary,
                linkTo(methodOn(InventoryController.class).one(id)).withSelfRel(),
                linkTo(methodOn(InventoryController.class).all()).withRel("items")
        );
    }

    //TODO remove...
    @Deprecated
    @PutMapping("/items/{id}")
    EntityModel<Item> replaceItem(@RequestBody Item newItem, @PathVariable Long id) {
        Item item = repository.findById(id)
                .map(itm -> {
                    itm.setName(newItem.getName());
                    itm.setDescription(newItem.getDescription());
                    itm.setPrice(newItem.getPrice());
                    itm.setBasePrice(newItem.getPrice());
                    itm.setQuantity(newItem.getQuantity());
                    return repository.save(itm);
                })
                .orElseGet(() -> {
                    newItem.setId(id);
                    return repository.save(newItem);
                });

        return new EntityModel<>(item,
                linkTo(methodOn(InventoryController.class).one(id)).withSelfRel(),
                linkTo(methodOn(InventoryController.class).all()).withRel("items"),
                linkTo(methodOn(InventoryController.class).buy(id)).withSelfRel()
        );
    }

    @Deprecated
    @DeleteMapping("/items/{id}")
    void deleteItem(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
