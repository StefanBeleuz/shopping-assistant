package com.example.shoppingassistant.entities;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.shoppingassistant.utils.MyLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity(tableName = "shops")
public class Shop {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private List<String> availableItems;
    @Embedded
    private MyLocation location;
    @Ignore
    private double distance;

    public Shop(String name, List<String> availableItems, MyLocation location) {
        this.name = name;
        this.availableItems = availableItems;
        this.location = location;
        this.distance = -1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAvailableItems() {
        return availableItems;
    }

    public void setAvailableItems(List<String> availableItems) {
        this.availableItems = availableItems;
    }

    public MyLocation getLocation() {
        return location;
    }

    public void setLocation(MyLocation myLocation) {
        this.location = myLocation;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getLocationStr() {
        return location.latitude + "," + location.longitude;
    }

    public static Shop[] populateDB() {
        return new Shop[]{
                new Shop("Kaufland", new ArrayList<>(Arrays.asList("Bread", "Milk")), new MyLocation(45.41226, 28.01922)),
                new Shop("Lidl", new ArrayList<>(Arrays.asList("Wine", "Water")), new MyLocation(45.41580, 28.00737)),
        };
    }
}
