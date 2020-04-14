package com.miwdemo.shop.model;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Item extends RepresentationModel<Item> {

    private @Id @GeneratedValue Long id;

    private String name;
    private String description;
    private int price;
    private int quantity;

    private int basePrice;
    private Timestamp surgeTime;

    @ElementCollection
    private List<Timestamp> views;

    public Item() {
    }

    public Item(String name, String description, int price, int quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;

        this.basePrice = price;
        this.views = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(int basePrice) {
        this.basePrice = basePrice;
    }

    public List<Timestamp> getViews() {
        return views;
    }

    public void setViews(List<Timestamp> views) {
        this.views = views;
    }

    public Timestamp getSurgeTime() {
        return surgeTime;
    }

    public void setSurgeTime(Timestamp surgeTime) {
        this.surgeTime = surgeTime;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", basePrice=" + basePrice +
                ", surgeTime=" + surgeTime +
                ", views=" + views +
                '}';
    }
}
