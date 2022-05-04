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
//Removed code unused by my project, changed imports and package name
package com.github.mattiadellepiane.gnssraw.utils.gnss

import android.view.LayoutInflater
import android.view.ViewGroup
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
import java.util.ArrayList
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.Spannable
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
import com.github.mattiadellepiane.gnssraw.MainActivity
import java.lang.Exception
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.GpsMathOperations
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
import java.net.UnknownHostException
import java.io.IOException
import android.location.cts.suplClient.SuplRrlpController
import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.PendingIntent
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
import android.annotation.SuppressLint
import android.location.*
import android.os.*
import android.util.Log
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

/**
 * A class that handles real time position and velocity calculation, passing [ ] instances to the [PseudorangePositionVelocityFromRealTimeEvents]
 * whenever a new raw measurement is received in order to compute a new position solution. The
 * computed position and velocity solutions are passed to the {link ResultFragment} to be
 * visualized.
 */
class RealTimePositionVelocityCalculator : MeasurementListener() {
    private var mPseudorangePositionVelocityFromRealTimeEvents: PseudorangePositionVelocityFromRealTimeEvents? = null
    private val mPositionVelocityCalculationHandlerThread: HandlerThread
    private val mMyPositionVelocityCalculationHandler: Handler
    private var mCurrentColor = Color.rgb(0x4a, 0x5f, 0x70)
    private var mCurrentColorIndex = 0
    private var mAllowShowingRawResults = false

    //private MapFragment mMapFragment;
    private var mMainActivity: MainActivity? = null
    private var mPlotFragment: PlotFragment? = null
    private val mRgbColorArray = intArrayOf(
            Color.rgb(0x4a, 0x5f, 0x70),
            Color.rgb(0x7f, 0x82, 0x5f),
            Color.rgb(0xbf, 0x90, 0x76),
            Color.rgb(0x82, 0x4e, 0x4e),
            Color.rgb(0x66, 0x77, 0x7d)
    )
    private var mResidualPlotStatus = 0
    private var mGroundTruth: DoubleArray? = null
    private var mPositionSolutionCount = 0

