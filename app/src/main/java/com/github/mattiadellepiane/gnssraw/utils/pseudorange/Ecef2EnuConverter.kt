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
 * Converts ECEF (Earth Centered Earth Fixed) Cartesian coordinates to local ENU (East, North,
 * and Up).
 *
 *
 *  Source: reference from Navipedia:
 * http://www.navipedia.net/index.php/Transformations_between_ECEF_and_ENU_coordinates
 */
object Ecef2EnuConverter {
    /**
     * Converts a vector represented by coordinates ecefX, ecefY, ecefZ in an
     * Earth-Centered Earth-Fixed (ECEF) Cartesian system into a vector in a
     * local east-north-up (ENU) Cartesian system.
     *
     *
     *  For example it can be used to rotate a speed vector or position offset vector to ENU.
     *
     * @param ecefX X coordinates in ECEF
     * @param ecefY Y coordinates in ECEF
     * @param ecefZ Z coordinates in ECEF
     * @param refLat Latitude in Radians of the Reference Position
     * @param refLng Longitude in Radians of the Reference Position
     * @return the converted values in `EnuValues`
     */
    fun convertEcefToEnu(ecefX: Double, ecefY: Double, ecefZ: Double,
                         refLat: Double, refLng: Double): EnuValues {
        val rotationMatrix = getRotationMatrix(refLat, refLng)
        val ecefCoordinates: RealMatrix = Array2DRowRealMatrix(doubleArrayOf(ecefX, ecefY, ecefZ))
        val enuResult = rotationMatrix.multiply(ecefCoordinates)
        return EnuValues(enuResult.getEntry(0, 0),
                enuResult.getEntry(1, 0), enuResult.getEntry(2, 0))
    }

    /**
     * Computes a rotation matrix for converting a vector in Earth-Centered Earth-Fixed (ECEF)
     * Cartesian system into a vector in local east-north-up (ENU) Cartesian system with respect to
     * a reference location. The matrix has the following content:
     *
     * - sinLng                     cosLng            0
     * - sinLat * cosLng      - sinLat * sinLng      cosLat
     * cosLat * cosLng        cosLat * sinLng      sinLat
     *
     *
     *  Reference: Pratap Misra and Per Enge
     * "Global Positioning System: Signals, Measurements, and Performance" Page 137.
     *
     * @param refLat Latitude of reference location
     * @param refLng Longitude of reference location
     * @return the Ecef to Enu rotation matrix
     */
    fun getRotationMatrix(refLat: Double, refLng: Double): RealMatrix {
        val rotationMatrix: RealMatrix = Array2DRowRealMatrix(3, 3)

        // Fill in the rotation Matrix
        rotationMatrix.setEntry(0, 0, -1 * Math.sin(refLng))
        rotationMatrix.setEntry(1, 0, -1 * Math.cos(refLng) * Math.sin(refLat))
        rotationMatrix.setEntry(2, 0, Math.cos(refLng) * Math.cos(refLat))
        rotationMatrix.setEntry(0, 1, Math.cos(refLng))
        rotationMatrix.setEntry(1, 1, -1 * Math.sin(refLat) * Math.sin(refLng))
        rotationMatrix.setEntry(2, 1, Math.cos(refLat) * Math.sin(refLng))
        rotationMatrix.setEntry(0, 2, 0.0)
        rotationMatrix.setEntry(1, 2, Math.cos(refLat))
        rotationMatrix.setEntry(2, 2, Math.sin(refLat))
        return rotationMatrix
    }

    /**
     * A container for values in ENU (East, North, Up) coordination system.
     */
    class EnuValues
    /**
     * Constructor
     */(
            /**
             * East Coordinates in local ENU
             */
            val enuEast: Double,
            /**
             * North Coordinates in local ENU
             */
            val enuNorth: Double,
            /**
             * Up Coordinates in local ENU
             */
            val enuUP: Double)
}