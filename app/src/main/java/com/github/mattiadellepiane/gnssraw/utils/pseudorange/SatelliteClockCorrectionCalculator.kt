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
//Adjusted package name
package com.github.mattiadellepiane.gnssraw.utils.pseudorange

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
 * Calculates the GPS satellite clock correction based on parameters observed from the navigation
 * message
 *
 * Source: Page 88 - 90 of the ICD-GPS 200
 */
object SatelliteClockCorrectionCalculator {
    private const val SPEED_OF_LIGHT_MPS = 299792458.0
    private const val EARTH_UNIVERSAL_GRAVITATIONAL_CONSTANT_M3_SM2 = 3.986005e14
    private const val RELATIVISTIC_CONSTANT_F = -4.442807633e-10
    private const val SECONDS_IN_WEEK = 604800
    private const val ACCURACY_TOLERANCE = 1.0e-11
    private const val MAX_ITERATIONS = 100

    /**
     * Computes the GPS satellite clock correction term in meters iteratively following page 88 - 90
     * and 98 - 100 of the ICD GPS 200. The method returns a pair of satellite clock correction in
     * meters and Kepler Eccentric Anomaly in Radians.
     *
     * @param ephemerisProto parameters of the navigation message
     * @param receiverGpsTowAtTimeOfTransmission Receiver estimate of GPS time of week when signal was
     * transmitted (seconds)
     * @param receiverGpsWeekAtTimeOfTransmission Receiver estimate of GPS week when signal was
     * transmitted (0-1024+)
     * @throws Exception
     */
    @Throws(Exception::class)
    fun calculateSatClockCorrAndEccAnomAndTkIteratively(
            ephemerisProto: GpsEphemerisProto?, receiverGpsTowAtTimeOfTransmission: Double,
            receiverGpsWeekAtTimeOfTransmission: Double): SatClockCorrection {
        // Units are not added in the variable names to have the same name as the ICD-GPS200
        // Mean anomaly (radians)
        var meanAnomalyRad: Double
        // Kepler's Equation for Eccentric Anomaly iteratively (Radians)
        var eccentricAnomalyRad: Double
        // Semi-major axis of orbit (meters)
        val a = ephemerisProto!!.rootOfA * ephemerisProto.rootOfA
        // Computed mean motion (radians/seconds)
        val n0 = Math.sqrt(EARTH_UNIVERSAL_GRAVITATIONAL_CONSTANT_M3_SM2 / (a * a * a))
        // Corrected mean motion (radians/seconds)
        val n = n0 + ephemerisProto.deltaN
        // In the following, Receiver GPS week and ephemeris GPS week are used to correct for week
        // rollover when calculating the time from clock reference epoch (tcSec)
        val timeOfTransmissionIncludingRxWeekSec = receiverGpsWeekAtTimeOfTransmission * SECONDS_IN_WEEK + receiverGpsTowAtTimeOfTransmission
        // time from clock reference epoch (seconds) page 88 ICD-GPS200
        var tcSec = (timeOfTransmissionIncludingRxWeekSec
                - (ephemerisProto.week * SECONDS_IN_WEEK + ephemerisProto.toc))
        // Correction for week rollover
        tcSec = fixWeekRollover(tcSec)
        var oldEccentricAnomalyRad = 0.0
        var newSatClockCorrectionSeconds = 0.0
        var relativisticCorrection = 0.0
        var changeInSatClockCorrection = 0.0
        // Initial satellite clock correction (unknown relativistic correction). Iterate to correct
        // with the relativistic effect and obtain a stable
        val initSatClockCorrectionSeconds = (ephemerisProto.af0
                + ephemerisProto.af1 * tcSec + ephemerisProto.af2 * tcSec * tcSec) - ephemerisProto.tgd
        var satClockCorrectionSeconds = initSatClockCorrectionSeconds
        var tkSec: Double
        var satClockCorrectionsCounter = 0
        do {
            var eccentricAnomalyCounter = 0
            // time from ephemeris reference epoch (seconds) page 98 ICD-GPS200
            tkSec = timeOfTransmissionIncludingRxWeekSec - (ephemerisProto.week * SECONDS_IN_WEEK + ephemerisProto.toe
                    + satClockCorrectionSeconds)
            // Correction for week rollover
            tkSec = fixWeekRollover(tkSec)
            // Mean anomaly (radians)
            meanAnomalyRad = ephemerisProto.m0 + n * tkSec
            // eccentric anomaly (radians)
            eccentricAnomalyRad = meanAnomalyRad
            // Iteratively solve for Kepler's eccentric anomaly according to ICD-GPS200 page 99
            do {
                oldEccentricAnomalyRad = eccentricAnomalyRad
                eccentricAnomalyRad = meanAnomalyRad + ephemerisProto.e * Math.sin(eccentricAnomalyRad)
                eccentricAnomalyCounter++
                if (eccentricAnomalyCounter > MAX_ITERATIONS) {
                    throw Exception("Kepler Eccentric Anomaly calculation did not converge in "
                            + MAX_ITERATIONS + " iterations")
                }
            } while (Math.abs(oldEccentricAnomalyRad - eccentricAnomalyRad) > ACCURACY_TOLERANCE)
            // relativistic correction term (seconds)
            relativisticCorrection = (RELATIVISTIC_CONSTANT_F * ephemerisProto.e
                    * ephemerisProto.rootOfA * Math.sin(eccentricAnomalyRad))
            // satellite clock correction including relativistic effect
            newSatClockCorrectionSeconds = initSatClockCorrectionSeconds + relativisticCorrection
            changeInSatClockCorrection = Math.abs(satClockCorrectionSeconds - newSatClockCorrectionSeconds)
            satClockCorrectionSeconds = newSatClockCorrectionSeconds
            satClockCorrectionsCounter++
            if (satClockCorrectionsCounter > MAX_ITERATIONS) {
                throw Exception("Satellite Clock Correction calculation did not converge in "
                        + MAX_ITERATIONS + " iterations")
            }
        } while (changeInSatClockCorrection > ACCURACY_TOLERANCE)
        tkSec = timeOfTransmissionIncludingRxWeekSec - (ephemerisProto.week * SECONDS_IN_WEEK + ephemerisProto.toe
                + satClockCorrectionSeconds)
        // return satellite clock correction (meters) and Kepler Eccentric Anomaly in Radians
        return SatClockCorrection(satClockCorrectionSeconds * SPEED_OF_LIGHT_MPS,
                eccentricAnomalyRad, tkSec)
    }

