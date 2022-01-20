/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//NOTICE: File edited (MattiaDellepiane)
//Edited some variable names, removed unused variables, methods and classes, added logs, adjusted imports and package name

package com.github.mattiadellepiane.gnssraw;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MeasurementProvider {

    private List<MeasurementListener> listeners;
    private LocationManager locationManager;
    private SensorMeasurements sensorMeasurements;

    private MainActivity context;
    private LocationListener locationListener = newLocationListener();
    private final GnssMeasurementsEvent.Callback gnssMeasurementsEventListener = newGnssMeasurementsEventCallback();
    private final GnssNavigationMessage.Callback gnssNavigationMessageListener = newGnssNavigationMessageCallback();
    private static final long LOCATION_RATE_GPS_MS = TimeUnit.SECONDS.toMillis(1L);
    private static final long LOCATION_RATE_NETWORK_MS = TimeUnit.SECONDS.toMillis(60L);

    public MeasurementProvider(MainActivity context, SensorMeasurements sensorMeasurements, MeasurementListener... listeners) {
        this.context =  context;
        this.listeners = Arrays.asList(listeners);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.sensorMeasurements = sensorMeasurements;
    }

    private String getDebugTag(){
        return context.getString(R.string.debug_tag);
    }

    private LocationListener newLocationListener() {
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                for (MeasurementListener listener : listeners) {
                    Log.v(getDebugTag(), "Fix ricevuto");
                    listener.onLocationChanged(location);
                }
            }
        };
    }

    private GnssMeasurementsEvent.Callback newGnssMeasurementsEventCallback() {
        return new GnssMeasurementsEvent.Callback() {
            @Override
            public void onGnssMeasurementsReceived(GnssMeasurementsEvent event) {
                for (MeasurementListener logger : listeners) {
                    Log.v(getDebugTag(), "Raw ricevuto");
                    logger.onGnssMeasurementsReceived(event);
                }
            }
        };
    }

    private GnssNavigationMessage.Callback newGnssNavigationMessageCallback() {
        return new GnssNavigationMessage.Callback() {
            @Override
            public void onGnssNavigationMessageReceived(GnssNavigationMessage event) {
                for (MeasurementListener logger : listeners) {
                    Log.v(getDebugTag(), "Nav ricevuto");
                    logger.onGnssNavigationMessageReceived(event);
                }
            }
        };
    }

    public void registerLocation() {
        boolean isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGpsProviderEnabled) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    LOCATION_RATE_NETWORK_MS,
                    0.0f /* minDistance */,
                    locationListener);
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_RATE_GPS_MS,
                    0.0f /* minDistance */,
                    locationListener);
        }
    }

    public void unregisterLocation() {
        locationManager.removeUpdates(locationListener);
    }

    public void registerAll() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        registerLocation();
        locationManager.registerGnssMeasurementsCallback(gnssMeasurementsEventListener);
        locationManager.registerGnssNavigationMessageCallback(gnssNavigationMessageListener);
        Log.v(getDebugTag(), "Listener attivati");
    }

    public void unRegisterAll(){
        unregisterLocation();
        locationManager.unregisterGnssMeasurementsCallback(gnssMeasurementsEventListener);
        locationManager.unregisterGnssNavigationMessageCallback(gnssNavigationMessageListener);
        Log.v(getDebugTag(), "Listener disattivati");
    }
}
