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
import com.google.common.base.Preconditions
import java.util.concurrent.TimeUnit

/** A representation of a time as "hours:minutes:seconds"  */
class TimerValues(hours: Int, minutes: Int, seconds: Int) {
    private var mHours: Int
    private var mMinutes: Int
    private var mSeconds: Int

    /**
     * Creates a [TimerValues]
     *
     * @param milliseconds The number of milliseconds to represent
     */
    constructor(milliseconds: Long) : this(
            0 /* hours */,
            0 /* minutes */,
            TimeUnit.SECONDS.convert(milliseconds, TimeUnit.MILLISECONDS).toInt()) {
    }

    /** Returns a [Bundle] from the [TimerValues]  */
    fun toBundle(): Bundle {
        val content = Bundle()
        content.putInt(HOURS, mHours)
        content.putInt(MINUTES, mMinutes)
        content.putInt(SECONDS, mSeconds)
        return content
    }

    /**
     * Configures a [NumberPicker] with appropriate bounds and initial value for displaying
     * "Hours"
     */
    fun configureHours(picker: NumberPicker) {
        picker.minValue = 0
        picker.maxValue = TimeUnit.HOURS.convert(1, TimeUnit.DAYS).toInt() - 1
        picker.value = mHours
    }

    /**
     * Configures a [NumberPicker] with appropriate bounds and initial value for displaying
     * "Minutes"
     */
    fun configureMinutes(picker: NumberPicker) {
        picker.minValue = 0
        picker.maxValue = TimeUnit.MINUTES.convert(1, TimeUnit.HOURS).toInt() - 1
        picker.value = mMinutes
    }

    /**
     * Configures a [NumberPicker] with appropriate bounds and initial value for displaying
     * "Seconds"
     */
    fun configureSeconds(picker: NumberPicker) {
        picker.minValue = 0
        picker.maxValue = TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES).toInt() - 1
        picker.value = mSeconds
    }

    /** Returns the [TimerValues] in milliseconds  */
    val totalMilliseconds: Long
        get() = (TimeUnit.MILLISECONDS.convert(mHours.toLong(), TimeUnit.HOURS)
                + TimeUnit.MILLISECONDS.convert(mMinutes.toLong(), TimeUnit.MINUTES)
                + TimeUnit.MILLISECONDS.convert(mSeconds.toLong(), TimeUnit.SECONDS))

    /** Returns `true` if [TimerValues] is zero.  */
    val isZero: Boolean
        get() = mHours == 0 && mMinutes == 0 && mSeconds == 0

    /** Returns string representation that includes "00:00:00"  */
    fun toCountdownString(): String {
        return String.format("%02d:%02d:%02d", mHours, mMinutes, mSeconds)
    }

    /** Normalize seconds and minutes  */
    private fun normalizeValues() {
        val minuteOverflow = TimeUnit.MINUTES.convert(mSeconds.toLong(), TimeUnit.SECONDS)
        val hourOverflow = TimeUnit.HOURS.convert(mMinutes.toLong(), TimeUnit.MINUTES)

        // Apply overflow
        mMinutes += minuteOverflow.toInt()
        mHours += hourOverflow.toInt()

        // Apply bounds
        mSeconds -= TimeUnit.SECONDS.convert(minuteOverflow, TimeUnit.MINUTES).toInt()
        mMinutes -= TimeUnit.MINUTES.convert(hourOverflow, TimeUnit.HOURS).toInt()
    }

    override fun toString(): String {
        return if (isZero) {
            EMPTY
        } else {
            toCountdownString()
        }
    }

    companion object {
        private const val EMPTY = "N/A"
        private const val HOURS = "hours"
        private const val MINUTES = "minutes"
        private const val SECONDS = "seconds"

        /** Creates a [TimerValues] from a [Bundle]  */
        fun fromBundle(bundle: Bundle?): TimerValues {
            Preconditions.checkArgument(bundle != null, "Bundle is null")
            return TimerValues(
                    bundle!!.getInt(HOURS, 0), bundle.getInt(MINUTES, 0), bundle.getInt(SECONDS, 0))
        }
    }

    /**
     * Creates a [TimerValues]
     *
     * @param hours The number of hours to represent
     * @param minutes The number of minutes to represent
     * @param seconds The number of seconds to represent
     */
    init {
        Preconditions.checkArgument(hours >= 0, "Hours is negative: %s", hours)
        Preconditions.checkArgument(minutes >= 0, "Minutes is negative: %s", minutes)
        Preconditions.checkArgument(seconds >= 0, "Seconds is negative: %s", seconds)
        mHours = hours
        mMinutes = minutes
        mSeconds = seconds
        normalizeValues()
    }
}