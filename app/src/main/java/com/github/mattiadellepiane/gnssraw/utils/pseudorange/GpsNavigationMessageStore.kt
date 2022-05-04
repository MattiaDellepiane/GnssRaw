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
import com.google.common.base.Preconditions
import java.util.concurrent.TimeUnit

/**
 * A class to extract the fields of the GPS navigation message from the raw bytes received from the
 * GPS receiver.
 *
 *
 * Raw bytes are passed by calling the method
 * [.onNavMessageReported]
 *
 *
 * A [GpsNavMessageProto] containing the extracted field is obtained by calling the method
 * [.createDecodedNavMessage]
 *
 *
 * References:
 * http://www.gps.gov/technical/icwg/IS-GPS-200D.pdf and
 * http://www.gps.gov/technical/ps/1995-SPS-signal-specification.pdf
 *
 */
class GpsNavigationMessageStore {
    /** Partially decoded intermediate ephemerides  */
    private val partiallyDecodedIntermediateEphemerides = arrayOfNulls<IntermediateEphemeris>(MAX_NUMBER_OF_SATELLITES)

    /** Fully decoded intermediate ephemerides  */
    private val fullyDecodedIntermediateEphemerides = arrayOfNulls<IntermediateEphemeris>(MAX_NUMBER_OF_SATELLITES)
    private var decodedIonosphericObj: IonosphericModelProto? = null

    /**
     * Builds and returns the current [GpsNavMessageProto] filling the different ephemeris for
     * the different satellites and setting the ionospheric model parameters.
     */
    fun createDecodedNavMessage(): GpsNavMessageProto {
        synchronized(fullyDecodedIntermediateEphemerides) {
            val gpsNavMessageProto = GpsNavMessageProto()
            val gpsEphemerisProtoList = ArrayList<GpsEphemerisProto>()
            for (i in 0 until MAX_NUMBER_OF_SATELLITES) {
                if (fullyDecodedIntermediateEphemerides[i] != null) {
                    gpsEphemerisProtoList.add(fullyDecodedIntermediateEphemerides[i]!!.ephemerisObj)
                }
            }
            if (decodedIonosphericObj != null) {
                gpsNavMessageProto.iono = decodedIonosphericObj
            }
            gpsNavMessageProto.ephemerids = gpsEphemerisProtoList.toTypedArray()
            return gpsNavMessageProto
        }
    }

    /**
     * Handles a fresh Navigation Message. The message is in its raw format.
     */
    fun onNavMessageReported(prn: Byte, type: Byte, id: Short, rawData: ByteArray?) {
        Preconditions.checkArgument(type.toInt() == 1, "Unsupported NavigationMessage Type: $type")
        Preconditions.checkArgument(
                rawData != null && rawData.size == L1_CA_MESSAGE_LENGTH_BYTES,
                "Invalid length of rawData for L1 C/A")
        synchronized(fullyDecodedIntermediateEphemerides) {
            when (id) {
                1 -> handleFirstSubframe(prn, rawData)
                2 -> handleSecondSubframe(prn, rawData)
                3 -> handleThirdSubframe(prn, rawData)
                4 -> handleFourthSubframe(rawData)
                5 -> {
                }
                else -> throw IllegalArgumentException("Invalid Subframe ID: $id")
            }
        }
    }

