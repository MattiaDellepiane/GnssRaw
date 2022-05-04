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
package com.github.mattiadellepiane.gnssraw

import android.Manifest
import com.github.mattiadellepiane.gnssraw.data.SharedData
import android.location.GnssMeasurementsEvent
import java.util.Arrays
import android.os.Build
import java.util.concurrent.Executors
import com.github.mattiadellepiane.gnssraw.listeners.MeasurementListener
import android.location.LocationManager
import android.location.GnssNavigationMessage
import android.content.pm.PackageManager
import android.location.LocationListener
import android.annotation.SuppressLint
import android.content.*
import android.location.GnssMeasurementRequest
import android.util.Log
import androidx.core.app.ActivityCompat
import java.util.concurrent.TimeUnit

class MeasurementProvider(private val context: MainActivity, sensorMeasurements: SensorMeasurements, vararg listeners: MeasurementListener) {
    private val listeners: List<MeasurementListener>
    private val locationManager: LocationManager
    private val sensorMeasurements: SensorMeasurements
    private val locationListener = newLocationListener()
    private val gnssMeasurementsEventListener = newGnssMeasurementsEventCallback()
    private val gnssNavigationMessageListener = newGnssNavigationMessageCallback()
    private val debugTag: String
        private get() = context.getString(R.string.debug_tag)

    private fun newLocationListener(): LocationListener {
        return LocationListener { location ->
            for (listener in listeners) {
                Log.v(debugTag, "Fix ricevuto")
                listener.onLocationChanged(location)
            }
        }
    }

    private fun newGnssMeasurementsEventCallback(): GnssMeasurementsEvent.Callback {
        return object : GnssMeasurementsEvent.Callback() {
            override fun onGnssMeasurementsReceived(event: GnssMeasurementsEvent) {
                for (logger in listeners) {
                    Log.v(debugTag, "Raw ricevuto")
                    logger.onGnssMeasurementsReceived(event)
                }
            }
        }
    }

    private fun newGnssNavigationMessageCallback(): GnssNavigationMessage.Callback {
        return object : GnssNavigationMessage.Callback() {
            override fun onGnssNavigationMessageReceived(event: GnssNavigationMessage) {
                for (logger in listeners) {
                    Log.v(debugTag, "Nav ricevuto")
                    logger.onGnssNavigationMessageReceived(event)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun registerLocation() {
        val isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (isGpsProviderEnabled) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                return
            }
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    LOCATION_RATE_NETWORK_MS,
                    0.0f /* minDistance */,
                    locationListener)
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_RATE_GPS_MS,
                    0.0f /* minDistance */,
                    locationListener)
        }
    }

    fun unregisterLocation() {
        locationManager.removeUpdates(locationListener)
    }

    @SuppressLint("MissingPermission")
    fun registerAll() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        registerLocation()
        val ex = Executors.newFixedThreadPool(2)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val requestBuilder = GnssMeasurementRequest.Builder().setFullTracking(SharedData.instance.fullTracking)
            Log.v("PROVA", "FullTracking: " + SharedData.instance.fullTracking)
            locationManager.registerGnssMeasurementsCallback(requestBuilder.build(), SharedData.instance.context!!.mainExecutor, gnssMeasurementsEventListener)
        } else {
            locationManager.registerGnssMeasurementsCallback(gnssMeasurementsEventListener)
        }
        locationManager.registerGnssNavigationMessageCallback(gnssNavigationMessageListener)
        Log.v(debugTag, "Listener attivati")
        startLogging()
    }

    fun unRegisterAll() {
        unregisterLocation()
        locationManager.unregisterGnssMeasurementsCallback(gnssMeasurementsEventListener)
        locationManager.unregisterGnssNavigationMessageCallback(gnssNavigationMessageListener)
        Log.v(debugTag, "Listener disattivati")
        stopLogging()
    }

    private fun startLogging() {
        for (logger in listeners) {
            logger.startLogging()
        }
    }

    private fun stopLogging() {
        for (logger in listeners) {
            logger.stopLogging()
        }
    }

    companion object {
        private val LOCATION_RATE_GPS_MS = TimeUnit.SECONDS.toMillis(1L)
        private val LOCATION_RATE_NETWORK_MS = TimeUnit.SECONDS.toMillis(60L)
    }

    init {
        this.listeners = listOf(*listeners)
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        this.sensorMeasurements = sensorMeasurements
    }
}