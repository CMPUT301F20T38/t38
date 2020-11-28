package com.example.booker.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.booker.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Jason'part
 * In this activity, displaying the pick-up location in a a map view, and user can jump to google map for direction
 * to the location.
 *
 * the geo location is retrieved from the cloud firestore under borrow.
 *
 * Modified and referenced to
 * https://developers.google.com/maps/documentation/android-sdk/start
 *
 */

public class DisplayMapActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap1;
    private Button backBtn;
    private Double selectedLat,selectedLon;
    private final String TAG="Display Map for you";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_borrow);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        Intent receivedLocationIntent = getIntent();

        selectedLat = receivedLocationIntent.getExtras().getDouble("LAT");
        selectedLon = receivedLocationIntent.getExtras().getDouble("LON");

        Log.d(TAG, "Data received  "+ selectedLat+ "   borrower  "+ selectedLon);



        backBtn = (Button) findViewById(R.id.goBackButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    finish();
                    /*Intent intent = new Intent(DisplayMapActivity.this, BorrowedBookListActivity.class);
                    startActivity(intent);*/
            }

        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap1 = googleMap;
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(selectedLat, selectedLon, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMap1.getUiSettings().setZoomControlsEnabled(true);
        mMap1.getUiSettings().setMapToolbarEnabled(true);
//        mMap1.getUiSettings().set;



        // Add a marker in Sydney and move the camera
        LatLng borrow_UA = new LatLng(selectedLat, selectedLon);
        mMap1.addMarker(new MarkerOptions().position(borrow_UA).title(addresses.get(0).getAddressLine(0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        mMap1.moveCamera(CameraUpdateFactory.newLatLngZoom(borrow_UA,16));
    }
}
