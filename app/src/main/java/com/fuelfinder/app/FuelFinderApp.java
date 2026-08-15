package com.fuelfinder.app;

import android.app.Application;
import com.google.android.libraries.navigation.NavigationApi;
import com.google.android.libraries.places.api.Places;

public class FuelFinderApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        String key = BuildConfig.MAPS_API_KEY;
        if (key != null && !key.isBlank() && !key.startsWith("YOUR_")) {
            NavigationApi.setApiKey(key);
            if (!Places.isInitialized()) {
                Places.initializeWithNewPlacesApiEnabled(this, key);
            }
        }
    }
}
