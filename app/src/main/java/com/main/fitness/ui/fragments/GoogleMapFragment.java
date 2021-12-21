package com.main.fitness.ui.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;


import com.google.android.gms.tasks.OnFailureListener;
import com.main.fitness.R;

public class GoogleMapFragment extends Fragment {

    //Used for location operations
    protected FusedLocationProviderClient client;
    private GoogleMap mMap;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {

            //Request permission location
            requestPermission();

            //Location monitor
            client = LocationServices.getFusedLocationProviderClient(requireActivity());
            mMap = googleMap;

            //Add function to zoom in and out on the map
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            //Move the camera to the current user location
            moveToCurrentLocation();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_google_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

    }

    //Request phone permission for location
    public void requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        }, 1);

    }

    //Move the view of the user to their current location
    @SuppressLint("MissingPermission")
    public void moveToCurrentLocation() {
        client.getLastLocation().addOnSuccessListener(location -> {

            try {
                //Add the custom marker on the location
                LatLng lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation));
                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(lastLocation));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 17));
                Toast.makeText(requireActivity(), "You are here", Toast.LENGTH_SHORT).show();
            }
            //If user does not enable GPS settings, they will get null result
            catch (NullPointerException e) {
                show_error_no_gps();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireActivity(), "Something wrong !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Guide user to settings to enable GPS
    public void show_error_no_gps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("No", (dialog, id) -> dialog.cancel())
                .setNegativeButton("Yes", (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)));
        final AlertDialog alert = builder.create();
        alert.show();
    }
}