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
//Commented out the entire class since I'm not using it at the moment. Changed package name
package com.github.mattiadellepiane.gnssraw.utils.gnss

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.github.mattiadellepiane.gnssraw.R
import com.github.mattiadellepiane.gnssraw.data.SharedData
import java.lang.Runnable
import org.achartengine.GraphicalView
import android.widget.TextView
import com.github.mattiadellepiane.gnssraw.ui.main.tabs.PlotFragment.ColorMap
import com.github.mattiadellepiane.gnssraw.ui.main.tabs.PlotFragment.DataSetManager
import org.achartengine.renderer.XYMultipleSeriesRenderer
import android.widget.LinearLayout
import android.widget.Spinner
import com.github.mattiadellepiane.gnssraw.ui.main.tabs.PlotFragment
import android.widget.AdapterView
import org.achartengine.model.XYMultipleSeriesDataset
import org.achartengine.ChartFactory
import android.graphics.Color
import android.location.GnssMeasurementsEvent
import android.location.GnssMeasurement
import java.util.ArrayList
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.Spannable
import android.location.GnssStatus
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.GpsNavigationMessageStore
import java.util.Collections
import java.util.Comparator
import org.achartengine.model.XYSeries
import org.achartengine.renderer.XYSeriesRenderer
import org.achartengine.util.MathHelper
import android.graphics.Paint.Align
import java.util.Locale
import android.widget.ImageButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.util.Arrays
import android.os.Environment
import java.io.FileFilter
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import android.view.Gravity
import androidx.core.content.ContextCompat
import android.util.TypedValue
import android.view.View.OnLongClickListener
import android.content.DialogInterface
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import androidx.preference.PreferenceFragmentCompat
import android.content.SharedPreferences
import com.google.android.material.button.MaterialButton
import android.widget.Button
import com.github.mattiadellepiane.gnssraw.services.BackgroundMeasurementService
import android.os.Build
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.fragment.app.FragmentActivity
import com.github.mattiadellepiane.gnssraw.utils.gnss.RealTimePositionVelocityCalculator
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.github.mattiadellepiane.gnssraw.ui.main.tabs.MeasurementFragment
import com.github.mattiadellepiane.gnssraw.ui.main.tabs.SettingsFragment
import com.github.mattiadellepiane.gnssraw.ui.main.tabs.FilesFragment
import com.github.mattiadellepiane.gnssraw.ui.main.tabs.MapsFragment
import com.github.mattiadellepiane.gnssraw.ui.main.SectionsPagerAdapter
import androidx.annotation.StringRes
import com.github.mattiadellepiane.gnssraw.MeasurementProvider
import com.github.mattiadellepiane.gnssraw.listeners.ServerCommunication
import java.lang.NumberFormatException
import java.lang.ClassCastException
import com.github.mattiadellepiane.gnssraw.data.SharedData.BillPughSingleton
import com.github.mattiadellepiane.gnssraw.utils.gnss.TimerValues
import android.widget.NumberPicker
import android.content.BroadcastReceiver
import com.github.mattiadellepiane.gnssraw.utils.gnss.DetectedActivitiesIntentReceiver
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.mattiadellepiane.gnssraw.listeners.MeasurementListener
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.PseudorangePositionVelocityFromRealTimeEvents
import android.os.HandlerThread
import com.github.mattiadellepiane.gnssraw.MainActivity
import android.location.LocationManager
import java.lang.Exception
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.GpsMathOperations
import android.location.GnssNavigationMessage
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.GpsTime
import org.joda.time.DateTime
import com.google.common.primitives.Longs
import org.joda.time.DateTimeZone
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.GpsMeasurement
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.EcefToTopocentricConverter.TopocentricAEDValues
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.EcefToTopocentricConverter
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.Ecef2LlaConverter.GeodeticLlaValues
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.Ecef2LlaConverter
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.Ecef2EnuConverter.EnuValues
import org.apache.commons.math3.linear.RealMatrix
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.Ecef2EnuConverter
import org.apache.commons.math3.linear.Array2DRowRealMatrix
import kotlin.Throws
import java.lang.ArithmeticException
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.Lla2EcefConverter
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.ElevationApiHelper
import java.net.HttpURLConnection
import java.net.URL
import java.io.InputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.GpsMeasurementWithRangeAndUncertainty
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.TroposphericModelEgnos.DryAndWetMappingValues
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.TroposphericModelEgnos
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.TroposphericModelEgnos.DryAndWetZenithDelays
import java.lang.IllegalArgumentException
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.GpsNavigationMessageStore.IntermediateEphemeris
import android.location.cts.nano.Ephemeris.IonosphericModelProto
import android.location.cts.nano.Ephemeris.GpsNavMessageProto
import android.location.cts.nano.Ephemeris.GpsEphemerisProto
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.GpsNavigationMessageStore.SubframeCheckResult
import java.lang.IllegalStateException
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.SatellitePositionCalculator.PositionAndVelocity
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.SatellitePositionCalculator.RangeAndRangeRate
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.SatellitePositionCalculator
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.SatelliteClockCorrectionCalculator.SatClockCorrection
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.SatelliteClockCorrectionCalculator
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.UserPositionVelocityWeightedLeastSquare.SatellitesPositionPseudorangesResidualAndCovarianceMatrix
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.ResidualCorrectionCalculator.SatelliteElevationAndResiduals
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.ResidualCorrectionCalculator
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.PseudorangeSmoother
import com.google.common.collect.Lists
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.UserPositionVelocityWeightedLeastSquare
import org.apache.commons.math3.linear.LUDecomposition
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.UserPositionVelocityWeightedLeastSquare.GpsTimeOfWeekAndWeekNumber
import org.apache.commons.math3.linear.QRDecomposition
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.PseudorangeNoSmoothingSmoother
import android.location.GnssClock
import java.net.UnknownHostException
import java.io.IOException
import android.location.cts.suplClient.SuplRrlpController
import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.PendingIntent
import android.os.IBinder
import java.lang.UnsupportedOperationException
import java.io.PrintWriter
import java.lang.StringBuilder
import java.net.Socket
import java.net.InetSocketAddress
import java.lang.Thread
import androidx.appcompat.app.AppCompatActivity
import com.github.mattiadellepiane.gnssraw.listeners.FileLogger
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import android.content.pm.PackageManager
import com.github.mattiadellepiane.gnssraw.SensorMeasurements
import android.location.LocationListener
import android.annotation.SuppressLint
import android.location.GnssMeasurementRequest