    /**
     * Handles the first navigation message subframe which contains satellite clock correction
     * parameters, GPS date (week number) plus satellite status and health.
     */
    private fun handleFirstSubframe(prn: Byte, rawData: ByteArray?) {
        var iodc = extractBits(IODC1_INDEX, IODC1_LENGTH, rawData) shl 8
        iodc = iodc or extractBits(IODC2_INDEX, IODC2_LENGTH, rawData)
        val intermediateEphemeris = findIntermediateEphemerisToUpdate(prn, SUBFRAME_1, iodc)
                ?: // we are up-to-date
                return
        val gpsEphemerisProto = intermediateEphemeris.ephemerisObj
        gpsEphemerisProto.iodc = iodc


        // the navigation message contains a modulo-1023 week number
        var week = extractBits(WEEK_INDEX, WEEK_LENGTH, rawData)
        week = getGpsWeekWithRollover(week)
        gpsEphemerisProto.week = week
        val uraIndex = extractBits(URA_INDEX, URA_LENGTH, rawData)
        val svAccuracy = computeNominalSvAccuracy(uraIndex)
        gpsEphemerisProto.svAccuracyM = svAccuracy
        val svHealth = extractBits(SV_HEALTH_INDEX, SV_HEALTH_LENGTH, rawData)
        gpsEphemerisProto.svHealth = svHealth
        val tgd = extractBits(TGD_INDEX, TGD_LENGTH, rawData).toByte()
        gpsEphemerisProto.tgd = tgd * POW_2_NEG_31
        val toc = extractBits(TOC_INDEX, TOC_LENGTH, rawData)
        val tocScaled = toc * POW_2_4
        gpsEphemerisProto.toc = tocScaled
        val af2 = extractBits(AF2_INDEX, AF2_LENGTH, rawData).toByte()
        gpsEphemerisProto.af2 = af2 * POW_2_NEG_55
        val af1 = extractBits(AF1_INDEX, AF1_LENGTH, rawData).toShort()
        gpsEphemerisProto.af1 = af1 * POW_2_NEG_43

        // a 22-bit two's complement number
        var af0 = extractBits(AF0_INDEX, AF0_LENGTH, rawData)
        af0 = getTwoComplement(af0, AF0_LENGTH)
        gpsEphemerisProto.af0 = af0 * POW_2_NEG_31
        updateDecodedState(prn, SUBFRAME_1, intermediateEphemeris)
    }

    /**
     * Handles the second navigation message subframe which contains satellite ephemeris
     */
    private fun handleSecondSubframe(prn: Byte, rawData: ByteArray?) {
        val iode = extractBits(IODE1_INDEX, IODE_LENGTH, rawData)
        val intermediateEphemeris = findIntermediateEphemerisToUpdate(prn, SUBFRAME_2, iode)
                ?: // nothing to update
                return
        val gpsEphemerisProto = intermediateEphemeris.ephemerisObj
        gpsEphemerisProto.iode = iode
        val crs = extractBits(CRS_INDEX, CRS_LENGTH, rawData).toShort()
        gpsEphemerisProto.crs = crs * POW_2_NEG_5
        val deltaN = extractBits(DELTA_N_INDEX, DELTA_N_LENGTH, rawData).toShort()
        gpsEphemerisProto.deltaN = deltaN * POW_2_NEG_43 * Math.PI
        val m0 = buildUnsigned32BitsWordFrom8And24Words(M0_INDEX8, M0_INDEX24, rawData).toInt()
        gpsEphemerisProto.m0 = m0 * POW_2_NEG_31 * Math.PI
        val cuc = extractBits(CUC_INDEX, CUC_LENGTH, rawData).toShort()
        gpsEphemerisProto.cuc = cuc * POW_2_NEG_29

        // an unsigned 32 bit value
        val e = buildUnsigned32BitsWordFrom8And24Words(E_INDEX8, E_INDEX24, rawData)
        gpsEphemerisProto.e = e * POW_2_NEG_33
        val cus = extractBits(CUS_INDEX, CUS_LENGTH, rawData).toShort()
        gpsEphemerisProto.cus = cus * POW_2_NEG_29

        // an unsigned 32 bit value
        val a = buildUnsigned32BitsWordFrom8And24Words(A_INDEX8, A_INDEX24, rawData)
        gpsEphemerisProto.rootOfA = a * POW_2_NEG_19
        val toe = extractBits(TOE_INDEX, TOE_LENGTH, rawData)
        val toeScaled = toe * POW_2_4
        gpsEphemerisProto.toe = toe * POW_2_4
        updateDecodedState(prn, SUBFRAME_2, intermediateEphemeris)
    }

