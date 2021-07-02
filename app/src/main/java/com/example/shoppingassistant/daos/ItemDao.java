package com.example.shoppingassistant.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.shoppingassistant.entities.Item;

import java.util.List;

@Dao
public interface ItemDao {
    @Query("SELECT * FROM items")
    List<Item> getAll();

    @Query("SELECT * FROM items WHERE items.category = :category")
    List<Item> filterByCategory(String category);

    @Update
    void updateItem(Item item);

    @Insert
    void insert(Item item);

    @Delete
    void delete(Item item);
}
