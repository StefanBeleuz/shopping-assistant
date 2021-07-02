package com.example.shoppingassistant.services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.shoppingassistant.R;
import com.example.shoppingassistant.database.AppDatabase;
import com.example.shoppingassistant.entities.Item;
import com.example.shoppingassistant.entities.Shop;
import com.example.shoppingassistant.utils.MyLocation;

import java.util.ArrayList;
import java.util.List;

// https://developer.android.com/about/versions/oreo/background-location-limits
public class NotificationService extends Service implements LocationListener {

    public static final String CHANNEL_ID = "10001";
    public static final String CHANNEL_NAME = "Location notification";

    private MyLocation lastLocation = null;

    LocationManager locationManager;

    AppDatabase db;
    List<Shop> shops;
    List<Item> shoppingList;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = AppDatabase.getInstance(this);
        shops = db.shopDao().getAll();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotification(String shopName, List<String> availableItems) {
        String title = String.format("You are near %s.", shopName);
        String text = String.format("Available items: %s", String.join(", ", availableItems));

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            builder.setChannelId(CHANNEL_ID);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        assert notificationManager != null;
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onLocationChanged(Location location) {
        double currLat = location.getLatitude();
        double currLon = location.getLongitude();

        // only notify when travelled more than 150 meters
        if (lastLocation != null && getDistance(currLat, currLon, lastLocation.latitude, lastLocation.longitude) <= 150)
            return;

        shoppingList = db.itemDao().getAll();

        for (Shop shop : shops) {
            double distance = getDistance(currLat, currLon, shop.getLocation().latitude, shop.getLocation().longitude);
            if (distance <= 150) {
                // check if shop has any items from shoppingList
                List<String> availableItems = new ArrayList<>();
                for (Item item : shoppingList) {
                    if (shop.getAvailableItems().stream().anyMatch(item.getName()::equalsIgnoreCase)) {
                        availableItems.add(item.getName());
                    }
                }
                if (availableItems.size() > 0)
                    createNotification(shop.getName(), availableItems);
            }
        }

        lastLocation = new MyLocation(currLat, currLon);
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

    // https://www.movable-type.co.uk/scripts/latlong.html
    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371 * Math.pow(10, 3); // earth’s radius in meters
        // phi is latitude, lambda is longitude
        double phi1 = lat1 * Math.PI / 180;
        double phi2 = lat2 * Math.PI / 180;
        double deltaPhi = (lat2 - lat1) * Math.PI / 180;
        double deltaLambda = (lon2 - lon1) * Math.PI / 180;

        double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) +
                Math.cos(phi1) * Math.cos(phi2) * Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // distance in meters
    }
}
