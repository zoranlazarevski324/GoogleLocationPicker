package com.example.gmap;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gmap.nearbylocations.GooglePlacesReadTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class SelectLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private LinearLayout searchbar;
    private TextView    searchInput;
    private MapView mapView;
    private GoogleMap gmap;

    private RelativeLayout placeTypeSheet;
    private RelativeLayout extraSheet;
    private ListView placeTypeList;
    private LinearLayout placeType;
    private TextView txtPlaceType;

    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    private Double latitude = 40.7143528;
    private Double longitude = -74.0059731;
    private boolean isShowPlaceTypeList = false;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    private String placeTypes[] = {
        "Bakery",
        "Bar",
        "Beauty Salon",
        "Cafe",
        "Casino",
        "Clothing Store",
        "Department Store",
        "Furniture Store",
        "Hardware Store",
        "Jewelery Store",
        "Liquor Store",
        "Meal Takeaways",
        "Restaurant",
        "Store"
    };

    private String currentPlaceType = "Restaurant";

    protected LocationManager locationManager;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            locationSelected(new LatLng(latitude, longitude), 0.0f);
            locationManager.removeUpdates(mLocationListener);
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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        placeTypeSheet = (RelativeLayout) findViewById(R.id.placetypesheet);
        extraSheet = (RelativeLayout) findViewById(R.id.extrapart);
        placeTypeList = (ListView) findViewById(R.id.placelist);
        placeType = (LinearLayout) findViewById(R.id.placetype);
        txtPlaceType = (TextView) findViewById(R.id.place_type_text);

        searchInput = (TextView) findViewById(R.id.searchinput);
        searchbar = (LinearLayout) findViewById(R.id.searchbar);
        searchbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(getApplicationContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

//        Bundle mapViewBundle = null;
//        if (savedInstanceState != null) {
//            mapViewBundle = savedInstanceState.getBundle(getString(R.string.geo_api_key));
//        }

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        }

        placeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isShowPlaceTypeList = true;
                placeTypeSheet.setVisibility(View.VISIBLE);
            }
        });

        placeTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentPlaceType = getResources().getStringArray(R.array.placetypes)[i];
                txtPlaceType.setText(currentPlaceType);
                isShowPlaceTypeList = false;
                placeTypeSheet.setVisibility(View.INVISIBLE);
                loadPlaces();
            }
        });

        extraSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isShowPlaceTypeList = false;
                placeTypeSheet.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                searchInput.setText(place.getName());
                locationSelected(place.getLatLng(), 0.0f);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(1);
        gmap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng ll = gmap.getCameraPosition().target;
                longitude = ll.longitude;
                latitude = ll.latitude;
                loadPlaces();
            }
        });
        locationSelected(new LatLng(latitude, longitude), 16f);
    }

    private void locationSelected(LatLng ll, float zoom){
        longitude = ll.longitude;
        latitude = ll.latitude;
        if(zoom == 0){
            gmap.moveCamera(CameraUpdateFactory.newLatLng(ll));
        } else {
            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, zoom));
        }
        loadPlaces();
    }

    private void loadPlaces(){
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 500);
        googlePlacesUrl.append("&types=" + currentPlaceType);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + getString(R.string.geo_api_key));

        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
        Object[] toPass = new Object[2];
        toPass[0] = gmap;
        toPass[1] = googlePlacesUrl.toString();
        googlePlacesReadTask.execute(toPass);
    }

    public void onDone(View view){
        LatLng location = gmap.getCameraPosition().target;
        Intent returnIntent = new Intent();
        returnIntent.putExtra("longitude", String.valueOf(location.longitude));
        returnIntent.putExtra("latitude", String.valueOf(location.latitude));
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
