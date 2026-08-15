package com.fuelfinder.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap.CameraPerspective;
import com.google.android.libraries.navigation.AudioGuidanceSettings;
import com.google.android.libraries.navigation.ListenableResultFuture;
import com.google.android.libraries.navigation.NavigationApi;
import com.google.android.libraries.navigation.Navigator;
import com.google.android.libraries.navigation.RoutingOptions;
import com.google.android.libraries.navigation.SupportNavigationFragment;
import com.google.android.libraries.navigation.Waypoint;
import com.google.android.material.button.MaterialButton;

public class NavigationActivity extends AppCompatActivity {
    private Navigator navigator;
    private SupportNavigationFragment navFragment;
    private String placeId;
    private String stationName;
    private double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        placeId = getIntent().getStringExtra("place_id");
        stationName = getIntent().getStringExtra("station_name");
        lat = getIntent().getDoubleExtra("lat", 0d);
        lng = getIntent().getDoubleExtra("lng", 0d);

        ((TextView) findViewById(R.id.navStationName)).setText(stationName == null ? "Fuel station" : stationName);
        ImageView logo = findViewById(R.id.navStationLogo);
        TextView brandText = findViewById(R.id.navBrandText);
        String logoUrl = BrandLogos.logoFor(stationName);
        if (logoUrl != null) {
            logo.setVisibility(android.view.View.VISIBLE);
            brandText.setVisibility(android.view.View.GONE);
            Glide.with(this).load(logoUrl).fitCenter().into(logo);
        } else {
            logo.setVisibility(android.view.View.GONE);
            brandText.setVisibility(android.view.View.VISIBLE);
            brandText.setText(BrandLogos.shortBrand(stationName));
        }

        MaterialButton endButton = findViewById(R.id.endNavigationButton);
        endButton.setOnClickListener(v -> {
            if (navigator != null) {
                navigator.stopGuidance();
                navigator.clearDestinations();
            }
            finish();
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Precise location permission is required for live navigation.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        navFragment = (SupportNavigationFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_fragment);
        initializeRealNavigation();
    }

    private void initializeRealNavigation() {
        NavigationApi.getNavigator(this, new NavigationApi.NavigatorListener() {
            @Override
            public void onNavigatorReady(Navigator readyNavigator) {
                if (isFinishing() || isDestroyed()) return;
                navigator = readyNavigator;

                navFragment.getMapAsync(googleMap -> {
                    googleMap.followMyLocation(CameraPerspective.TILTED);
                    googleMap.setTrafficEnabled(true);
                });

                RoutingOptions routing = new RoutingOptions();
                routing.travelMode(RoutingOptions.TravelMode.DRIVING);

                Waypoint destination;
                try {
                    Waypoint.Builder builder = Waypoint.builder().setTitle(stationName == null ? "Fuel station" : stationName);
                    if (placeId != null && !placeId.isBlank()) builder.setPlaceIdString(placeId);
                    else builder.setLatLng(lat, lng).setVehicleStopover(true);
                    destination = builder.build();
                } catch (Exception e) {
                    showError("Destination could not be prepared: " + e.getMessage());
                    return;
                }

                ListenableResultFuture<Navigator.RouteStatus> route = navigator.setDestination(destination, routing);
                route.setOnResultListener(status -> {
                    if (status == Navigator.RouteStatus.OK) {
                        AudioGuidanceSettings audio = AudioGuidanceSettings.builder()
                            .setGuidanceMode(AudioGuidanceSettings.GuidanceMode.VOICE_ALERTS_AND_GUIDANCE)
                            .setVolumeLevel(AudioGuidanceSettings.VolumeLevel.NORMAL)
                            .build();
                        navigator.setAudioGuidanceSettings(audio);
                        navigator.startGuidance();
                    } else {
                        showError("Route could not start: " + status);
                    }
                });
            }

            @Override
            public void onError(int errorCode) {
                if (!isFinishing() && !isDestroyed()) {
                    showError("Navigation SDK error: " + errorCode + ". Check API restrictions, network, location and Terms acceptance.");
                }
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        if (navigator != null && isFinishing()) {
            navigator.stopGuidance();
        }
        super.onDestroy();
    }
}
