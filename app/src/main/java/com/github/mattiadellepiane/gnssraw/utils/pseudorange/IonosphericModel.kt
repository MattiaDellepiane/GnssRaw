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
//Adjusted imports of artifacts and package name
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
 * Calculates the Ionospheric correction of the pseudorange given the `userPosition`,
 * `satellitePosition`, `gpsTimeSeconds` and the ionospheric parameters sent by the
 * satellite `alpha` and `beta`
 *
 *
 * Source: http://www.navipedia.net/index.php/Klobuchar_Ionospheric_Model and
 * http://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=4104345 and
 * http://www.ion.org/museum/files/ACF2A4.pdf
 */
object IonosphericModel {
    /** Center frequency of the L1 band in Hz.  */
    const val L1_FREQ_HZ = 10.23 * 1e6 * 154

    /** Center frequency of the L2 band in Hz.  */
    const val L2_FREQ_HZ = 10.23 * 1e6 * 120

    /** Center frequency of the L5 band in Hz.  */
    const val L5_FREQ_HZ = 10.23 * 1e6 * 115
    private const val SECONDS_PER_DAY = 86400.0
    private const val PERIOD_OF_DELAY_THRESHOLD_SECONDS = 72000.0
    private const val IPP_LATITUDE_THRESHOLD_SEMI_CIRCLE = 0.416
    private const val DC_TERM = 5.0e-9
    private const val NORTH_GEOMAGNETIC_POLE_LONGITUDE_RADIANS = 5.08
    private const val GEOMETRIC_LATITUDE_CONSTANT = 0.064
    private const val DELAY_PHASE_TIME_CONSTANT_SECONDS = 50400
    private const val IONO_0_IDX = 0
    private const val IONO_1_IDX = 1
    private const val IONO_2_IDX = 2
    private const val IONO_3_IDX = 3

    /**
     * Calculates the Ionospheric correction of the pseudorange in seconds using the Klobuchar
     * Ionospheric model.
     */
    fun ionoKlobucharCorrectionSeconds(
            userPositionECEFMeters: DoubleArray,
            satellitePositionECEFMeters: DoubleArray?,
            gpsTOWSeconds: Double,
            alpha: DoubleArray,
            beta: DoubleArray,
            frequencyHz: Double): Double {
        val elevationAndAzimuthRadians = EcefToTopocentricConverter.calculateElAzDistBetween2Points(userPositionECEFMeters, satellitePositionECEFMeters)
        val elevationSemiCircle = elevationAndAzimuthRadians!!.elevationRadians / Math.PI
        val azimuthSemiCircle = elevationAndAzimuthRadians.azimuthRadians / Math.PI
        val latLngAlt = Ecef2LlaConverter.convertECEFToLLACloseForm(
                userPositionECEFMeters[0], userPositionECEFMeters[1], userPositionECEFMeters[2])
        val latitudeUSemiCircle = latLngAlt!!.latitudeRadians / Math.PI
        val longitudeUSemiCircle = latLngAlt.longitudeRadians / Math.PI

        // earth's centered angle (semi-circles)
        val earthCentredAngleSemiCircle = 0.0137 / (elevationSemiCircle + 0.11) - 0.022

        // latitude of the Ionospheric Pierce Point (IPP) (semi-circles)
        var latitudeISemiCircle = latitudeUSemiCircle + earthCentredAngleSemiCircle * Math.cos(azimuthSemiCircle * Math.PI)
        if (latitudeISemiCircle > IPP_LATITUDE_THRESHOLD_SEMI_CIRCLE) {
            latitudeISemiCircle = IPP_LATITUDE_THRESHOLD_SEMI_CIRCLE
        } else if (latitudeISemiCircle < -IPP_LATITUDE_THRESHOLD_SEMI_CIRCLE) {
            latitudeISemiCircle = -IPP_LATITUDE_THRESHOLD_SEMI_CIRCLE
        }

        // geodetic longitude of the Ionospheric Pierce Point (IPP) (semi-circles)
        val longitudeISemiCircle = longitudeUSemiCircle + earthCentredAngleSemiCircle
        * Math.sin(azimuthSemiCircle * Math.PI) / Math.cos(latitudeISemiCircle * Math.PI)

        // geomagnetic latitude of the Ionospheric Pierce Point (IPP) (semi-circles)
        val geomLatIPPSemiCircle = latitudeISemiCircle + GEOMETRIC_LATITUDE_CONSTANT
        * Math.cos(longitudeISemiCircle * Math.PI - NORTH_GEOMAGNETIC_POLE_LONGITUDE_RADIANS)

        // local time (sec) at the Ionospheric Pierce Point (IPP)
        var localTimeSeconds = SECONDS_PER_DAY / 2.0 * longitudeISemiCircle + gpsTOWSeconds
        localTimeSeconds %= SECONDS_PER_DAY
        if (localTimeSeconds < 0) {
            localTimeSeconds += SECONDS_PER_DAY
        }

        // amplitude of the ionospheric delay (seconds)
        var amplitudeOfDelaySeconds = alpha[IONO_0_IDX] + alpha[IONO_1_IDX] * geomLatIPPSemiCircle + alpha[IONO_2_IDX] * geomLatIPPSemiCircle * geomLatIPPSemiCircle + (alpha[IONO_3_IDX]
                * geomLatIPPSemiCircle * geomLatIPPSemiCircle * geomLatIPPSemiCircle)
        if (amplitudeOfDelaySeconds < 0) {
            amplitudeOfDelaySeconds = 0.0
        }

        // period of ionospheric delay
        var periodOfDelaySeconds = beta[IONO_0_IDX] + beta[IONO_1_IDX] * geomLatIPPSemiCircle + beta[IONO_2_IDX] * geomLatIPPSemiCircle * geomLatIPPSemiCircle + (beta[IONO_3_IDX]
                * geomLatIPPSemiCircle * geomLatIPPSemiCircle * geomLatIPPSemiCircle)
        if (periodOfDelaySeconds < PERIOD_OF_DELAY_THRESHOLD_SECONDS) {
            periodOfDelaySeconds = PERIOD_OF_DELAY_THRESHOLD_SECONDS
        }

        // phase of ionospheric delay
        val phaseOfDelayRadians = 2 * Math.PI * (localTimeSeconds - DELAY_PHASE_TIME_CONSTANT_SECONDS) / periodOfDelaySeconds

        // slant factor
        val slantFactor = 1.0 + 16.0 * Math.pow(0.53 - elevationSemiCircle, 3.0)

        // ionospheric time delay (seconds)
        var ionoDelaySeconds: Double
        ionoDelaySeconds = if (Math.abs(phaseOfDelayRadians) >= Math.PI / 2.0) {
            DC_TERM * slantFactor
        } else {
            (DC_TERM
                    + (1 - Math.pow(phaseOfDelayRadians, 2.0) / 2.0 + Math.pow(phaseOfDelayRadians, 4.0) / 24.0)
                    * amplitudeOfDelaySeconds) * slantFactor
        }

        // apply factor for frequency bands other than L1
        ionoDelaySeconds *= L1_FREQ_HZ * L1_FREQ_HZ / (frequencyHz * frequencyHz)
        return ionoDelaySeconds
    }
}