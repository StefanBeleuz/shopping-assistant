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
import com.example.shoppingassistant.entities.Item;

import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> {

    public ItemAdapter(@NonNull Context context, @NonNull List<Item> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Item item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_layout, parent, false);
        }

        TextView itemName = convertView.findViewById(R.id.shopName);
        TextView itemCategory = convertView.findViewById(R.id.itemCategory);
        TextView itemQuantity = convertView.findViewById(R.id.itemQuantity);

        itemName.setText(item.getName());
        itemCategory.setText(item.getCategory());
        itemQuantity.setText(String.valueOf(item.getQuantity()));

        return convertView;
    }
}