    /**
     * Handles the third navigation message subframe which contains satellite ephemeris
     */
    private fun handleThirdSubframe(prn: Byte, rawData: ByteArray?) {
        val iode = extractBits(IODE2_INDEX, IODE_LENGTH, rawData)
        val intermediateEphemeris = findIntermediateEphemerisToUpdate(prn, SUBFRAME_3, iode)
                ?: // A fully or partially decoded message is available , hence nothing to update
                return
        val gpsEphemerisProto = intermediateEphemeris.ephemerisObj
        gpsEphemerisProto.iode = iode
        val cic = extractBits(CIC_INDEX, CIC_LENGTH, rawData).toShort()
        gpsEphemerisProto.cic = cic * POW_2_NEG_29
        val o0 = buildUnsigned32BitsWordFrom8And24Words(O0_INDEX8, O0_INDEX24, rawData).toInt()
        gpsEphemerisProto.omega0 = o0 * POW_2_NEG_31 * Math.PI
        val o = buildUnsigned32BitsWordFrom8And24Words(O_INDEX8, O_INDEX24, rawData).toInt()
        gpsEphemerisProto.omega = o * POW_2_NEG_31 * Math.PI
        var odot = extractBits(ODOT_INDEX, ODOT_LENGTH, rawData)
        odot = getTwoComplement(odot, ODOT_LENGTH)
        gpsEphemerisProto.omegaDot = odot * POW_2_NEG_43 * Math.PI
        val cis = extractBits(CIS_INDEX, CIS_LENGTH, rawData).toShort()
        gpsEphemerisProto.cis = cis * POW_2_NEG_29
        val i0 = buildUnsigned32BitsWordFrom8And24Words(I0_INDEX8, I0_INDEX24, rawData).toInt()
        gpsEphemerisProto.i0 = i0 * POW_2_NEG_31 * Math.PI
        val crc = extractBits(CRC_INDEX, CRC_LENGTH, rawData).toShort()
        gpsEphemerisProto.crc = crc * POW_2_NEG_5


        // a 14-bit two's complement number
        var idot = extractBits(IDOT_INDEX, IDOT_LENGTH, rawData)
        idot = getTwoComplement(idot, IDOT_LENGTH)
        gpsEphemerisProto.iDot = idot * POW_2_NEG_43 * Math.PI
        updateDecodedState(prn, SUBFRAME_3, intermediateEphemeris)
    }

    /**
     * Subframe four provides ionospheric model parameters , UTC information, part of the almanac, and
     * indications whether the Anti-Spoofing, is activated or not.
     *
     *
     * For now, only the ionospheric parameters are parsed.
     */
    private fun handleFourthSubframe(rawData: ByteArray?) {
        val pageId = extractBits(62, 6, rawData).toByte()
        if (pageId != IONOSPHERIC_PARAMETERS_PAGE_18_SV_ID) {
            // We only care to decode ionospheric parameters for now
            return
        }
        val ionosphericModelProto = IonosphericModelProto()
        val alpha = DoubleArray(4)
        val a0 = extractBits(A0_INDEX, A_B_LENGTH, rawData).toByte()
        alpha[0] = a0 * POW_2_NEG_30
        val a1 = extractBits(A1_INDEX, A_B_LENGTH, rawData).toByte()
        alpha[1] = a1 * POW_2_NEG_27
        val a2 = extractBits(A2_INDEX, A_B_LENGTH, rawData).toByte()
        alpha[2] = a2 * POW_2_NEG_24
        val a3 = extractBits(A3_INDEX, A_B_LENGTH, rawData).toByte()
        alpha[3] = a3 * POW_2_NEG_24
        ionosphericModelProto.alpha = alpha
        val beta = DoubleArray(4)
        val b0 = extractBits(B0_INDEX, A_B_LENGTH, rawData).toByte()
        beta[0] = b0 * POW_2_11
        val b1 = extractBits(B1_INDEX, A_B_LENGTH, rawData).toByte()
        beta[1] = b1 * POW_2_14
        val b2 = extractBits(B2_INDEX, A_B_LENGTH, rawData).toByte()
        beta[2] = b2 * POW_2_16
        val b3 = extractBits(B3_INDEX, A_B_LENGTH, rawData).toByte()
        beta[3] = b3 * POW_2_16
        ionosphericModelProto.beta = beta
        val a0UTC = (buildSigned32BitsWordFrom8And24WordsWith8bitslsb(I0UTC_INDEX8, I0UTC_INDEX24, rawData)
                * Math.pow(2.0, -30.0))
        val a1UTC = getTwoComplement(extractBits(I1UTC_INDEX, 24, rawData), 24) * Math.pow(2.0, -50.0)
        val tot = (extractBits(TOT_LS_INDEX, A_B_LENGTH, rawData) * POW_2_12).toShort()
        val wnt = extractBits(WN_LS_INDEX, A_B_LENGTH, rawData).toShort()
        val tls = extractBits(DELTA_T_LS_INDEX, A_B_LENGTH, rawData).toShort()
        val wnlsf = extractBits(WNF_LS_INDEX, A_B_LENGTH, rawData).toShort()
        val dn = extractBits(DN_LS_INDEX, A_B_LENGTH, rawData).toShort()
        val tlsf = extractBits(DELTA_TF_LS_INDEX, A_B_LENGTH, rawData).toShort()
        decodedIonosphericObj = ionosphericModelProto
    }

