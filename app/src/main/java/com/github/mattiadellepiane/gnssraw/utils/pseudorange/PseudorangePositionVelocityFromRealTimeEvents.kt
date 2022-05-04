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
import android.util.Log
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Helper class for calculating Gps position and velocity solution using weighted least squares
 * where the raw Gps measurements are parsed as a [BufferedReader] with the option to apply
 * doppler smoothing, carrier phase smoothing or no smoothing.
 *
 */
class PseudorangePositionVelocityFromRealTimeEvents {
    private var mHardwareGpsNavMessageProto: GpsNavMessageProto? = null

    // navigation message parser
    private val mGpsNavigationMessageStore = GpsNavigationMessageStore()
    /** Returns the last computed weighted least square position solution  */
    var positionSolutionLatLngDeg = GpsMathOperations.createAndFillArray(3, Double.NaN)
        private set
    /** Returns the last computed Velocity solution  */
    var velocitySolutionEnuMps = GpsMathOperations.createAndFillArray(3, Double.NaN)
        private set

    /**
     * Returns the last computed position and velocity uncertainties in meters and meter per seconds,
     * respectively.
     */
    val positionVelocityUncertaintyEnu = GpsMathOperations.createAndFillArray(6, Double.NaN)

    /**
     * Returns the pseudorange residuals corrected by using clock bias computed from highest
     * elevationDegree satellites.
     */
    var pseudorangeResidualsMeters = GpsMathOperations.createAndFillArray(
            GpsNavigationMessageStore.Companion.MAX_NUMBER_OF_SATELLITES, Double.NaN)
        private set
    private var mFirstUsefulMeasurementSet = true
    private var mReferenceLocation: IntArray? = null
    private var mLastReceivedSuplMessageTimeMillis: Long = 0
    private val mDeltaTimeMillisToMakeSuplRequest = TimeUnit.MINUTES.toMillis(30)
    private var mFirstSuplRequestNeeded = true
    private var mGpsNavMessageProtoUsed: GpsNavMessageProto? = null

    // Only the interface of pseudorange smoother is provided. Please implement customized smoother.
    var mPseudorangeSmoother: PseudorangeSmoother = PseudorangeNoSmoothingSmoother()
    private val mUserPositionVelocityLeastSquareCalculator = UserPositionVelocityWeightedLeastSquare(mPseudorangeSmoother)
    private val mUsefulSatellitesToReceiverMeasurements = arrayOfNulls<GpsMeasurement>(GpsNavigationMessageStore.Companion.MAX_NUMBER_OF_SATELLITES)
    private val mUsefulSatellitesToTowNs = arrayOfNulls<Long>(GpsNavigationMessageStore.Companion.MAX_NUMBER_OF_SATELLITES)
    private var mLargestTowNs = Long.MIN_VALUE
    private var mArrivalTimeSinceGPSWeekNs = 0.0
    private var mDayOfYear1To366 = 0
    private var mGpsWeekNumber = 0
    private var mArrivalTimeSinceGpsEpochNs: Long = 0