    /**
     * Update the reference location in [PseudorangePositionVelocityFromRealTimeEvents] if the
     * received location is a network location. Otherwise, update the {link ResultFragment} to
     * visualize both GPS location computed by the device and the one computed from the raw data.
     */
    override fun onLocationChanged(location: Location) {
        if (!SharedData.Companion.getInstance().isListeningForMeasurements()) return
        if (location.provider == LocationManager.NETWORK_PROVIDER) {
            val r = Runnable {
                if (mPseudorangePositionVelocityFromRealTimeEvents == null) {
                    return@Runnable
                }
                try {
                    mPseudorangePositionVelocityFromRealTimeEvents.setReferencePosition(
                            (location.latitude * 1E7).toInt(),
                            (location.longitude * 1E7).toInt(),
                            (location.altitude * 1E7).toInt())
                } catch (e: Exception) {
                    Log.e("prova", " Exception setting reference location : ", e)
                }
            }
            mMyPositionVelocityCalculationHandler.post(r)
        } else if (location.provider == LocationManager.GPS_PROVIDER) {
            if (mAllowShowingRawResults) {
                val r = Runnable {
                    if (mPseudorangePositionVelocityFromRealTimeEvents == null) {
                        return@Runnable
                    }
                    val posSolution = mPseudorangePositionVelocityFromRealTimeEvents.getPositionSolutionLatLngDeg()
                    val velSolution = mPseudorangePositionVelocityFromRealTimeEvents.getVelocitySolutionEnuMps()
                    val pvUncertainty = mPseudorangePositionVelocityFromRealTimeEvents
                            .getPositionVelocityUncertaintyEnu()
                    if (java.lang.Double.isNaN(posSolution!![0])) {
                        logPositionFromRawDataEvent("No Position Calculated Yet")
                        logPositionError("And no offset calculated yet...")
                    } else {
                        if (mResidualPlotStatus != RESIDUAL_MODE_DISABLED
                                && mResidualPlotStatus != RESIDUAL_MODE_AT_INPUT_LOCATION) {
                            updateGroundTruth(posSolution)
                        }
                        val formattedLatDegree = DecimalFormat("##.######").format(posSolution!![0])
                        val formattedLngDegree = DecimalFormat("##.######").format(posSolution!![1])
                        val formattedAltMeters = DecimalFormat("##.#").format(posSolution!![2])
                        logPositionFromRawDataEvent(
                                "latDegrees = "
                                        + formattedLatDegree
                                        + " lngDegrees = "
                                        + formattedLngDegree
                                        + "altMeters = "
                                        + formattedAltMeters)
                        val formattedVelocityEastMps = DecimalFormat("##.###").format(velSolution!![0])
                        val formattedVelocityNorthMps = DecimalFormat("##.###").format(velSolution!![1])
                        val formattedVelocityUpMps = DecimalFormat("##.###").format(velSolution!![2])
                        logVelocityFromRawDataEvent(
                                "Velocity East = "
                                        + formattedVelocityEastMps
                                        + "mps"
                                        + " Velocity North = "
                                        + formattedVelocityNorthMps
                                        + "mps"
                                        + "Velocity Up = "
                                        + formattedVelocityUpMps
                                        + "mps")
                        val formattedPosUncertaintyEastMeters = DecimalFormat("##.###").format(pvUncertainty!![0])
                        val formattedPosUncertaintyNorthMeters = DecimalFormat("##.###").format(pvUncertainty!![1])
                        val formattedPosUncertaintyUpMeters = DecimalFormat("##.###").format(pvUncertainty!![2])
                        logPositionUncertainty(
                                "East = "
                                        + formattedPosUncertaintyEastMeters
                                        + "m North = "
                                        + formattedPosUncertaintyNorthMeters
                                        + "m Up = "
                                        + formattedPosUncertaintyUpMeters
                                        + "m")
                        val formattedVelUncertaintyEastMeters = DecimalFormat("##.###").format(pvUncertainty!![3])
                        val formattedVelUncertaintyNorthMeters = DecimalFormat("##.###").format(pvUncertainty!![4])
                        val formattedVelUncertaintyUpMeters = DecimalFormat("##.###").format(pvUncertainty!![5])
                        logVelocityUncertainty(
                                "East = "
                                        + formattedVelUncertaintyEastMeters
                                        + "mps North = "
                                        + formattedVelUncertaintyNorthMeters
                                        + "mps Up = "
                                        + formattedVelUncertaintyUpMeters
                                        + "mps")
                        val formattedOffsetMeters = DecimalFormat("##.######")
                                .format(
                                        getDistanceMeters(
                                                location.latitude,
                                                location.longitude,
                                                posSolution!![0],
                                                posSolution!![1]))
                        logPositionError("position offset = $formattedOffsetMeters meters")
                        val formattedSpeedOffsetMps = DecimalFormat("##.###")
                                .format(
                                        Math.abs(location.speed
                                                - Math.sqrt(
                                                Math.pow(velSolution!![0], 2.0)
                                                        + Math.pow(velSolution!![1], 2.0))))
                        logVelocityError("speed offset = $formattedSpeedOffsetMps mps")
                    }
                    logLocationEvent("onLocationChanged: $location")
                    /*if (!Double.isNaN(posSolution[0])) {
                            updateMapViewWithPositions(
                                posSolution[0],
                                posSolution[1],
                                location.getLatitude(),
                                location.getLongitude(),
                                location.getTime());
                          } else {
                            clearMapMarkers();
                          }*/
                }
                mMyPositionVelocityCalculationHandler.post(r)
            }
        }
    }

