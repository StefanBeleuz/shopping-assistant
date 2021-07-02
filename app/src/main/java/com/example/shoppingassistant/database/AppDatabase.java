package com.example.shoppingassistant.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.shoppingassistant.daos.ItemDao;
import com.example.shoppingassistant.daos.ShopDao;
import com.example.shoppingassistant.database.converters.Converters;
import com.example.shoppingassistant.entities.Item;
import com.example.shoppingassistant.entities.Shop;

import java.util.concurrent.Executors;

@Database(entities = {Item.class, Shop.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract ItemDao itemDao();

    public abstract ShopDao shopDao();

    public synchronized static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, AppDatabase.class, "my-database")
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadScheduledExecutor().execute(() ->
                                    getInstance(context).shopDao().insertAll(Shop.populateDB()));
                        }
                    })
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}