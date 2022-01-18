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

    private LocationListener newLocationListener() {
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                for (MeasurementListener listener : listeners) {
                    Log.v("PROVA", "Fix ricevuto");
                    listener.onLocationChanged(location);
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                for (MeasurementListener listener : listeners) {
                    Log.v("PROVA", "Fix cambio stato");
                    listener.onLocationStatusChanged(s, i, bundle);
                }
            }

            @Override
            public void onProviderEnabled(String s) {
                for (MeasurementListener listener : listeners) {
                    listener.onProviderEnabled(s);
                }
            }

            @Override
            public void onProviderDisabled(String s) {
                for (MeasurementListener listener : listeners) {
                    listener.onProviderDisabled(s);
                }
            }
        };
    }

    private GnssMeasurementsEvent.Callback newGnssMeasurementsEventCallback() {
        return new GnssMeasurementsEvent.Callback() {
            @Override
            public void onGnssMeasurementsReceived(GnssMeasurementsEvent event) {
                for (MeasurementListener logger : listeners) {
                    Log.v("PROVA", "Raw ricevuto");
                    logger.onGnssMeasurementsReceived(event);
                }
            }

            @Override
            public void onStatusChanged(int status) {
                for (MeasurementListener logger : listeners) {
                    Log.v("PROVA", "Raw cambio stato");
                    logger.onGnssMeasurementsStatusChanged(status);
                }
            }
        };
    }

    private GnssNavigationMessage.Callback newGnssNavigationMessageCallback() {
        return new GnssNavigationMessage.Callback() {
            @Override
            public void onGnssNavigationMessageReceived(GnssNavigationMessage event) {
                for (MeasurementListener logger : listeners) {
                    Log.v("PROVA", "Nav ricevuto");
                    logger.onGnssNavigationMessageReceived(event);
                }
            }

            @Override
            public void onStatusChanged(int status) {
                for (MeasurementListener logger : listeners) {
                    Log.v("PROVA", "Nav cambio stato");
                    logger.onGnssNavigationMessageStatusChanged(status);
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
        Log.v("PROVA", "Listener attivati");
    }

    public void unRegisterAll(){
        unregisterLocation();
        locationManager.unregisterGnssMeasurementsCallback(gnssMeasurementsEventListener);
        locationManager.unregisterGnssNavigationMessageCallback(gnssNavigationMessageListener);
        Log.v("PROVA", "Listener disattivati");
    }
}
