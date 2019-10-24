package com.example.gmap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the SDK
        Places.initialize(getApplicationContext(), getString(R.string.geo_api_key));

        // Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);

        startActivityForResult(new Intent(getApplicationContext(), SelectLocationActivity.class), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK) {
            double longitude = Double.parseDouble(data.getStringExtra("longitude"));
            double latitude = Double.parseDouble(data.getStringExtra("latitude"));

            Toast.makeText(getApplicationContext(), data.getStringExtra("longitude") + "/" + data.getStringExtra("latitude"), Toast.LENGTH_SHORT);
        }
    }
}
