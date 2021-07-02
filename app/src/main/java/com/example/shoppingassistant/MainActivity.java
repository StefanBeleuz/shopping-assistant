package com.example.shoppingassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.shoppingassistant.adapters.ItemAdapter;
import com.example.shoppingassistant.database.AppDatabase;
import com.example.shoppingassistant.entities.Item;
import com.example.shoppingassistant.services.NotificationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    AppDatabase db;

    ListView shoppingListView;

    ItemAdapter itemAdapter;
    ArrayAdapter<String> spinnerAdapter;

    String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            startService(new Intent(this, NotificationService.class));
        }

        List<String> categories = new ArrayList<String>() {{
            add("All");
            addAll(Arrays.asList(getResources().getStringArray(R.array.categories)));
        }};
        spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, categories);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        Spinner categorySpinner = findViewById(R.id.categorySpinner);
        categorySpinner.setAdapter(spinnerAdapter);
        categorySpinner.setSelection(spinnerAdapter.getPosition("All"));

        db = AppDatabase.getInstance(this);

        itemAdapter = new ItemAdapter(MainActivity.this, db.itemDao().getAll());
        shoppingListView = findViewById(R.id.shoppingListView);
        shoppingListView.setAdapter(itemAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategory = (String) adapterView.getItemAtPosition(i);
                updateAdapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        registerForContextMenu(shoppingListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.contextual_menu, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("InflateParams")
    public void addItem(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new item");

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.new_item_alert_layout, null);
        builder.setView(dialogView);

        builder.setPositiveButton("Add item", (dialog, which) -> {
            String itemName = ((EditText) dialogView.findViewById(R.id.itemNameEditText)).getText().toString();
            String itemCategory = ((Spinner) dialogView.findViewById(R.id.itemCategorySpinner)).getSelectedItem().toString();
            String itemQuantity = ((EditText) dialogView.findViewById(R.id.itemQuantityEditText)).getText().toString();

            db.itemDao().insert(new Item(itemName, itemCategory, Integer.parseInt(itemQuantity)));
            updateAdapter();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void changeItem(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
        Item item = itemAdapter.getItem(menuInfo.position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change item");

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.new_item_alert_layout, null);

        EditText itemNameEditText = dialogView.findViewById(R.id.itemNameEditText);
        Spinner itemCategorySpinner = dialogView.findViewById(R.id.itemCategorySpinner);
        EditText itemQuantityEditText = dialogView.findViewById(R.id.itemQuantityEditText);

        itemNameEditText.setText(item.getName());
        itemCategorySpinner.setSelection(spinnerAdapter.getPosition(item.getCategory()) - 1);
        itemQuantityEditText.setText(String.valueOf(item.getQuantity()));

        builder.setView(dialogView);

        builder.setPositiveButton("Change item", (dialog, which) -> {
            String itemName = itemNameEditText.getText().toString();
            String itemCategory = itemCategorySpinner.getSelectedItem().toString();
            String itemQuantity = itemQuantityEditText.getText().toString();

            item.setName(itemName);
            item.setCategory(itemCategory);
            item.setQuantity(Integer.parseInt(itemQuantity));

            db.itemDao().updateItem(item);
            updateAdapter();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteItem(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
        Item item = itemAdapter.getItem(menuInfo.position);

        db.itemDao().delete(item);
        updateAdapter();
    }

    private void updateAdapter() {
        itemAdapter.clear();
        if (selectedCategory.equals("All")) {
            itemAdapter.addAll(db.itemDao().getAll());
        } else {
            itemAdapter.addAll(db.itemDao().filterByCategory(selectedCategory));
        }
        itemAdapter.notifyDataSetChanged();
    }

    public void viewShops(MenuItem menuItem) {
        Intent intent = new Intent(this, ShopsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startService(new Intent(this, NotificationService.class));
            } else {
                Toast.makeText(MainActivity.this,
                        "Permission for accessing location denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}