package com.example.shoppingassistant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.shoppingassistant.R;
import com.example.shoppingassistant.entities.Shop;

import java.util.List;
import java.util.Locale;

public class ShopAdapter extends ArrayAdapter<Shop> {

    public ShopAdapter(@NonNull Context context, @NonNull List<Shop> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Shop shop = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.shop_layout, parent, false);
        }

        TextView shopName = convertView.findViewById(R.id.shopName);
        TextView shopDistance = convertView.findViewById(R.id.shopDistance);

        shopName.setText(shop.getName());
        if (shop.getDistance() < 0) {
            shopDistance.setText("-");
        } else {
            shopDistance.setText(String.format(Locale.getDefault(), "%.2f m", shop.getDistance()));
        }

        return convertView;
    }
}
