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
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.Spannable
import android.location.GnssStatus
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.GpsNavigationMessageStore
import org.achartengine.model.XYSeries
import org.achartengine.renderer.XYSeriesRenderer
import org.achartengine.util.MathHelper
import android.graphics.Paint.Align
import android.widget.ImageButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
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
import android.util.Pair
import com.google.common.base.Preconditions
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A simple class to represent time unit used by GPS.
 */
class GpsTime : Comparable<GpsTime> {
    /**
     * @return nanoseconds since GPS epoch.
     */
    // nanoseconds since GPS epoch (1980/1/6).
    var nanosSinceGpsEpoch: Long
        private set

    /**
     * Constructor for GpsTime. Input values are all in GPS time.
     * @param year Year
     * @param month Month from 1 to 12
     * @param day Day from 1 to 31
     * @param hour Hour from 0 to 23
     * @param minute Minute from 0 to 59
     * @param second Second from 0 to 59
     */
    constructor(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Double) {
        val utcDateTime = DateTime(year, month, day, hour, minute,
                second.toInt(), (second * 1000) as Int % 1000, UTC_ZONE)

        // Since input time is already specify in GPS time, no need to count leap second here.
        nanosSinceGpsEpoch = (TimeUnit.MILLISECONDS.toNanos(utcDateTime.millis)
                - GPS_UTC_EPOCH_OFFSET_NANOS)
    }

    /**
     * Constructor
     * @param dateTime is created using GPS time values.
     */
    constructor(dateTime: DateTime) {
        nanosSinceGpsEpoch = (TimeUnit.MILLISECONDS.toNanos(dateTime.millis)
                - GPS_UTC_EPOCH_OFFSET_NANOS)
    }

    /**
     * Constructor
     * @param gpsNanos nanoseconds since GPS epoch.
     */
    constructor(gpsNanos: Long) {
        nanosSinceGpsEpoch = gpsNanos
    }// JAVA/UNIX epoch: January 1, 1970 in msec
    // GPS epoch: January 6, 1980 in second
    /**
     * @return week count since GPS epoch, and second count since the beginning of
     * that week.
     */
    val gpsWeekSecond: Pair<Int, Int>
        get() {
            // JAVA/UNIX epoch: January 1, 1970 in msec
            // GPS epoch: January 6, 1980 in second
            val week = (nanosSinceGpsEpoch / NANOS_IN_WEEK).toInt()
            val second = TimeUnit.NANOSECONDS.toSeconds(nanosSinceGpsEpoch % NANOS_IN_WEEK).toInt()
            return Pair.create(week, second)
        }// UNIX epoch: January 1, 1970 in msec
    // GPS epoch: January 6, 1980 in second
    // 80 millis is 0.08 second.
    /**
     * @return week count since GPS epoch, and second count in 0.08 sec
     * resolution, 23-bit presentation (required by RRLP.)"
     */
    val gpsWeekTow23b: Pair<Int, Int>
        get() {
            // UNIX epoch: January 1, 1970 in msec
            // GPS epoch: January 6, 1980 in second
            val week = (nanosSinceGpsEpoch / NANOS_IN_WEEK).toInt()
            // 80 millis is 0.08 second.
            val tow23b = TimeUnit.NANOSECONDS.toMillis(nanosSinceGpsEpoch % NANOS_IN_WEEK).toInt() / 80
            return Pair.create(week, tow23b)
        }

    fun getBreakdownEpoch(vararg units: TimeUnit): LongArray {
        var nanos = nanosSinceGpsEpoch
        val values = LongArray(units.size)
        for (idx in 0 until units.size) {
            val unit = units[idx]
            val value = unit.convert(nanos, TimeUnit.NANOSECONDS)
            values[idx] = value
            nanos -= unit.toNanos(value)
        }
        return values
    }