    /**
     * Updates the [IntermediateEphemeris] with the decoded status of the current subframe.
     * Moreover, update the `partiallyDecodedIntermediateEphemerides` list and
     * `fullyDecodedIntermediateEphemerides` list
     */
    private fun updateDecodedState(prn: Byte, decodedSubframeNumber: Int,
                                   intermediateEphemeris: IntermediateEphemeris) {
        intermediateEphemeris.reportDecodedSubframe(decodedSubframeNumber)
        if (intermediateEphemeris.isFullyDecoded) {
            partiallyDecodedIntermediateEphemerides[prn - 1] = null
            fullyDecodedIntermediateEphemerides[prn - 1] = intermediateEphemeris
        } else {
            partiallyDecodedIntermediateEphemerides[prn - 1] = intermediateEphemeris
        }
    }

    /**
     * Finds an [IntermediateEphemeris] that can be updated by the given data. The pseudocode is
     * as follows:
     *
     * if a fully decoded message is available and matches, there is no need to update
     *
     * if a partially decoded message is available and matches, there is no need to update
     *
     * if the provided issueOfData matches intermediate partially decoded state, update in place
     *
     * otherwise, start a new decoding 'session' for the prn
     *
     * @param prn The prn to update
     * @param subframe The subframe available to update
     * @param issueOfData The issueOfData associated with the given subframe
     * @return a [IntermediateEphemeris] to update with the available data, `null` if
     * there is no need to update a [IntermediateEphemeris].
     */
    private fun findIntermediateEphemerisToUpdate(prn: Byte, subframe: Int,
                                                  issueOfData: Int): IntermediateEphemeris? {
        // find out if we have fully decoded up-to-date ephemeris first
        val fullyDecodedIntermediateEphemeris = fullyDecodedIntermediateEphemerides[prn - 1]
        if (fullyDecodedIntermediateEphemeris != null
                && fullyDecodedIntermediateEphemeris.findSubframeInfo(prn, subframe, issueOfData)
                        .isSubframeDecoded) {
            return null
        }

        // find out next if there is a partially decoded intermediate state we can continue working on
        val partiallyDecodedIntermediateEphemeris = partiallyDecodedIntermediateEphemerides[prn - 1]
                ?: // no intermediate partially decoded state, we need to start a decoding 'session'
                return IntermediateEphemeris(prn)
        val subframeCheckResult = partiallyDecodedIntermediateEphemeris
                .findSubframeInfo(prn, subframe, issueOfData)
        if (subframeCheckResult.isSubframeDecoded) {
            return null
        }
        if (subframeCheckResult.hasSubframe && !subframeCheckResult.issueOfDataMatches) {
            // the navigation message has changed, we need to start over
            return IntermediateEphemeris(prn)
        }
        var intermediateIode = Int.MAX_VALUE
        var intermediateHasIode = false
        val gpsEphemerisProto = partiallyDecodedIntermediateEphemeris.ephemerisObj
        if (partiallyDecodedIntermediateEphemeris.hasDecodedSubframe(SUBFRAME_1)) {
            intermediateHasIode = true
            intermediateIode = gpsEphemerisProto.iodc and IODE_TO_IODC_MASK
        }
        if (partiallyDecodedIntermediateEphemeris.hasDecodedSubframe(SUBFRAME_2)
                || partiallyDecodedIntermediateEphemeris.hasDecodedSubframe(SUBFRAME_3)) {
            intermediateHasIode = true
            intermediateIode = gpsEphemerisProto.iode
        }
        val canContinueDecoding: Boolean
        val iode: Int
        when (subframe) {
            SUBFRAME_1 -> {
                iode = issueOfData and IODE_TO_IODC_MASK
                canContinueDecoding = !intermediateHasIode || intermediateIode == iode
            }
            SUBFRAME_2, SUBFRAME_3 -> {
                iode = issueOfData
                canContinueDecoding = !intermediateHasIode || intermediateIode == iode
            }
            SUBFRAME_4, SUBFRAME_5 ->         // always continue decoding for subframes 4-5
                canContinueDecoding = true
            else -> throw IllegalStateException("invalid subframe requested: $subframe")
        }
        return if (canContinueDecoding) {
            partiallyDecodedIntermediateEphemeris
        } else IntermediateEphemeris(prn)
    }