    /**
     * Computes Weighted least square position and velocity solutions from a received [ ] and store the result in [ ][this.positionSolutionLatLngDeg] and [ ][this.velocitySolutionEnuMps]
     */
    @Throws(Exception::class)
    fun computePositionVelocitySolutionsFromRawMeas(event: GnssMeasurementsEvent) {
        if (mReferenceLocation == null) {
            // If no reference location is received, we can not get navigation message from SUPL and hence
            // we will not try to compute location.
            Log.d(TAG, " No reference Location ..... no position is calculated")
            return
        }
        for (i in 0 until GpsNavigationMessageStore.Companion.MAX_NUMBER_OF_SATELLITES) {
            mUsefulSatellitesToReceiverMeasurements[i] = null
            mUsefulSatellitesToTowNs[i] = null
        }
        val gnssClock = event.clock
        mArrivalTimeSinceGpsEpochNs = gnssClock.timeNanos - gnssClock.fullBiasNanos
        for (measurement in event.measurements) {
            // ignore any measurement if it is not from GPS constellation
            if (measurement.constellationType != GnssStatus.CONSTELLATION_GPS) {
                continue
            }
            // ignore raw data if time is zero, if signal to noise ratio is below threshold or if
            // TOW is not yet decoded
            if (measurement.cn0DbHz >= C_TO_N0_THRESHOLD_DB_HZ
                    && measurement.state and (1L shl TOW_DECODED_MEASUREMENT_STATE_BIT).toInt() != 0L) {

                // calculate day of year and Gps week number needed for the least square
                val gpsTime = GpsTime(mArrivalTimeSinceGpsEpochNs)
                // Gps weekly epoch in Nanoseconds: defined as of every Sunday night at 00:00:000
                val gpsWeekEpochNs: Long = GpsTime.Companion.getGpsWeekEpochNano(gpsTime)
                mArrivalTimeSinceGPSWeekNs = (mArrivalTimeSinceGpsEpochNs - gpsWeekEpochNs).toDouble()
                mGpsWeekNumber = gpsTime.gpsWeekSecond.first
                // calculate day of the year between 1 and 366
                val cal = gpsTime.timeInCalendar
                mDayOfYear1To366 = cal!![Calendar.DAY_OF_YEAR]
                val receivedGPSTowNs = measurement.receivedSvTimeNanos
                if (receivedGPSTowNs > mLargestTowNs) {
                    mLargestTowNs = receivedGPSTowNs
                }
                mUsefulSatellitesToTowNs[measurement.svid - 1] = receivedGPSTowNs
                val gpsReceiverMeasurement = GpsMeasurement(
                        mArrivalTimeSinceGPSWeekNs.toLong(),
                        measurement.accumulatedDeltaRangeMeters,
                        isAccumulatedDeltaRangeStateValid(measurement.accumulatedDeltaRangeState),
                        measurement.pseudorangeRateMetersPerSecond,
                        measurement.cn0DbHz,
                        measurement.accumulatedDeltaRangeUncertaintyMeters,
                        measurement.pseudorangeRateUncertaintyMetersPerSecond)
                mUsefulSatellitesToReceiverMeasurements[measurement.svid - 1] = gpsReceiverMeasurement
            }
        }

        // check if we should continue using the navigation message from the SUPL server, or use the
        // navigation message from the device if we fully received it
        val useNavMessageFromSupl = continueUsingNavMessageFromSupl(
                mUsefulSatellitesToReceiverMeasurements, mHardwareGpsNavMessageProto)
        if (useNavMessageFromSupl) {
            Log.d(TAG, "Using navigation message from SUPL server")
            if (mFirstSuplRequestNeeded
                    || System.currentTimeMillis() - mLastReceivedSuplMessageTimeMillis
                    > mDeltaTimeMillisToMakeSuplRequest) {
                // The following line is blocking call for SUPL connection and back. But it is fast enough
                mGpsNavMessageProtoUsed = getSuplNavMessage(mReferenceLocation!![0].toLong(), mReferenceLocation!![1].toLong())
                if (!isEmptyNavMessage(mGpsNavMessageProtoUsed)) {
                    mFirstSuplRequestNeeded = false
                    mLastReceivedSuplMessageTimeMillis = System.currentTimeMillis()
                } else {
                    return
                }
            }
        } else {
            Log.d(TAG, "Using navigation message from the GPS receiver")
            mGpsNavMessageProtoUsed = mHardwareGpsNavMessageProto
        }

        // some times the SUPL server returns less satellites than the visible ones, so remove those
        // visible satellites that are not returned by SUPL
        for (i in 0 until GpsNavigationMessageStore.Companion.MAX_NUMBER_OF_SATELLITES) {
            if (mUsefulSatellitesToReceiverMeasurements[i] != null
                    && !navMessageProtoContainsSvid(mGpsNavMessageProtoUsed, i + 1)) {
                mUsefulSatellitesToReceiverMeasurements[i] = null
                mUsefulSatellitesToTowNs[i] = null
            }
        }

        // calculate the number of useful satellites
        var numberOfUsefulSatellites = 0
        for (element in mUsefulSatellitesToReceiverMeasurements) {
            if (element != null) {
                numberOfUsefulSatellites++
            }
        }
        if (numberOfUsefulSatellites >= MINIMUM_NUMBER_OF_USEFUL_SATELLITES) {
            // ignore first set of > 4 satellites as they often result in erroneous position
            if (!mFirstUsefulMeasurementSet) {
                // start with last known position and velocity of zero. Following the structure:
                // [X position, Y position, Z position, clock bias,
                //  X Velocity, Y Velocity, Z Velocity, clock bias rate]
                val positionVelocitySolutionEcef = GpsMathOperations.createAndFillArray(8, 0.0)
                val positionVelocityUncertaintyEnu = GpsMathOperations.createAndFillArray(6, 0.0)
                val pseudorangeResidualMeters = GpsMathOperations.createAndFillArray(
                        GpsNavigationMessageStore.Companion.MAX_NUMBER_OF_SATELLITES, Double.NaN)
                performPositionVelocityComputationEcef(
                        mUserPositionVelocityLeastSquareCalculator,
                        mUsefulSatellitesToReceiverMeasurements,
                        mUsefulSatellitesToTowNs,
                        mLargestTowNs,
                        mArrivalTimeSinceGPSWeekNs,
                        mDayOfYear1To366,
                        mGpsWeekNumber,
                        positionVelocitySolutionEcef,
                        positionVelocityUncertaintyEnu,
                        pseudorangeResidualMeters)
                // convert the position solution from ECEF to latitude, longitude and altitude
                val latLngAlt = Ecef2LlaConverter.convertECEFToLLACloseForm(
                        positionVelocitySolutionEcef!![0],
                        positionVelocitySolutionEcef[1],
                        positionVelocitySolutionEcef[2])
                positionSolutionLatLngDeg!![0] = Math.toDegrees(latLngAlt!!.latitudeRadians)
                positionSolutionLatLngDeg!![1] = Math.toDegrees(latLngAlt.longitudeRadians)
                positionSolutionLatLngDeg!![2] = latLngAlt.altitudeMeters
                positionVelocityUncertaintyEnu!![0] = positionVelocityUncertaintyEnu[0]
                positionVelocityUncertaintyEnu[1] = positionVelocityUncertaintyEnu[1]
                positionVelocityUncertaintyEnu[2] = positionVelocityUncertaintyEnu[2]
                System.arraycopy(
                        pseudorangeResidualMeters,
                        0 /*source starting pos*/,
                        pseudorangeResidualsMeters,
                        0 /*destination starting pos*/,
                        GpsNavigationMessageStore.Companion.MAX_NUMBER_OF_SATELLITES /*length of elements*/
                )
                Log.d(TAG,
                        "Position Uncertainty ENU Meters :"
                                + positionVelocityUncertaintyEnu[0]
                                + " "
                                + positionVelocityUncertaintyEnu[1]
                                + " "
                                + positionVelocityUncertaintyEnu[2])
                Log.d(
                        TAG,
                        "Latitude, Longitude, Altitude: "
                                + positionSolutionLatLngDeg!![0]
                                + " "
                                + positionSolutionLatLngDeg!![1]
                                + " "
                                + positionSolutionLatLngDeg!![2])
                val velocityEnu = Ecef2EnuConverter.convertEcefToEnu(
                        positionVelocitySolutionEcef[4],
                        positionVelocitySolutionEcef[5],
                        positionVelocitySolutionEcef[6],
                        latLngAlt.latitudeRadians,
                        latLngAlt.longitudeRadians
                )
                velocitySolutionEnuMps!![0] = velocityEnu!!.enuEast
                velocitySolutionEnuMps!![1] = velocityEnu.enuNorth
                velocitySolutionEnuMps!![2] = velocityEnu.enuUP
                Log.d(
                        TAG,
                        "Velocity ENU Mps: "
                                + velocitySolutionEnuMps!![0]
                                + " "
                                + velocitySolutionEnuMps!![1]
                                + " "
                                + velocitySolutionEnuMps!![2])
                positionVelocityUncertaintyEnu[3] = positionVelocityUncertaintyEnu[3]
                positionVelocityUncertaintyEnu[4] = positionVelocityUncertaintyEnu[4]
                positionVelocityUncertaintyEnu[5] = positionVelocityUncertaintyEnu[5]
                Log.d(TAG,
                        "Velocity Uncertainty ENU Mps :"
                                + positionVelocityUncertaintyEnu[3]
                                + " "
                                + positionVelocityUncertaintyEnu[4]
                                + " "
                                + positionVelocityUncertaintyEnu[5])
            }
            mFirstUsefulMeasurementSet = false
        } else {
            Log.d(
                    TAG, "Less than four satellites with SNR above threshold visible ... "
                    + "no position is calculated!")
            positionSolutionLatLngDeg = GpsMathOperations.createAndFillArray(3, Double.NaN)
            velocitySolutionEnuMps = GpsMathOperations.createAndFillArray(3, Double.NaN)
            pseudorangeResidualsMeters = GpsMathOperations.createAndFillArray(
                    GpsNavigationMessageStore.Companion.MAX_NUMBER_OF_SATELLITES, Double.NaN)
        }
    }