    /*private void clearMapMarkers() {
    mMapFragment.clearMarkers();
  }

  private void updateMapViewWithPositions(
      double latDegRaw,
      double lngDegRaw,
      double latDegDevice,
      double lngDegDevice,
      long timeMillis) {
    mMapFragment.updateMapViewWithPositions(
        latDegRaw, lngDegRaw, latDegDevice, lngDegDevice, timeMillis);
  }*/
    override fun onGnssMeasurementsReceived(event: GnssMeasurementsEvent) {
        if (!SharedData.Companion.getInstance().isListeningForMeasurements()) return
        mAllowShowingRawResults = true
        val r = Runnable {
            mMainActivity!!.runOnUiThread { if (SharedData.Companion.getInstance().getPlotFragment() != null) SharedData.Companion.getInstance().getPlotFragment().updateCnoTab(event) }
            if (mPseudorangePositionVelocityFromRealTimeEvents == null) {
                return@Runnable
            }
            try {
                if (mResidualPlotStatus != RESIDUAL_MODE_DISABLED
                        && mResidualPlotStatus != RESIDUAL_MODE_AT_INPUT_LOCATION) {
                    // The position at last epoch is used for the residual analysis.
                    // This is happening by updating the ground truth for pseudorange before using the
                    // new arriving pseudoranges to compute a new position.
                    mPseudorangePositionVelocityFromRealTimeEvents
                            .setCorrectedResidualComputationTruthLocationLla(mGroundTruth)
                }
                mPseudorangePositionVelocityFromRealTimeEvents
                        .computePositionVelocitySolutionsFromRawMeas(event)
                // Running on main thread instead of in parallel will improve the thread safety
                if (mResidualPlotStatus != RESIDUAL_MODE_DISABLED) {
                    mMainActivity!!.runOnUiThread {
                        if (SharedData.Companion.getInstance().getPlotFragment() != null) SharedData.Companion.getInstance().getPlotFragment().updatePseudorangeResidualTab(
                                mPseudorangePositionVelocityFromRealTimeEvents
                                        .getPseudorangeResidualsMeters(),
                                TimeUnit.NANOSECONDS.toSeconds(
                                        event.clock.timeNanos).toDouble())
                    }
                } else {
                    mMainActivity!!.runOnUiThread { // Here we create gaps when the residual plot is disabled
                        if (SharedData.Companion.getInstance().getPlotFragment() != null) SharedData.Companion.getInstance().getPlotFragment().updatePseudorangeResidualTab(
                                GpsMathOperations.createAndFillArray(
                                        GpsNavigationMessageStore.Companion.MAX_NUMBER_OF_SATELLITES, Double.NaN),
                                TimeUnit.NANOSECONDS.toSeconds(
                                        event.clock.timeNanos).toDouble())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mMyPositionVelocityCalculationHandler.post(r)
    }

    override fun onGnssNavigationMessageReceived(event: GnssNavigationMessage) {
        if (!SharedData.Companion.getInstance().isListeningForMeasurements()) return
        if (event.type == GnssNavigationMessage.TYPE_GPS_L1CA) {
            mPseudorangePositionVelocityFromRealTimeEvents!!.parseHwNavigationMessageUpdates(event)
        }
    }

    private fun logEvent(tag: String, message: String, color: Int) {
        Log.d(/*MeasurementProvider.TAG + */tag, message)
        logText(tag, message, color)
    }

    private fun logText(tag: String, text: String, color: Int) {
        //UIResultComponent component = getUiResultComponent();
        //if (component != null) {
        //component.logTextResults(tag, text, color);
        //}
    }

    fun logLocationEvent(event: String) {
        mCurrentColor = nextColor
        logEvent("Location", event, mCurrentColor)
    }

    private fun logPositionFromRawDataEvent(event: String) {
        logEvent("Calculated Position From Raw Data", """
     $event
     
     """.trimIndent(), mCurrentColor)
    }

    private fun logVelocityFromRawDataEvent(event: String) {
        logEvent("Calculated Velocity From Raw Data", """
     $event
     
     """.trimIndent(), mCurrentColor)
    }

    private fun logPositionError(event: String) {
        logEvent("Offset between the reported position and Google's WLS position based on reported "
                + "measurements",
                """
                    $event
                    
                    """.trimIndent(),
                mCurrentColor)
    }

    private fun logVelocityError(event: String) {
        logEvent("Offset between the reported velocity and "
                + "Google's computed velocity based on reported measurements ",
                """
                    $event
                    
                    """.trimIndent(),
                mCurrentColor)
    }

    private fun logPositionUncertainty(event: String) {
        logEvent("Uncertainty of the calculated position from Raw Data", """
     $event
     
     """.trimIndent(), mCurrentColor)
    }

    private fun logVelocityUncertainty(event: String) {
        logEvent("Uncertainty of the calculated velocity from Raw Data", """
     $event
     
     """.trimIndent(), mCurrentColor)
    }

    @get:Synchronized
    private val nextColor: Int
        private get() {
            ++mCurrentColorIndex
            return mRgbColorArray[mCurrentColorIndex % mRgbColorArray.size]
        }

    /**
     * Return the distance (measured along the surface of the sphere) between 2 points
     */
    fun getDistanceMeters(
            lat1Degree: Double, lng1Degree: Double, lat2Degree: Double, lng2Degree: Double): Double {
        val deltaLatRadian = Math.toRadians(lat2Degree - lat1Degree)
        val deltaLngRadian = Math.toRadians(lng2Degree - lng1Degree)
        val a = (Math.sin(deltaLatRadian / 2) * Math.sin(deltaLatRadian / 2)
                + (Math.cos(Math.toRadians(lat1Degree))
                * Math.cos(Math.toRadians(lat2Degree))
                * Math.sin(deltaLngRadian / 2)
                * Math.sin(deltaLngRadian / 2)))
        val angularDistanceRad = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return EARTH_RADIUS_METERS * angularDistanceRad
    }

    /**
     * Update the ground truth for pseudorange residual analysis based on the user activity.
     */
    @Synchronized
    private fun updateGroundTruth(posSolution: DoubleArray?) {

        // In case of switching between modes, last ground truth from previous mode will be used.
        if (mGroundTruth == null) {
            // If mGroundTruth has not been initialized, we set it to be the same as position solution
            mGroundTruth = doubleArrayOf(0.0, 0.0, 0.0)
            mGroundTruth!![0] = posSolution!![0]
            mGroundTruth!![1] = posSolution[1]
            mGroundTruth!![2] = posSolution[2]
        } else if (mResidualPlotStatus == RESIDUAL_MODE_STILL) {
            // If the user is standing still, we average our WLS position solution
            // Reference: https://en.wikipedia.org/wiki/Moving_average#Cumulative_moving_average
            mGroundTruth!![0] = ((mGroundTruth!![0] * mPositionSolutionCount + posSolution!![0])
                    / (mPositionSolutionCount + 1))
            mGroundTruth!![1] = ((mGroundTruth!![1] * mPositionSolutionCount + posSolution[1])
                    / (mPositionSolutionCount + 1))
            mGroundTruth!![2] = ((mGroundTruth!![2] * mPositionSolutionCount + posSolution[2])
                    / (mPositionSolutionCount + 1))
            mPositionSolutionCount++
        } else if (mResidualPlotStatus == RESIDUAL_MODE_MOVING) {
            // If the user is moving fast, we use single WLS position solution
            mGroundTruth!![0] = posSolution!![0]
            mGroundTruth!![1] = posSolution[1]
            mGroundTruth!![2] = posSolution[2]
            mPositionSolutionCount = 0
        }
    }
    /**
     * Sets [MapFragment] for receiving WLS location update
     */
    /*public void setMapFragment(MapFragment mapFragment) {
    this.mMapFragment = mapFragment;
  }*/
    /**
     * Sets {link PlotFragment} for receiving Gnss measurement and residual computation results for
     * plot
     */
    fun setPlotFragment(plotFragment: PlotFragment?) {
        mPlotFragment = plotFragment
    }

    /**
     * Sets [MainActivity] for running some UI tasks on UI thread
     */
    fun setMainActivity(mainActivity: MainActivity?) {
        mMainActivity = mainActivity
    }

    /**
     * Sets the ground truth mode in [PseudorangePositionVelocityFromRealTimeEvents]
     * for calculating corrected pseudorange residuals, also logs the change in ResultFragment
     */
    fun setResidualPlotMode(residualPlotStatus: Int, fixedGroundTruth: DoubleArray?) {
        mResidualPlotStatus = residualPlotStatus
        if (mPseudorangePositionVelocityFromRealTimeEvents == null) {
            return
        }
        when (mResidualPlotStatus) {
            RESIDUAL_MODE_MOVING -> {
                mPseudorangePositionVelocityFromRealTimeEvents
                        .setCorrectedResidualComputationTruthLocationLla(mGroundTruth)
                logEvent("Residual Plot", "Mode is set to moving", mCurrentColor)
            }
            RESIDUAL_MODE_STILL -> {
                mPseudorangePositionVelocityFromRealTimeEvents
                        .setCorrectedResidualComputationTruthLocationLla(mGroundTruth)
                logEvent("Residual Plot", "Mode is set to still", mCurrentColor)
            }
            RESIDUAL_MODE_AT_INPUT_LOCATION -> {
                mPseudorangePositionVelocityFromRealTimeEvents
                        .setCorrectedResidualComputationTruthLocationLla(fixedGroundTruth)
                logEvent("Residual Plot", "Mode is set to fixed ground truth", mCurrentColor)
            }
            RESIDUAL_MODE_DISABLED -> {
                mGroundTruth = null
                mPseudorangePositionVelocityFromRealTimeEvents
                        .setCorrectedResidualComputationTruthLocationLla(mGroundTruth)
                logEvent("Residual Plot", "Mode is set to Disabled", mCurrentColor)
            }
            else -> mPseudorangePositionVelocityFromRealTimeEvents
                    .setCorrectedResidualComputationTruthLocationLla(null)
        }
    }

    override fun write(s: String?) {}
    override fun initResources() {}
    override fun releaseResources() {}

    companion object {
        /** Residual analysis where user disabled residual plots  */
        const val RESIDUAL_MODE_DISABLED = -1

        /** Residual analysis where the user is not moving  */
        const val RESIDUAL_MODE_STILL = 0

        /** Residual analysis where the user is moving  */
        const val RESIDUAL_MODE_MOVING = 1

        /**
         * Residual analysis where the user chose to enter a LLA input as their position
         */
        const val RESIDUAL_MODE_AT_INPUT_LOCATION = 2
        private const val EARTH_RADIUS_METERS: Long = 6371000
    }

    init {
        mPositionVelocityCalculationHandlerThread = HandlerThread("Position From Realtime Pseudoranges")
        mPositionVelocityCalculationHandlerThread.start()
        mMyPositionVelocityCalculationHandler = Handler(mPositionVelocityCalculationHandlerThread.looper)
        val r = Runnable {
            try {
                mPseudorangePositionVelocityFromRealTimeEvents = PseudorangePositionVelocityFromRealTimeEvents()
            } catch (e: Exception) {
                Log.e(
                        "prova",
                        " Exception in constructing PseudorangePositionFromRealTimeEvents : ",
                        e)
            }
        }
        mMyPositionVelocityCalculationHandler.post(r)
    }
}