    /**
     * A representation of an intermediate ephemeris that can be fully decoded or partially decoded.
     */
    private class IntermediateEphemeris(prn: Byte) {
        val ephemerisObj = GpsEphemerisProto()
        private var subframesDecoded = 0
        fun reportDecodedSubframe(subframe: Int) {
            subframesDecoded = subframesDecoded or subframe
        }

        fun hasDecodedSubframe(subframe: Int): Boolean {
            return subframesDecoded and subframe == subframe
        }

        val isFullyDecoded: Boolean
            get() = (hasDecodedSubframe(SUBFRAME_1) && hasDecodedSubframe(SUBFRAME_2)
                    && hasDecodedSubframe(SUBFRAME_3))

        /**
         * Verifies that the received subframe info (IODE and IODC) matches the existing info (IODE and
         * IODC). For each subframe there is a given issueOfData that must match, this method abstracts
         * the logic to perform such check.
         *
         * @param prn The expected prn.
         * @param subframe The expected subframe.
         * @param issueOfData The issueOfData for the given subframe.
         *
         * @return [SubframeCheckResult] representing the state found.
         */
        fun findSubframeInfo(prn: Byte, subframe: Int, issueOfData: Int): SubframeCheckResult {
            if (ephemerisObj.prn != prn.toInt()) {
                return SubframeCheckResult(false /* hasSubframe */, false /* issueOfDataMatches */)
            }
            val issueOfDataMatches: Boolean
            issueOfDataMatches = when (subframe) {
                SUBFRAME_1 -> ephemerisObj.iodc == issueOfData
                SUBFRAME_2, SUBFRAME_3 -> ephemerisObj.iode == issueOfData
                SUBFRAME_4, SUBFRAME_5 ->           // subframes 4-5 do not have IOD to match, so we assume they always match
                    true
                else -> throw IllegalArgumentException("Invalid subframe provided: $subframe")
            }
            val hasDecodedSubframe = hasDecodedSubframe(subframe)
            return SubframeCheckResult(hasDecodedSubframe, issueOfDataMatches)
        }

        init {
            ephemerisObj.prn = prn.toInt()
        }
    }

    /**
     * Represents a result while finding a subframe in an intermediate [IntermediateEphemeris].
     */
    private class SubframeCheckResult(
            /**
             * The intermediate [IntermediateEphemeris] has the requested subframe.
             */
            val hasSubframe: Boolean,
            /**
             * The issue of data, associated with the requested subframe, matches the subframe found in the
             * intermediate state.
             */
            val issueOfDataMatches: Boolean) {
        /**
         * @return `true` if the requested subframe has been decoded in the intermediate state,
         * `false` otherwise.
         */
        val isSubframeDecoded: Boolean
            get() = hasSubframe && issueOfDataMatches
    }