    private fun isEmptyNavMessage(navMessageProto: GpsNavMessageProto?): Boolean {
        if (navMessageProto!!.iono == null) return true
        return if (navMessageProto.ephemerids.size == 0) true else false
    }

    private fun navMessageProtoContainsSvid(navMessageProto: GpsNavMessageProto?, svid: Int): Boolean {
        val ephemeridesList: List<GpsEphemerisProto> = ArrayList(Arrays.asList(*navMessageProto!!.ephemerids))
        for (ephProtoFromList in ephemeridesList) {
            if (ephProtoFromList.prn == svid) {
                return true
            }
        }
        return false
    }

    /**
     * Calculates ECEF least square position and velocity solutions from an array of [ ] in meters and meters per second and store the result in `positionVelocitySolutionEcef`
     */
    @Throws(Exception::class)
    private fun performPositionVelocityComputationEcef(
            userPositionVelocityLeastSquare: UserPositionVelocityWeightedLeastSquare,
            usefulSatellitesToReceiverMeasurements: Array<GpsMeasurement?>,
            usefulSatellitesToTOWNs: Array<Long?>,
            largestTowNs: Long,
            arrivalTimeSinceGPSWeekNs: Double,
            dayOfYear1To366: Int,
            gpsWeekNumber: Int,
            positionVelocitySolutionEcef: DoubleArray?,
            positionVelocityUncertaintyEnu: DoubleArray?,
            pseudorangeResidualMeters: DoubleArray?) {
        val usefulSatellitesToPseudorangeMeasurements: List<GpsMeasurementWithRangeAndUncertainty> = UserPositionVelocityWeightedLeastSquare.Companion.computePseudorangeAndUncertainties(
                Arrays.asList(*usefulSatellitesToReceiverMeasurements),
                usefulSatellitesToTOWNs,
                largestTowNs)

        // calculate iterative least square position solution and velocity solutions
        userPositionVelocityLeastSquare.calculateUserPositionVelocityLeastSquare(
                mGpsNavMessageProtoUsed,
                usefulSatellitesToPseudorangeMeasurements,
                arrivalTimeSinceGPSWeekNs * SECONDS_PER_NANO,
                gpsWeekNumber,
                dayOfYear1To366,
                positionVelocitySolutionEcef,
                positionVelocityUncertaintyEnu,
                pseudorangeResidualMeters)
        Log.d(
                TAG,
                "Least Square Position Solution in ECEF meters: "
                        + positionVelocitySolutionEcef!![0]
                        + " "
                        + positionVelocitySolutionEcef[1]
                        + " "
                        + positionVelocitySolutionEcef[2])
        Log.d(TAG, "Estimated Receiver clock offset in meters: " + positionVelocitySolutionEcef[3])
        Log.d(
                TAG,
                "Velocity Solution in ECEF Mps: "
                        + positionVelocitySolutionEcef[4]
                        + " "
                        + positionVelocitySolutionEcef[5]
                        + " "
                        + positionVelocitySolutionEcef[6])
        Log.d(TAG, "Estimated Receiver clock offset rate in mps: " + positionVelocitySolutionEcef[7])
    }