/**
 * A map fragment to show the computed least square position and the device computed position on
 * Google map.
 */
/*public class MapFragment extends Fragment implements OnMapReadyCallback {
  private static final float ZOOM_LEVEL = 15;
  private static final String TAG = "MapFragment";
  private RealTimePositionVelocityCalculator mPositionVelocityCalculator;

  private static final SimpleDateFormat DATE_SDF = new SimpleDateFormat("HH:mm:ss");

  // UI members
  private GoogleMap mMap; // Might be null if Google Play services APK is not available.
  private MapView mMapView;
  private final Set<Object> mSetOfFeatures = new HashSet<Object>();

  private Marker mLastLocationMarkerRaw = null;
  private Marker mLastLocationMarkerDevice = null;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.map_fragment, container, false);
    mMapView = ((MapView) rootView.findViewById(R.id.map));
    mMapView.onCreate(savedInstanceState);
    mMapView.getMapAsync(this);
    MapsInitializer.initialize(getActivity());

    RealTimePositionVelocityCalculator currentPositionVelocityCalculator =
        mPositionVelocityCalculator;
    if (currentPositionVelocityCalculator != null) {
      currentPositionVelocityCalculator.setMapFragment(this);
    }

    return rootView;
  }

  @Override
  public void onResume() {
    super.onResume();
    mMapView.onResume();
    if (mMap != null) {
      mMap.clear();
    }
    mLastLocationMarkerRaw = null;
    mLastLocationMarkerDevice = null;
  }

  @Override
  public void onPause() {
    mMapView.onPause();
    super.onPause();
  }

  @Override
  public void onDestroy() {
    mMapView.onDestroy();
    super.onDestroy();
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    mMap.setMyLocationEnabled(false);
    mMap.getUiSettings().setZoomControlsEnabled(true);
    mMap.getUiSettings().setZoomGesturesEnabled(true);
    mMap.getUiSettings().setMapToolbarEnabled(false);
  }

  public void setPositionVelocityCalculator(RealTimePositionVelocityCalculator value) {
    mPositionVelocityCalculator = value;
  }

  public void updateMapViewWithPositions(
      final double latDegRaw,
      final double lngDegRaw,
      final double latDegDevice,
      final double lngDegDevice,
      final long timeMillis) {
    Activity activity = getActivity();
    if (activity == null) {
      return;
    }
    activity.runOnUiThread(
        new Runnable() {
          @Override
          public void run() {
            Log.i(TAG, "onLocationChanged");
            LatLng latLngRaw = new LatLng(latDegRaw, lngDegRaw);
            LatLng latLngDevice = new LatLng(latDegDevice, lngDegDevice);
            if (mLastLocationMarkerRaw == null && mLastLocationMarkerDevice == null) {
              if (mMap != null) {
                mLastLocationMarkerDevice =
                    mMap.addMarker(
                        new MarkerOptions()
                            .position(latLngDevice)
                            .title(getResources().getString(R.string.title_device))
                            .icon(
                                BitmapDescriptorFactory.defaultMarker(
                                    BitmapDescriptorFactory.HUE_BLUE)));
                mLastLocationMarkerDevice.showInfoWindow();

                mLastLocationMarkerRaw =
                    mMap.addMarker(
                        new MarkerOptions()
                            .position(latLngRaw)
                            .title(getResources().getString(R.string.title_wls))
                            .icon(
                                BitmapDescriptorFactory.defaultMarker(
                                    BitmapDescriptorFactory.HUE_GREEN)));
                mLastLocationMarkerRaw.showInfoWindow();

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngRaw, ZOOM_LEVEL));
              }
            } else {
              mLastLocationMarkerRaw.setPosition(latLngRaw);
              mLastLocationMarkerDevice.setPosition(latLngDevice);
            }
            if (mLastLocationMarkerRaw == null && mLastLocationMarkerDevice == null) {
              String formattedDate = DATE_SDF.format(new Date(timeMillis));
              mLastLocationMarkerRaw.setTitle("time: " + formattedDate);
              mLastLocationMarkerDevice.showInfoWindow();

              mLastLocationMarkerRaw.setTitle("time: " + formattedDate);
              mLastLocationMarkerDevice.showInfoWindow();
            }
          }
        });
  }

  public void clearMarkers() {
    Activity activity = getActivity();
    if (activity == null) {
      return;
    }
    activity.runOnUiThread(
        new Runnable() {
          @Override
          public void run() {
            if (mLastLocationMarkerRaw != null) {
              mLastLocationMarkerRaw.remove();
              mLastLocationMarkerRaw = null;
            }
            if (mLastLocationMarkerDevice != null) {
              mLastLocationMarkerDevice.remove();
              mLastLocationMarkerDevice = null;
            }
          }
        });
  }
}*/