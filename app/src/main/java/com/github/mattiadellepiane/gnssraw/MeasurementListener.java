package com.github.mattiadellepiane.gnssraw;

import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.OnNmeaMessageListener;
import android.os.Bundle;

public interface MeasurementListener {

    //void onProviderEnabled(String provider);
    //void onProviderDisabled(String provider);
    void onLocationChanged(Location location);
    //void onLocationStatusChanged(String provider, int status, Bundle extras);

    void onGnssMeasurementsReceived(GnssMeasurementsEvent event);
    //void onGnssMeasurementsStatusChanged(int status);
    void onGnssNavigationMessageReceived(GnssNavigationMessage event);
    /*void onGnssNavigationMessageStatusChanged(int status);
    void onGnssStatusChanged(GnssStatus gnssStatus);
    void onListenerRegistration(String listener, boolean result);
    void onNmeaReceived(long l, String s);
    void onTTFFReceived(long l);*/
}