    /**
     * Reads the navigation message from the SUPL server by creating a Stubby client to Stubby server
     * that wraps the SUPL server. The input is the time in nanoseconds since the GPS epoch at which
     * the navigation message is required and the output is a [GpsNavMessageProto]
     *
     * @throws IOException
     * @throws UnknownHostException
     */
    @Throws(UnknownHostException::class, IOException::class)
    private fun getSuplNavMessage(latE7: Long, lngE7: Long): GpsNavMessageProto {
        val suplRrlpController = SuplRrlpController(SUPL_SERVER_NAME, SUPL_SERVER_PORT)
        return suplRrlpController.generateNavMessage(latE7, lngE7)
    }

    /**
     * Parses a string array containing an updates to the navigation message and return the most
     * recent [GpsNavMessageProto].
     */
    fun parseHwNavigationMessageUpdates(navigationMessage: GnssNavigationMessage) {
        val messagePrn = navigationMessage.svid.toByte()
        val messageType = (navigationMessage.type shr 8).toByte()
        val subMessageId = navigationMessage.submessageId
        val messageRawData = navigationMessage.data
        // parse only GPS navigation messages for now
        if (messageType.toInt() == 1) {
            mGpsNavigationMessageStore.onNavMessageReported(
                    messagePrn, messageType, subMessageId.toShort(), messageRawData)
            mHardwareGpsNavMessageProto = mGpsNavigationMessageStore.createDecodedNavMessage()
        }
    }