    /**
     * Calculates Satellite Clock Error Rate in (meters/second) by subtracting the Satellite
     * Clock Error Values at t+0.5s and t-0.5s.
     *
     *
     * This approximation is more accurate than differentiating because both the orbital
     * and relativity terms have non-linearities that are not easily differentiable.
     */
    @Throws(Exception::class)
    fun calculateSatClockCorrErrorRate(
            ephemerisProto: GpsEphemerisProto?, receiverGpsTowAtTimeOfTransmissionSeconds: Double,
            receiverGpsWeekAtTimeOfTransmission: Double): Double {
        val satClockCorrectionPlus = calculateSatClockCorrAndEccAnomAndTkIteratively(
                ephemerisProto, receiverGpsTowAtTimeOfTransmissionSeconds + 0.5,
                receiverGpsWeekAtTimeOfTransmission)
        val satClockCorrectionMinus = calculateSatClockCorrAndEccAnomAndTkIteratively(
                ephemerisProto, receiverGpsTowAtTimeOfTransmissionSeconds - 0.5,
                receiverGpsWeekAtTimeOfTransmission)
        return (satClockCorrectionPlus.satelliteClockCorrectionMeters
                - satClockCorrectionMinus.satelliteClockCorrectionMeters)
    }

    /**
     * Method to check for week rollover according to ICD-GPS 200 page 98.
     *
     *
     * Result should be between -302400 and 302400 if the ephemeris is within one week of
     * transmission, otherwise it is adjusted to the correct range
     */
    private fun fixWeekRollover(time: Double): Double {
        var correctedTime = time
        if (time > SECONDS_IN_WEEK / 2.0) {
            correctedTime = time - SECONDS_IN_WEEK
        }
        if (time < -SECONDS_IN_WEEK / 2.0) {
            correctedTime = time + SECONDS_IN_WEEK
        }
        return correctedTime
    }

    /**
     *
     * Class containing the satellite clock correction parameters: The satellite clock correction in
     * meters, Kepler Eccentric Anomaly in Radians and the time from the reference epoch in seconds.
     */
    class SatClockCorrection
    /**
     * Constructor
     */(
            /**
             * Satellite clock correction in meters
             */
            val satelliteClockCorrectionMeters: Double,
            /**
             * Kepler Eccentric Anomaly in Radians
             */
            val eccentricAnomalyRadians: Double,
            /**
             * Time from the reference epoch in Seconds
             */
            val timeFromRefEpochSec: Double)
}