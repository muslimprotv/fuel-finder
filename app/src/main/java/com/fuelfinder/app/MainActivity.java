package com.fuelfinder.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.CircularBounds;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.SearchNearbyRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocation;
    private PlacesClient placesClient;
    private StationAdapter adapter;
    private TextView status;
    private CircularProgressIndicator progress;
    private MaterialButton findButton;

    private final ActivityResultLauncher<String[]> permissionsLauncher =
        registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean fine = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION));
            boolean coarse = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_COARSE_LOCATION));
            if (fine || coarse) loadNearbyFuelStations();
            else setStatus("Location access is required to find nearby fuel stations.", false);
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = findViewById(R.id.statusText);
        progress = findViewById(R.id.searchProgress);
        findButton = findViewById(R.id.findButton);
        RecyclerView list = findViewById(R.id.stationList);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StationAdapter(this::openRealNavigation);
        list.setAdapter(adapter);

        fusedLocation = LocationServices.getFusedLocationProviderClient(this);
        if (Places.isInitialized()) placesClient = Places.createClient(this);

        findButton.setOnClickListener(v -> ensurePermissionAndSearch());
        ensurePermissionAndSearch();
    }

    private void ensurePermissionAndSearch() {
        if (BuildConfig.MAPS_API_KEY.startsWith("YOUR_")) {
            setStatus("Add your Google Maps Platform API key in secrets.properties first.", false);
            return;
        }
        boolean fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (fine || coarse) loadNearbyFuelStations();
        else permissionsLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
    }

    @SuppressWarnings("MissingPermission")
    private void loadNearbyFuelStations() {
        if (placesClient == null) {
            setStatus("Places API is not initialized. Check your API key and enabled APIs.", false);
            return;
        }
        setStatus("Finding fuel stations around you…", true);
        fusedLocation.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener(location -> {
                if (location == null) {
                    setStatus("Could not get a GPS fix. Turn on precise location and try again.", false);
                    return;
                }
                searchFuel(location);
            })
            .addOnFailureListener(e -> setStatus("Location error: " + e.getMessage(), false));
    }

    private void searchFuel(Location userLocation) {
        LatLng center = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        CircularBounds circle = CircularBounds.newInstance(center, 7000.0);
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.DISPLAY_NAME, Place.Field.LAT_LNG);
        SearchNearbyRequest request = SearchNearbyRequest.builder(circle, fields)
            .setIncludedTypes(Arrays.asList("gas_station"))
            .setMaxResultCount(20)
            .build();

        placesClient.searchNearby(request)
            .addOnSuccessListener(response -> {
                List<Station> result = new ArrayList<>();
                for (Place place : response.getPlaces()) {
                    if (place.getId() == null || place.getLatLng() == null) continue;
                    String name = place.getDisplayName() == null ? "Fuel Station" : place.getDisplayName().toString();
                    float[] distance = new float[1];
                    Location.distanceBetween(
                        userLocation.getLatitude(), userLocation.getLongitude(),
                        place.getLatLng().latitude, place.getLatLng().longitude,
                        distance
                    );
                    result.add(new Station(place.getId(), name, place.getLatLng().latitude, place.getLatLng().longitude, distance[0]));
                }
                result.sort(Comparator.comparingDouble(s -> s.distanceMeters));
                adapter.submit(result);
                setStatus(result.isEmpty() ? "No fuel stations found within 7 km." : result.size() + " nearby fuel stations found", false);
            })
            .addOnFailureListener(e -> setStatus("Fuel station search failed: " + e.getMessage(), false));
    }

    private void openRealNavigation(Station station) {
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.putExtra("place_id", station.placeId);
        intent.putExtra("station_name", station.name);
        intent.putExtra("lat", station.lat);
        intent.putExtra("lng", station.lng);
        startActivity(intent);
    }

    private void setStatus(String message, boolean loading) {
        status.setText(message);
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        findButton.setEnabled(!loading);
    }
}