    /** Sets a rough location of the receiver that can be used to request SUPL assistance data  */
    fun setReferencePosition(latE7: Int, lngE7: Int, altE7: Int) {
        if (mReferenceLocation == null) {
            mReferenceLocation = IntArray(3)
        }
        mReferenceLocation!![0] = latE7
        mReferenceLocation!![1] = lngE7
        mReferenceLocation!![2] = altE7
    }

    /**
     * Converts the input from LLA coordinates to ECEF and set up the reference position of
     * `mUserPositionVelocityLeastSquareCalculator` to calculate a corrected residual.
     *
     *
     *  Based on this input ground truth, true residuals can be computed. This is done by using
     * the high elevation satellites to compute the true user clock error and with the knowledge of
     * the satellite positions.
     *
     *
     *  If no ground truth is set, no residual analysis will be performed.
     */
    fun setCorrectedResidualComputationTruthLocationLla(groundTruthLocationLla: DoubleArray?) {
        if (groundTruthLocationLla == null) {
            mUserPositionVelocityLeastSquareCalculator
                    .setTruthLocationForCorrectedResidualComputationEcef(null)
            return
        }
        val llaValues = GeodeticLlaValues(
                Math.toRadians(groundTruthLocationLla[0]),
                Math.toRadians(groundTruthLocationLla[1]),
                Math.toRadians(groundTruthLocationLla[2]))
        mUserPositionVelocityLeastSquareCalculator.setTruthLocationForCorrectedResidualComputationEcef(
                Lla2EcefConverter.convertFromLlaToEcefMeters(llaValues))
    }

    companion object {
        private const val TAG = "PseudorangePositionVelocityFromRealTimeEvents"
        private const val SECONDS_PER_NANO = 1.0e-9
        private const val TOW_DECODED_MEASUREMENT_STATE_BIT = 3

        /** Average signal travel time from GPS satellite and earth  */
        private const val MINIMUM_NUMBER_OF_USEFUL_SATELLITES = 4
        private const val C_TO_N0_THRESHOLD_DB_HZ = 18
        private const val SUPL_SERVER_NAME = "supl.google.com"
        private const val SUPL_SERVER_PORT = 7276

        /**
         * Checks if we should continue using the navigation message from the SUPL server, or use the
         * navigation message from the device if we fully received it. If the navigation message read from
         * the receiver has all the visible satellite ephemerides, return false, otherwise, return true.
         */
        private fun continueUsingNavMessageFromSupl(
                usefulSatellitesToReceiverMeasurements: Array<GpsMeasurement?>,
                hardwareGpsNavMessageProto: GpsNavMessageProto?): Boolean {
            var useNavMessageFromSupl = true
            if (hardwareGpsNavMessageProto != null) {
                val hardwareEphemeridesList = ArrayList(Arrays.asList(*hardwareGpsNavMessageProto.ephemerids))
                if (hardwareGpsNavMessageProto.iono != null) {
                    for (i in 0 until GpsNavigationMessageStore.Companion.MAX_NUMBER_OF_SATELLITES) {
                        if (usefulSatellitesToReceiverMeasurements[i] != null) {
                            val prn = i + 1
                            for (hardwareEphProtoFromList in hardwareEphemeridesList) {
                                if (hardwareEphProtoFromList.prn == prn) {
                                    useNavMessageFromSupl = false
                                    break
                                }
                                useNavMessageFromSupl = true
                            }
                            if (useNavMessageFromSupl == true) {
                                break
                            }
                        }
                    }
                }
            }
            return useNavMessageFromSupl
        }

        /**
         * Returns the result of the GnssMeasurement.ADR_STATE_VALID bitmask being applied to the
         * AccumulatedDeltaRangeState from a GnssMeasurement - true if the ADR state is valid,
         * false if it is not
         * @param accumulatedDeltaRangeState accumulatedDeltaRangeState from GnssMeasurement
         * @return the result of the GnssMeasurement.ADR_STATE_VALID bitmask being applied to the
         * * AccumulatedDeltaRangeState of the given GnssMeasurement - true if the ADR state is valid,
         * * false if it is not
         */
        private fun isAccumulatedDeltaRangeStateValid(accumulatedDeltaRangeState: Int): Boolean {
            return GnssMeasurement.ADR_STATE_VALID and accumulatedDeltaRangeState == GnssMeasurement.ADR_STATE_VALID
        }
    }
}