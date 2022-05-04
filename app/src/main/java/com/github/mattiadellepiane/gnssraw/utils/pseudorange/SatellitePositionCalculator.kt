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
 * Class to calculate GPS satellite positions from the ephemeris data
 */
object SatellitePositionCalculator {
    private const val SPEED_OF_LIGHT_MPS = 299792458.0
    private const val UNIVERSAL_GRAVITATIONAL_PARAMETER_M3_SM2 = 3.986005e14
    private const val NUMBER_OF_ITERATIONS_FOR_SAT_POS_CALCULATION = 5
    private const val EARTH_ROTATION_RATE_RAD_PER_SEC = 7.2921151467e-5

    /**
     *
     * Calculates GPS satellite position and velocity from ephemeris including the Sagnac effect
     * starting from unknown user to satellite distance and speed. So we start from an initial guess
     * of the user to satellite range and range rate and iterate to include the Sagnac effect. Few
     * iterations are enough to achieve a satellite position with millimeter accuracy.
     * A `PositionAndVelocity` class is returned containing satellite position in meters
     * (x, y and z) and velocity in meters per second (x, y, z)
     *
     *
     * Satellite position and velocity equations are obtained from:
     * http://www.gps.gov/technical/icwg/ICD-GPS-200C.pdf) pages 94 - 101 and
     * http://fenrir.naruoka.org/download/autopilot/note/080205_gps/gps_velocity.pdf
     *
     * @param ephemerisProto parameters of the navigation message
     * @param receiverGpsTowAtTimeOfTransmissionCorrectedSec Receiver estimate of GPS time of week
     * when signal was transmitted corrected with the satellite clock drift (seconds)
     * @param receiverGpsWeekAtTimeOfTransmission Receiver estimate of GPS week when signal was
     * transmitted (0-1024+)
     * @param userPosXMeters Last known user x-position (if known) [meters]
     * @param userPosYMeters Last known user y-position (if known) [meters]
     * @param userPosZMeters Last known user z-position (if known) [meters]
     * @throws Exception
     */
    @Throws(Exception::class)
    fun calculateSatellitePositionAndVelocityFromEphemeris(ephemerisProto: GpsEphemerisProto?, receiverGpsTowAtTimeOfTransmissionCorrectedSec: Double,
                                                           receiverGpsWeekAtTimeOfTransmission: Int,
                                                           userPosXMeters: Double,
                                                           userPosYMeters: Double,
                                                           userPosZMeters: Double): PositionAndVelocity {

        // lets start with a first user to sat distance guess of 70 ms and zero velocity
        val userSatRangeAndRate = RangeAndRangeRate(0.070 * SPEED_OF_LIGHT_MPS, 0.0 /* range rate*/)

        // To apply sagnac effect correction, We are starting from an approximate guess of the user to
        // satellite range, iterate 3 times and that should be enough to reach millimeter accuracy
        val satPosAndVel = PositionAndVelocity(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        val userPosAndVel = PositionAndVelocity(userPosXMeters, userPosYMeters, userPosZMeters,
                0.0 /* user velocity x*/, 0.0 /* user velocity y*/, 0.0 /* user velocity z */)
        for (i in 0 until NUMBER_OF_ITERATIONS_FOR_SAT_POS_CALCULATION) {
            calculateSatellitePositionAndVelocity(ephemerisProto,
                    receiverGpsTowAtTimeOfTransmissionCorrectedSec, receiverGpsWeekAtTimeOfTransmission,
                    userSatRangeAndRate, satPosAndVel)
            computeUserToSatelliteRangeAndRangeRate(userPosAndVel, satPosAndVel, userSatRangeAndRate)
        }
        return satPosAndVel
    }

    /**
     * Calculates GPS satellite position and velocity from ephemeris based on the ICD-GPS-200.
     * Satellite position in meters (x, y and z) and velocity in meters per second (x, y, z) are set
     * in the passed `PositionAndVelocity` instance.
     *
     *
     * Sources: http://www.gps.gov/technical/icwg/ICD-GPS-200C.pdf) pages 94 - 101 and
     * http://fenrir.naruoka.org/download/autopilot/note/080205_gps/gps_velocity.pdf
     *
     * @param ephemerisProto parameters of the navigation message
     * @param receiverGpsTowAtTimeOfTransmissionCorrected Receiver estimate of GPS time of week when
     * signal was transmitted corrected with the satellite clock drift (seconds)
     * @param receiverGpsWeekAtTimeOfTransmission Receiver estimate of GPS week when signal was
     * transmitted (0-1024+)
     * @param userSatRangeAndRate user to satellite range and range rate
     * @param satPosAndVel Satellite position and velocity instance in which the method results will
     * be set
     * @throws Exception
     */
    @Throws(Exception::class)
    fun calculateSatellitePositionAndVelocity(ephemerisProto: GpsEphemerisProto?,
                                              receiverGpsTowAtTimeOfTransmissionCorrected: Double, receiverGpsWeekAtTimeOfTransmission: Int,
                                              userSatRangeAndRate: RangeAndRangeRate, satPosAndVel: PositionAndVelocity) {

        // Calculate satellite clock correction (meters), Kepler Eccentric anomaly (radians) and time
        // from ephemeris reference epoch (tkSec) iteratively
        val satClockCorrectionValues = SatelliteClockCorrectionCalculator.calculateSatClockCorrAndEccAnomAndTkIteratively(
                ephemerisProto, receiverGpsTowAtTimeOfTransmissionCorrected,
                receiverGpsWeekAtTimeOfTransmission.toDouble())
        val eccentricAnomalyRadians = satClockCorrectionValues!!.eccentricAnomalyRadians
        val tkSec = satClockCorrectionValues.timeFromRefEpochSec

        // True_anomaly (angle from perigee)
        val trueAnomalyRadians = Math.atan2(Math.sqrt(1.0 - ephemerisProto!!.e * ephemerisProto.e)
                * Math.sin(eccentricAnomalyRadians),
                Math.cos(eccentricAnomalyRadians) - ephemerisProto.e)

        // Argument of latitude of the satellite
        var argumentOfLatitudeRadians = trueAnomalyRadians + ephemerisProto.omega

        // Radius of satellite orbit
        var radiusOfSatelliteOrbitMeters = (ephemerisProto.rootOfA * ephemerisProto.rootOfA
                * (1.0 - ephemerisProto.e * Math.cos(eccentricAnomalyRadians)))

        // Radius correction due to second harmonic perturbations of the orbit
        val radiusCorrectionMeters = ephemerisProto.crc
        * Math.cos(2.0 * argumentOfLatitudeRadians) + ephemerisProto.crs
        * Math.sin(2.0 * argumentOfLatitudeRadians)
        // Argument of latitude correction due to second harmonic perturbations of the orbit
        val argumentOfLatitudeCorrectionRadians = ephemerisProto.cuc
        * Math.cos(2.0 * argumentOfLatitudeRadians) + ephemerisProto.cus
        * Math.sin(2.0 * argumentOfLatitudeRadians)
        // Correction to inclination due to second harmonic perturbations of the orbit
        val inclinationCorrectionRadians = ephemerisProto.cic
        * Math.cos(2.0 * argumentOfLatitudeRadians) + ephemerisProto.cis
        * Math.sin(2.0 * argumentOfLatitudeRadians)

        // Corrected radius of satellite orbit
        radiusOfSatelliteOrbitMeters += radiusCorrectionMeters
        // Corrected argument of latitude
        argumentOfLatitudeRadians += argumentOfLatitudeCorrectionRadians
        // Corrected inclination
        val inclinationRadians = ephemerisProto.i0 + inclinationCorrectionRadians + ephemerisProto.iDot * tkSec

        // Position in orbital plane
        val xPositionMeters = radiusOfSatelliteOrbitMeters * Math.cos(argumentOfLatitudeRadians)
        val yPositionMeters = radiusOfSatelliteOrbitMeters * Math.sin(argumentOfLatitudeRadians)

        // Corrected longitude of the ascending node (signal propagation time is included to compensate
        // for the Sagnac effect)
        val omegaKRadians = (ephemerisProto.omega0
                + (ephemerisProto.omegaDot - EARTH_ROTATION_RATE_RAD_PER_SEC) * tkSec
                - EARTH_ROTATION_RATE_RAD_PER_SEC
                * (ephemerisProto.toe + userSatRangeAndRate.rangeMeters / SPEED_OF_LIGHT_MPS))

        // compute the resulting satellite position
        val satPosXMeters = xPositionMeters * Math.cos(omegaKRadians) - (yPositionMeters
                * Math.cos(inclinationRadians) * Math.sin(omegaKRadians))
        val satPosYMeters = xPositionMeters * Math.sin(omegaKRadians) + (yPositionMeters
                * Math.cos(inclinationRadians) * Math.cos(omegaKRadians))
        val satPosZMeters = yPositionMeters * Math.sin(inclinationRadians)

        // Satellite Velocity Computation using the broadcast ephemeris
        // http://fenrir.naruoka.org/download/autopilot/note/080205_gps/gps_velocity.pdf
        // Units are not added in some of the variable names to have the same name as the ICD-GPS200
        // Semi-major axis of orbit (meters)
        val a = ephemerisProto.rootOfA * ephemerisProto.rootOfA
        // Computed mean motion (radians/seconds)
        val n0 = Math.sqrt(UNIVERSAL_GRAVITATIONAL_PARAMETER_M3_SM2 / (a * a * a))
        // Corrected mean motion (radians/seconds)
        val n = n0 + ephemerisProto.deltaN
        // Derivative of mean anomaly (radians/seconds)
        // Derivative of eccentric anomaly (radians/seconds)
        val eccentricAnomalyDotRadPerSec = n / (1.0 - ephemerisProto.e * Math.cos(eccentricAnomalyRadians))
        // Derivative of true anomaly (radians/seconds)
        val trueAnomalyDotRadPerSec = (Math.sin(eccentricAnomalyRadians)
                * eccentricAnomalyDotRadPerSec
                * (1.0 + ephemerisProto.e * Math.cos(trueAnomalyRadians))) / (Math.sin(trueAnomalyRadians)
                * (1.0 - ephemerisProto.e * Math.cos(eccentricAnomalyRadians)))
        // Derivative of argument of latitude (radians/seconds)
        val argumentOfLatitudeDotRadPerSec = trueAnomalyDotRadPerSec + 2.0 * (ephemerisProto.cus
                * Math.cos(2.0 * argumentOfLatitudeRadians) - ephemerisProto.cuc
                * Math.sin(2.0 * argumentOfLatitudeRadians)) * trueAnomalyDotRadPerSec
        // Derivative of radius of satellite orbit (m/s)
        val radiusOfSatelliteOrbitDotMPerSec = (a * ephemerisProto.e
                * Math.sin(eccentricAnomalyRadians) * n)
        / 1.0 - ephemerisProto.e * Math.cos(eccentricAnomalyRadians) + (2.0 * (ephemerisProto.crs * Math.cos(2.0 * argumentOfLatitudeRadians)
                - ephemerisProto.crc * Math.sin(2.0 * argumentOfLatitudeRadians))
                * trueAnomalyDotRadPerSec)
        // Derivative of the inclination (radians/seconds)
        val inclinationDotRadPerSec = ephemerisProto.iDot + (ephemerisProto.cis
                * Math.cos(2.0 * argumentOfLatitudeRadians) - ephemerisProto.cic
                * Math.sin(2.0 * argumentOfLatitudeRadians)) * 2.0 * trueAnomalyDotRadPerSec
        val xVelocityMPS = (radiusOfSatelliteOrbitDotMPerSec * Math.cos(argumentOfLatitudeRadians)
                - yPositionMeters * argumentOfLatitudeDotRadPerSec)
        val yVelocityMPS = (radiusOfSatelliteOrbitDotMPerSec * Math.sin(argumentOfLatitudeRadians)
                + xPositionMeters * argumentOfLatitudeDotRadPerSec)

        // Corrected rate of right ascension including compensation for the Sagnac effect
        val omegaDotRadPerSec = ephemerisProto.omegaDot - EARTH_ROTATION_RATE_RAD_PER_SEC
        * 1.0 + userSatRangeAndRate.rangeRateMetersPerSec / SPEED_OF_LIGHT_MPS
        // compute the resulting satellite velocity
        val satVelXMPS = xVelocityMPS - yPositionMeters * Math.cos(inclinationRadians) * omegaDotRadPerSec
        * Math.cos(omegaKRadians) - (xPositionMeters * omegaDotRadPerSec + yVelocityMPS
                * Math.cos(inclinationRadians) - (yPositionMeters * Math.sin(inclinationRadians)
                * inclinationDotRadPerSec)) * Math.sin(omegaKRadians)
        val satVelYMPS = xVelocityMPS - yPositionMeters * Math.cos(inclinationRadians) * omegaDotRadPerSec
        * Math.sin(omegaKRadians) + (xPositionMeters * omegaDotRadPerSec + yVelocityMPS
                * Math.cos(inclinationRadians) - (yPositionMeters * Math.sin(inclinationRadians)
                * inclinationDotRadPerSec)) * Math.cos(omegaKRadians)
        val satVelZMPS = yVelocityMPS * Math.sin(inclinationRadians) + (yPositionMeters
                * Math.cos(inclinationRadians) * inclinationDotRadPerSec)
        satPosAndVel.positionXMeters = satPosXMeters
        satPosAndVel.positionYMeters = satPosYMeters
        satPosAndVel.positionZMeters = satPosZMeters
        satPosAndVel.velocityXMetersPerSec = satVelXMPS
        satPosAndVel.velocityYMetersPerSec = satVelYMPS
        satPosAndVel.velocityZMetersPerSec = satVelZMPS
    }

    /**
     * Computes and sets the passed `RangeAndRangeRate` instance containing user to satellite
     * range (meters) and range rate (m/s) given the user position (ECEF meters), user velocity (m/s),
     * satellite position (ECEF meters) and satellite velocity (m/s).
     */
    private fun computeUserToSatelliteRangeAndRangeRate(userPosAndVel: PositionAndVelocity,
                                                        satPosAndVel: PositionAndVelocity, rangeAndRangeRate: RangeAndRangeRate) {
        val dXMeters = satPosAndVel.positionXMeters - userPosAndVel.positionXMeters
        val dYMeters = satPosAndVel.positionYMeters - userPosAndVel.positionYMeters
        val dZMeters = satPosAndVel.positionZMeters - userPosAndVel.positionZMeters
        // range in meters
        val rangeMeters = Math.sqrt(dXMeters * dXMeters + dYMeters * dYMeters + dZMeters * dZMeters)
        // range rate in meters / second
        val rangeRateMetersPerSec = (((userPosAndVel.velocityXMetersPerSec - satPosAndVel.velocityXMetersPerSec) * dXMeters + (userPosAndVel.velocityYMetersPerSec - satPosAndVel.velocityYMetersPerSec) * dYMeters + (userPosAndVel.velocityZMetersPerSec - satPosAndVel.velocityZMetersPerSec) * dZMeters)
                / rangeMeters)
        rangeAndRangeRate.rangeMeters = rangeMeters
        rangeAndRangeRate.rangeRateMetersPerSec = rangeRateMetersPerSec
    }

    /**
     *
     * A class containing position values (x, y, z) in meters and velocity values (x, y, z) in meters
     * per seconds
     */
    class PositionAndVelocity
    /**
     * Constructor
     */(
            /**
             * x - position in meters
             */
            var positionXMeters: Double,
            /**
             * y - position in meters
             */
            var positionYMeters: Double,
            /**
             * z - position in meters
             */
            var positionZMeters: Double,
            /**
             * x - velocity in meters
             */
            var velocityXMetersPerSec: Double,
            /**
             * y - velocity in meters
             */
            var velocityYMetersPerSec: Double,
            /**
             * z - velocity in meters
             */
            var velocityZMetersPerSec: Double)

    /**
     *
     * A class containing range of satellite to user in meters and range rate in meters per seconds
     */
    class RangeAndRangeRate
    /**
     * Constructor
     */(
            /**
             * Range in meters
             */
            var rangeMeters: Double,
            /**
             * Range rate in meters per seconds
             */
            var rangeRateMetersPerSec: Double)
}