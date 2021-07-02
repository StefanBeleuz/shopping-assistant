package com.example.shoppingassistant.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.shoppingassistant.entities.Shop;

import java.util.List;

@Dao
public interface ShopDao {
    @Query("SELECT * FROM shops")
    List<Shop> getAll();

    @Insert
    void insertAll(Shop... shops);
}