    /**
     * @return milliseconds since JAVA/UNIX epoch.
     */
    val millisSinceJavaEpoch: Long
        get() = TimeUnit.NANOSECONDS.toMillis(nanosSinceGpsEpoch + GPS_UTC_EPOCH_OFFSET_NANOS)

    /**
     * @return milliseconds since GPS epoch.
     */
    val millisSinceGpsEpoch: Long
        get() = TimeUnit.NANOSECONDS.toMillis(nanosSinceGpsEpoch)

    /**
     * @return microseconds since GPS epoch.
     */
    val microsSinceGpsEpoch: Long
        get() = TimeUnit.NANOSECONDS.toMicros(nanosSinceGpsEpoch)

    /**
     * @return the GPS time in Calendar.
     */
    val timeInCalendar: Calendar
        get() = gpsDateTime.toGregorianCalendar()

    /**
     * @return a DateTime with leap seconds considered.
     */
    val utcDateTime: DateTime
        get() {
            val gpsDateTime = gpsDateTime
            return DateTime(
                    gpsDateTime.millis - TimeUnit.SECONDS.toMillis(getLeapSecond(gpsDateTime).toLong()), UTC_ZONE)
        }

    /**
     * @return a DateTime based on the pure GPS time (without considering leap second).
     */
    val gpsDateTime: DateTime
        get() = DateTime(TimeUnit.NANOSECONDS.toMillis(nanosSinceGpsEpoch
                + GPS_UTC_EPOCH_OFFSET_NANOS), UTC_ZONE)

