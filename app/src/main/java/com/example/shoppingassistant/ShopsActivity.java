package com.example.shoppingassistant;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.shoppingassistant.adapters.ShopAdapter;
import com.example.shoppingassistant.database.AppDatabase;

import com.example.shoppingassistant.entities.Shop;
import com.example.shoppingassistant.services.NotificationService;

import java.util.ArrayList;
import java.util.List;

public class ShopsActivity extends AppCompatActivity implements LocationListener {

    AppDatabase db;

    List<Shop> shops = new ArrayList<>();

    ShopAdapter shopAdapter;
    ListView shopListView;

    LocationManager locationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "Please activate Location Services!", Toast.LENGTH_SHORT).show();
            }
        }

        db = AppDatabase.getInstance(this);
        shops = db.shopDao().getAll();

        shopAdapter = new ShopAdapter(ShopsActivity.this, shops);
        shopListView = findViewById(R.id.shopListView);
        shopListView.setAdapter(shopAdapter);

        registerForContextMenu(shopListView);
    }

    public void viewAvailableItems(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
        Shop shop = shopAdapter.getItem(menuInfo.position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Available items");

        String[] availableItems = shop.getAvailableItems().toArray(new String[0]);
        builder.setItems(availableItems, (dialog, which) -> {
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void viewOnMap(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
        Shop shop = shopAdapter.getItem(menuInfo.position);

        String uri = String.format("geo:%s?q=%s(%s)&z=15", shop.getLocationStr(), shop.getLocationStr(), shop.getName());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.contextual_menu_shops, menu);
    }

    @Override
    public void onLocationChanged(Location location) {
        for (Shop shop : shops) {
            double distance = NotificationService.getDistance(shop.getLocation().latitude, shop.getLocation().longitude,
                    location.getLatitude(), location.getLongitude());
            shopAdapter.getItem(shopAdapter.getPosition(shop)).setDistance(distance);
            shopAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }
}