    companion object {
        private const val IONOSPHERIC_PARAMETERS_PAGE_18_SV_ID: Byte = 56
        private const val WORD_SIZE_BITS = 30
        private const val WORD_PADDING_BITS = 2
        private const val BYTE_AS_BITS = 8
        private const val GPS_CYCLE_WEEKS = 1024
        private const val IODE_TO_IODC_MASK = 0xFF
        const val SUBFRAME_1 = 1 shl 0
        const val SUBFRAME_2 = 1 shl 1
        const val SUBFRAME_3 = 1 shl 2
        const val SUBFRAME_4 = 1 shl 3
        const val SUBFRAME_5 = 1 shl 4
        private val POW_2_4 = Math.pow(2.0, 4.0)
        private val POW_2_11 = Math.pow(2.0, 11.0)
        private val POW_2_12 = Math.pow(2.0, 12.0)
        private val POW_2_14 = Math.pow(2.0, 14.0)
        private val POW_2_16 = Math.pow(2.0, 16.0)
        private val POW_2_NEG_5 = Math.pow(2.0, -5.0)
        private val POW_2_NEG_19 = Math.pow(2.0, -19.0)
        private val POW_2_NEG_24 = Math.pow(2.0, -24.0)
        private val POW_2_NEG_27 = Math.pow(2.0, -27.0)
        private val POW_2_NEG_29 = Math.pow(2.0, -29.0)
        private val POW_2_NEG_30 = Math.pow(2.0, -30.0)
        private val POW_2_NEG_31 = Math.pow(2.0, -31.0)
        private val POW_2_NEG_33 = Math.pow(2.0, -33.0)
        private val POW_2_NEG_43 = Math.pow(2.0, -43.0)
        private val POW_2_NEG_55 = Math.pow(2.0, -55.0)
        private const val INTEGER_RANGE = 0xFFFFFFFFL

        // 3657 is the number of days between the unix epoch and GPS epoch as the GPS epoch started on
        // Jan 6, 1980
        private val GPS_EPOCH_AS_UNIX_EPOCH_MS = TimeUnit.DAYS.toMillis(3657)

        // A GPS Cycle is 1024 weeks, or 7168 days
        private val GPS_CYCLE_MS = TimeUnit.DAYS.toMillis(7168)

        /** Maximum possible number of GPS satellites  */
        const val MAX_NUMBER_OF_SATELLITES = 32
        private const val L1_CA_MESSAGE_LENGTH_BYTES = 40
        private const val IODC1_INDEX = 82
        private const val IODC1_LENGTH = 2
        private const val IODC2_INDEX = 210
        private const val IODC2_LENGTH = 8
        private const val WEEK_INDEX = 60
        private const val WEEK_LENGTH = 10
        private const val URA_INDEX = 72
        private const val URA_LENGTH = 4
        private const val SV_HEALTH_INDEX = 76
        private const val SV_HEALTH_LENGTH = 6
        private const val TGD_INDEX = 196
        private const val TGD_LENGTH = 8
        private const val AF2_INDEX = 240
        private const val AF2_LENGTH = 8
        private const val AF1_INDEX = 248
        private const val AF1_LENGTH = 16
        private const val AF0_INDEX = 270
        private const val AF0_LENGTH = 22
        private const val IODE1_INDEX = 60
        private const val IODE_LENGTH = 8
        private const val TOC_INDEX = 218
        private const val TOC_LENGTH = 16
        private const val CRS_INDEX = 68
        private const val CRS_LENGTH = 16
        private const val DELTA_N_INDEX = 90
        private const val DELTA_N_LENGTH = 16
        private const val M0_INDEX8 = 106
        private const val M0_INDEX24 = 120
        private const val CUC_INDEX = 150
        private const val CUC_LENGTH = 16
        private const val E_INDEX8 = 166
        private const val E_INDEX24 = 180
        private const val CUS_INDEX = 210
        private const val CUS_LENGTH = 16
        private const val A_INDEX8 = 226
        private const val A_INDEX24 = 240
        private const val TOE_INDEX = 270
        private const val TOE_LENGTH = 16
        private const val IODE2_INDEX = 270
        private const val CIC_INDEX = 60
        private const val CIC_LENGTH = 16
        private const val O0_INDEX8 = 76
        private const val O0_INDEX24 = 90
        private const val O_INDEX8 = 196
        private const val O_INDEX24 = 210
        private const val ODOT_INDEX = 240
        private const val ODOT_LENGTH = 24
        private const val CIS_INDEX = 120
        private const val CIS_LENGTH = 16
        private const val I0_INDEX8 = 136
        private const val I0_INDEX24 = 150
        private const val CRC_INDEX = 180
        private const val CRC_LENGTH = 16
        private const val IDOT_INDEX = 278
        private const val IDOT_LENGTH = 14
        private const val A0_INDEX = 68
        private const val A_B_LENGTH = 8
        private const val A1_INDEX = 76
        private const val A2_INDEX = 90
        private const val A3_INDEX = 98
        private const val B0_INDEX = 106
        private const val B1_INDEX = 120
        private const val B2_INDEX = 128
        private const val B3_INDEX = 136
        private const val WN_LS_INDEX = 226
        private const val DELTA_T_LS_INDEX = 240
        private const val TOT_LS_INDEX = 218
        private const val DN_LS_INDEX = 256
        private const val WNF_LS_INDEX = 248
        private const val DELTA_TF_LS_INDEX = 270
        private const val I0UTC_INDEX8 = 210
        private const val I0UTC_INDEX24 = 180
        private const val I1UTC_INDEX = 150

        /**
         * Extracts the requested bits from the raw stream.
         *
         * @param index Zero-based index of the first bit to extract.
         * @param length The length of the stream of bits to extract.
         * @param rawData The stream to extract data from.
         *
         * @return The bits requested always shifted to the least significant positions.
         */
        private fun extractBits(index: Int, length: Int, rawData: ByteArray?): Int {
            var result = 0
            for (i in 0 until length) {
                var workingIndex = index + i
                val wordIndex = workingIndex / WORD_SIZE_BITS
                // account for 2 bit padding for every 30bit word
                workingIndex += (wordIndex + 1) * WORD_PADDING_BITS
                val byteIndex = workingIndex / BYTE_AS_BITS
                val byteOffset = workingIndex % BYTE_AS_BITS
                val raw = rawData!![byteIndex]
                // account for zero-based indexing
                val shiftOffset = BYTE_AS_BITS - 1 - byteOffset
                val mask = 1 shl shiftOffset
                var bit: Int = raw and mask
                bit = bit shr shiftOffset

                // account for zero-based indexing
                result = result or (bit shl length - 1 - i)
            }
            return result
        }

        /**
         * Extracts an unsigned 32 bit word where the word is partitioned 8/24 bits.
         *
         * @param index8 The index of the first 8 bits used.
         * @param index24 The index of the last 24 bits used.
         * @param rawData The stream to extract data from.
         *
         * @return The bits requested represented as a long and stored in the least significant positions.
         */
        private fun buildUnsigned32BitsWordFrom8And24Words(index8: Int, index24: Int,
                                                           rawData: ByteArray?): Long {
            var result = extractBits(index8, 8, rawData).toLong() shl 24
            result = result or extractBits(index24, 24, rawData).toLong()
            return result
        }

        /**
         * Extracts a signed 32 bit word where the word is partitioned 8/24 bits with LSB first.
         *
         * @param index8 The index of the first 8 bits used.
         * @param index24 The index of the last 24 bits used.
         * @param rawData The stream to extract data from.
         * @return The bits requested represented as an int and stored in the least significant positions.
         */
        private fun buildSigned32BitsWordFrom8And24WordsWith8bitslsb(
                index8: Int, index24: Int, rawData: ByteArray?): Int {
            var result = extractBits(index24, 24, rawData) shl 8
            result = result or extractBits(index8, 8, rawData)
            return result
        }

        /**
         * Calculates the 2s complement for a specific number of bits of a given value
         *
         * @param value The set of bits to translate.
         * @param bits The number of bits to consider.
         *
         * @return The calculated 2s complement.
         */
        private fun getTwoComplement(value: Int, bits: Int): Int {
            val msbMask = 1 shl bits - 1
            val msb = value and msbMask
            if (msb == 0) {
                // the value is positive
                return value
            }
            val valueBitMask = (1 shl bits) - 1
            val extendedSignMask = INTEGER_RANGE.toInt() - valueBitMask
            return value or extendedSignMask
        }

        /**
         * Calculates the GPS week with rollovers. A rollover happens every 1024 weeks, beginning from GPS
         * epoch (January 6, 1980).
         *
         * @param gpsWeek The modulo-1024 GPS week.
         *
         * @return The absolute GPS week.
         */
        private fun getGpsWeekWithRollover(gpsWeek: Int): Int {
            val nowMs = System.currentTimeMillis()
            val elapsedTimeFromGpsEpochMs = nowMs - GPS_EPOCH_AS_UNIX_EPOCH_MS
            val rolloverCycles = elapsedTimeFromGpsEpochMs / GPS_CYCLE_MS
            val rolloverWeeks = rolloverCycles.toInt() * GPS_CYCLE_WEEKS
            return gpsWeek + rolloverWeeks
        }

        /**
         * Computes a nominal Sv Accuracy based on the URA index. This implementation is taken from
         * http://www.gps.gov/technical/icwg/IS-GPS-200D.pdf, section '20.3.3.3.1.3 Sv Accuracy'.
         *
         * @param uraIndex The URA Index
         *
         * @return A computed nominal Sv accuracy.
         */
        private fun computeNominalSvAccuracy(uraIndex: Int): Double {
            if (uraIndex < 0 || uraIndex >= 15) {
                return Double.NaN
            } else if (uraIndex == 1) {
                return 2.8
            } else if (uraIndex == 3) {
                return 5.7
            } else if (uraIndex == 5) {
                return 11.3
            }
            val exponent: Int
            exponent = if (uraIndex < 6) {
                1 + uraIndex / 2
            } else {
                uraIndex - 2
            }
            return Math.pow(2.0, exponent.toDouble())
        }
    }
}