    /**
     * Compares two `GpsTime` objects temporally.
     *
     * @param   other   the `GpsTime` to be compared.
     * @return  the value `0` if this `GpsTime` is simultaneous with
     * the argument `GpsTime`; a value less than `0` if this
     * `GpsTime` occurs before the argument `GpsTime`; and
     * a value greater than `0` if this `GpsTime` occurs
     * after the argument `GpsTime` (signed comparison).
     */
    override fun compareTo(other: GpsTime): Int {
        return java.lang.Long.compare(nanosSinceGpsEpoch, other.nanosSinceGpsEpoch)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is GpsTime) {
            return false
        }
        return nanosSinceGpsEpoch == other.nanosSinceGpsEpoch
    }

    override fun hashCode(): Int {
        return Longs.hashCode(nanosSinceGpsEpoch)
    }

    companion object {
        const val MILLIS_IN_SECOND = 1000
        const val SECONDS_IN_MINUTE = 60
        const val MINUTES_IN_HOUR = 60
        const val HOURS_IN_DAY = 24
        const val SECONDS_IN_DAY = HOURS_IN_DAY * MINUTES_IN_HOUR * SECONDS_IN_MINUTE
        const val DAYS_IN_WEEK = 7
        val MILLIS_IN_DAY = TimeUnit.DAYS.toMillis(1)
        val MILLIS_IN_WEEK = TimeUnit.DAYS.toMillis(7)
        val NANOS_IN_WEEK = TimeUnit.DAYS.toNanos(7)

        // GPS epoch is 1980/01/06
        const val GPS_DAYS_SINCE_JAVA_EPOCH: Long = 3657
        val GPS_UTC_EPOCH_OFFSET_SECONDS = TimeUnit.DAYS.toSeconds(GPS_DAYS_SINCE_JAVA_EPOCH)
        val GPS_UTC_EPOCH_OFFSET_NANOS = TimeUnit.SECONDS.toNanos(GPS_UTC_EPOCH_OFFSET_SECONDS)
        private val UTC_ZONE = DateTimeZone.UTC
        private val LEAP_SECOND_DATE_1981 = DateTime(1981, 7, 1, 0, 0, UTC_ZONE)
        private val LEAP_SECOND_DATE_2012 = DateTime(2012, 7, 1, 0, 0, UTC_ZONE)
        private val LEAP_SECOND_DATE_2015 = DateTime(2015, 7, 1, 0, 0, UTC_ZONE)
        private val LEAP_SECOND_DATE_2017 = DateTime(2017, 1, 1, 0, 0, UTC_ZONE)

        /**
         * Creates a GPS time using a UTC based date and time.
         * @param dateTime represents the current time in UTC time, must be after 2009
         */
        fun fromUtc(dateTime: DateTime): GpsTime {
            return GpsTime(TimeUnit.MILLISECONDS.toNanos(dateTime.millis)
                    + TimeUnit.SECONDS.toNanos(
                    getLeapSecond(dateTime) - GPS_UTC_EPOCH_OFFSET_SECONDS))
        }

        /**
         * Creates a GPS time based upon the current time.
         */
        fun now(): GpsTime {
            return fromUtc(DateTime.now(DateTimeZone.UTC))
        }

        /**
         * Creates a GPS time using absolute GPS week number, and the time of week.
         * @param gpsWeek
         * @param towSec GPS time of week in second
         * @return actual time in GpsTime.
         */
        fun fromWeekTow(gpsWeek: Int, towSec: Int): GpsTime {
            val nanos = gpsWeek * NANOS_IN_WEEK + TimeUnit.SECONDS.toNanos(towSec.toLong())
            return GpsTime(nanos)
        }

        /**
         * Creates a GPS time using YUMA GPS week number (0..1023), and the time of week.
         * @param yumaWeek (0..1023)
         * @param towSec GPS time of week in second
         * @return actual time in GpsTime.
         */
        fun fromYumaWeekTow(yumaWeek: Int, towSec: Int): GpsTime {
            Preconditions.checkArgument(yumaWeek >= 0)
            Preconditions.checkArgument(yumaWeek < 1024)

            // Estimate the multiplier of current week.
            val currentTime = DateTime.now(UTC_ZONE)
            val refTime = GpsTime(currentTime)
            val refWeekSec = refTime.gpsWeekSecond
            val weekMultiplier = refWeekSec.first / 1024
            val gpsWeek = weekMultiplier * 1024 + yumaWeek
            return fromWeekTow(gpsWeek, towSec)
        }

        fun fromTimeSinceGpsEpoch(gpsSec: Long): GpsTime {
            return GpsTime(TimeUnit.SECONDS.toNanos(gpsSec))
        }

        /**
         * Computes leap seconds. Only accurate after 2009.
         * @param time
         * @return number of leap seconds since GPS epoch.
         */
        fun getLeapSecond(time: DateTime?): Int {
            return if (LEAP_SECOND_DATE_2017.compareTo(time) <= 0) {
                18
            } else if (LEAP_SECOND_DATE_2015.compareTo(time) <= 0) {
                17
            } else if (LEAP_SECOND_DATE_2012.compareTo(time) <= 0) {
                16
            } else if (LEAP_SECOND_DATE_1981.compareTo(time) <= 0) {
                // Only correct between 2012/7/1 to 2008/12/31
                15
            } else {
                0
            }
        }

        /**
         * Computes GPS weekly epoch of the reference time.
         *
         * GPS weekly epoch are defined as of every Sunday 00:00:000 (mor
         * @param refTime reference time
         * @return nanoseconds since GPS epoch, for the week epoch.
         */
        fun getGpsWeekEpochNano(refTime: GpsTime): Long {
            val weekSecond = refTime.gpsWeekSecond
            return weekSecond.first * NANOS_IN_WEEK
        }// Since current is derived from UTC time, we need to add leap second here.

        /**
         * @return Day of year in GPS time (GMT time)
         */
        val currentDayOfYear: Int
            get() {
                val current = DateTime.now(DateTimeZone.UTC)
                // Since current is derived from UTC time, we need to add leap second here.
                val gpsTimeMillis = current.millis + getLeapSecond(current)
                val gpsCurrent = DateTime(gpsTimeMillis, UTC_ZONE)
                return gpsCurrent.dayOfYear
            }
    }
}