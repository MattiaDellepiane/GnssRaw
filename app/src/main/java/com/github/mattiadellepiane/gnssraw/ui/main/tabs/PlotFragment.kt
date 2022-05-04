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
//Edited 2 methods access modifiers from protected to public (updateCnoTab() and updatePseudorangeResidualTab())
//and added method to restart the chart view
package com.github.mattiadellepiane.gnssraw.ui.main.tabs

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
import android.util.ArrayMap
import androidx.fragment.app.Fragment
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import java.util.concurrent.TimeUnit

/** A plot fragment to show real-time Gnss analysis migrated from GnssAnalysis Tool.  */
class PlotFragment : Fragment() {
    private var mChartView: GraphicalView? = null

    /** The average of the average of strongest satellite signal strength over history  */
    private var mAverageCn0 = 0.0

    /** Total number of [GnssMeasurementsEvent] has been received */
    private var mMeasurementCount = 0
    private var mInitialTimeSeconds = -1.0
    private var mAnalysisView: TextView? = null
    private var mLastTimeReceivedSeconds = 0.0
    private val mColorMap = ColorMap()
    private var mDataSetManager: DataSetManager? = null
    private var mCurrentRenderer: XYMultipleSeriesRenderer? = null
    private var mLayout: LinearLayout? = null
    private var mCurrentTab = 0
    private var tabSpinner: Spinner? = null
    private var spinner: Spinner? = null
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val plotView = inflater.inflate(R.layout.fragment_plot, container, false /* attachToRoot */)
        mDataSetManager = DataSetManager(NUMBER_OF_TABS, NUMBER_OF_CONSTELLATIONS, mColorMap)

        // Set UI elements handlers
        spinner = plotView.findViewById(R.id.constellation_spinner)
        tabSpinner = plotView.findViewById(R.id.tab_spinner)
        val spinnerOnSelectedListener: AdapterView.OnItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                mCurrentTab = tabSpinner!!.selectedItemPosition
                val renderer = mDataSetManager!!.getRenderer(mCurrentTab, spinner!!.selectedItemPosition)
                val dataSet = mDataSetManager!!.getDataSet(mCurrentTab, spinner!!.selectedItemPosition)
                if (mLastTimeReceivedSeconds > TIME_INTERVAL_SECONDS) {
                    renderer.xAxisMax = mLastTimeReceivedSeconds
                    renderer.xAxisMin = mLastTimeReceivedSeconds - TIME_INTERVAL_SECONDS
                }
                mCurrentRenderer = renderer
                mLayout!!.removeAllViews()
                mChartView = ChartFactory.getLineChartView(context, dataSet, renderer)
                mLayout!!.addView(mChartView)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinner!!.onItemSelectedListener = spinnerOnSelectedListener
        tabSpinner!!.onItemSelectedListener = spinnerOnSelectedListener

        // Set up the Graph View
        mCurrentRenderer = mDataSetManager!!.getRenderer(mCurrentTab, DATA_SET_INDEX_ALL)
        val currentDataSet = mDataSetManager!!.getDataSet(mCurrentTab, DATA_SET_INDEX_ALL)
        mChartView = ChartFactory.getLineChartView(context, currentDataSet, mCurrentRenderer)
        mAnalysisView = plotView.findViewById(R.id.analysis)
        mAnalysisView!!.setTextColor(Color.BLACK)
        mLayout = plotView.findViewById(R.id.plot)
        mLayout!!.addView(mChartView)
        SharedData.instance.plotFragment = this
        /*if(savedInstanceState == null)
      SharedData.getInstance().setPlotFragment(this);
    else
      Log.v("prova", "esisteva già");*/return plotView
    }

    fun restartChart() {
        if (mChartView != null) {
            mLayout!!.removeView(mChartView)
        }
        mDataSetManager = DataSetManager(NUMBER_OF_TABS, NUMBER_OF_CONSTELLATIONS, mColorMap)
        val currentDataSet = mDataSetManager!!.getDataSet(mCurrentTab, DATA_SET_INDEX_ALL)
        mCurrentTab = tabSpinner!!.selectedItemPosition
        val renderer = mDataSetManager!!.getRenderer(mCurrentTab, spinner!!.selectedItemPosition)
        val dataSet = mDataSetManager!!.getDataSet(mCurrentTab, spinner!!.selectedItemPosition)
        if (mLastTimeReceivedSeconds > TIME_INTERVAL_SECONDS) {
            renderer.xAxisMax = mLastTimeReceivedSeconds
            renderer.xAxisMin = mLastTimeReceivedSeconds - TIME_INTERVAL_SECONDS
        }
        mCurrentRenderer = renderer
        mChartView = ChartFactory.getLineChartView(context, dataSet, mCurrentRenderer)
        mLayout!!.addView(mChartView)
    }

    /**
     * Updates the CN0 versus Time plot data from a [GnssMeasurement]
     */
    fun updateCnoTab(event: GnssMeasurementsEvent) {
        if (context == null) return
        val timeInSeconds = TimeUnit.NANOSECONDS.toSeconds(event.clock.timeNanos)
        if (mInitialTimeSeconds < 0) {
            mInitialTimeSeconds = timeInSeconds.toDouble()
        }

        // Building the texts message in analysis text view
        val measurements = sortByCarrierToNoiseRatio(ArrayList(event.measurements))
        val builder = SpannableStringBuilder()
        var currentAverage = 0.0
        if (measurements.size >= NUMBER_OF_STRONGEST_SATELLITES) {
            mAverageCn0 = ((mAverageCn0 * mMeasurementCount
                    + (measurements[0].cn0DbHz
                    + measurements[1].cn0DbHz
                    + measurements[2].cn0DbHz
                    + measurements[3].cn0DbHz)
                    / NUMBER_OF_STRONGEST_SATELLITES)
                    / ++mMeasurementCount)
            currentAverage = ((measurements[0].cn0DbHz
                    + measurements[1].cn0DbHz
                    + measurements[2].cn0DbHz
                    + measurements[3].cn0DbHz)
                    / NUMBER_OF_STRONGEST_SATELLITES)
        }
        builder.append(getString(R.string.history_average_hint,
                """
                    ${sDataFormat.format(mAverageCn0)}
                    
                    """.trimIndent()))
        builder.append(getString(R.string.current_average_hint,
                """
                    ${sDataFormat.format(currentAverage)}
                    
                    """.trimIndent()))
        var i = 0
        while (i < NUMBER_OF_STRONGEST_SATELLITES && i < measurements.size) {
            val start = builder.length
            builder.append(
                    """
                        ${mDataSetManager!!.getConstellationPrefix(measurements[i].constellationType)}${measurements[i].svid}: ${sDataFormat.format(measurements[i].cn0DbHz)}
                        
                        """.trimIndent())
            val end = builder.length
            builder.setSpan(
                    ForegroundColorSpan(
                            mColorMap.getColor(
                                    measurements[i].svid, measurements[i].constellationType)),
                    start,
                    end,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            i++
        }
        builder.append(getString(R.string.satellite_number_sum_hint, measurements.size))
        mAnalysisView!!.text = builder

        // Adding incoming data into Dataset
        mLastTimeReceivedSeconds = timeInSeconds - mInitialTimeSeconds
        for (measurement in measurements) {
            val constellationType = measurement.constellationType
            val svID = measurement.svid
            if (constellationType != GnssStatus.CONSTELLATION_UNKNOWN) {
                mDataSetManager!!.addValue(
                        CN0_TAB,
                        constellationType,
                        svID,
                        mLastTimeReceivedSeconds,
                        measurement.cn0DbHz)
            }
        }
        mDataSetManager!!.fillInDiscontinuity(CN0_TAB, mLastTimeReceivedSeconds)

        // Checks if the plot has reached the end of frame and resize
        if (mLastTimeReceivedSeconds > mCurrentRenderer!!.xAxisMax) {
            mCurrentRenderer!!.xAxisMax = mLastTimeReceivedSeconds
            mCurrentRenderer!!.xAxisMin = mLastTimeReceivedSeconds - TIME_INTERVAL_SECONDS
        }
        mChartView!!.invalidate()
    }

    /**
     * Updates the pseudorange residual plot from residual results calculated by
     * {link RealTimePositionVelocityCalculator}
     *
     * @param residuals An array of MAX_NUMBER_OF_SATELLITES elements where indexes of satellites was
     * not seen are fixed with `Double.NaN` and indexes of satellites what were seen
     * are filled with pseudorange residual in meters
     * @param timeInSeconds the time at which measurements are received
     */
    fun updatePseudorangeResidualTab(residuals: DoubleArray?, timeInSeconds: Double) {
        if (context == null) return
        val timeSinceLastMeasurement = timeInSeconds - mInitialTimeSeconds
        for (i in 1..GpsNavigationMessageStore.Companion.MAX_NUMBER_OF_SATELLITES) {
            if (!java.lang.Double.isNaN(residuals!![i - 1])) {
                mDataSetManager!!.addValue(
                        PR_RESIDUAL_TAB,
                        GnssStatus.CONSTELLATION_GPS,
                        i,
                        timeSinceLastMeasurement,
                        residuals[i - 1])
            }
        }
        mDataSetManager!!.fillInDiscontinuity(PR_RESIDUAL_TAB, timeSinceLastMeasurement)
    }

    private fun sortByCarrierToNoiseRatio(measurements: List<GnssMeasurement>): List<GnssMeasurement> {
        Collections.sort(
                measurements
        ) { o1, o2 -> java.lang.Double.compare(o2.cn0DbHz, o1.cn0DbHz) }
        return measurements
    }

    /**
     * An utility class provides and keeps record of all color assignments to the satellite in the
     * plots. Each satellite will receive a unique color assignment through out every graph.
     */
    private class ColorMap {
        private val mColorMap = ArrayMap<Int, Int>()
        private var mColorsAssigned = 0
        private val mRandom = Random()
        fun getColor(svId: Int, constellationType: Int): Int {
            // Assign the color from Kelly's 21 contrasting colors to satellites first, if all color
            // has been assigned, use a random color and record in {@link mColorMap}.
            if (mColorMap.containsKey(constellationType * 1000 + svId)) {
                return mColorMap[getUniqueSatelliteIdentifier(constellationType, svId)]!!
            }
            if (mColorsAssigned < CONTRASTING_COLORS.size) {
                val color = Color.parseColor(CONTRASTING_COLORS[mColorsAssigned++])
                mColorMap[getUniqueSatelliteIdentifier(constellationType, svId)] = color
                return color
            }
            val color = Color.argb(255, mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256))
            mColorMap[getUniqueSatelliteIdentifier(constellationType, svId)] = color
            return color
        }

        companion object {
            /**
             * Source of Kelly's contrasting colors:
             * https://medium.com/@rjurney/kellys-22-colours-of-maximum-contrast-58edb70c90d1
             */
            private val CONTRASTING_COLORS = arrayOf(
                    "#222222", "#F3C300", "#875692", "#F38400", "#A1CAF1", "#BE0032", "#C2B280", "#848482",
                    "#008856", "#E68FAC", "#0067A5", "#F99379", "#604E97", "#F6A600", "#B3446C", "#DCD300",
                    "#882D17", "#8DB600", "#654522", "#E25822", "#2B3D26"
            )
        }
    }

    /**
     * An utility class stores and maintains all the data sets and corresponding renders.
     * We use 0 as the `dataSetIndex` of all constellations and 1 - 6 as the
     * `dataSetIndex` of each satellite constellations
     */
    private class DataSetManager(numberOfTabs: Int, numberOfConstellations: Int, colorMap: ColorMap) {
        private val mSatelliteIndex: Array<MutableList<ArrayMap<Int, Int>>>
        private val mSatelliteConstellationIndex: Array<MutableList<ArrayMap<Int, Int>>>
        private val mDataSetList: Array<MutableList<XYMultipleSeriesDataset>>
        private val mRendererList: Array<MutableList<XYMultipleSeriesRenderer>>
        private val mColorMap: ColorMap

        // The constellation type should range from 1 to 6
        fun getConstellationPrefix(constellationType: Int): String {
            return if (constellationType <= GnssStatus.CONSTELLATION_UNKNOWN
                    || constellationType > NUMBER_OF_CONSTELLATIONS) {
                ""
            } else CONSTELLATION_PREFIX[constellationType - 1]
        }

        /** Returns the multiple series data set at specific tab and index  */
        fun getDataSet(tab: Int, dataSetIndex: Int): XYMultipleSeriesDataset {
            return mDataSetList[tab][dataSetIndex]
        }

        /** Returns the multiple series renderer set at specific tab and index  */
        fun getRenderer(tab: Int, dataSetIndex: Int): XYMultipleSeriesRenderer {
            return mRendererList[tab][dataSetIndex]
        }

        /**
         * Adds a value into the both the data set containing all constellations and individual data set
         * of the constellation of the satellite
         */
        fun addValue(tab: Int, constellationType: Int, svID: Int,
                     timeInSeconds: Double, value: Double) {
            var value = value
            val dataSetAll = getDataSet(tab, DATA_SET_INDEX_ALL)
            val rendererAll = getRenderer(tab, DATA_SET_INDEX_ALL)
            value = sDataFormat.format(value).toDouble()
            if (hasSeen(constellationType, svID, tab)) {
                // If the satellite has been seen before, we retrieve the dataseries it is add and add new
                // data
                dataSetAll
                        .getSeriesAt(mSatelliteIndex[tab][constellationType][svID]!!)
                        .add(timeInSeconds, value)
                mDataSetList[tab][constellationType]
                        .getSeriesAt(mSatelliteConstellationIndex[tab][constellationType][svID]!!)
                        .add(timeInSeconds, value)
            } else {
                // If the satellite has not been seen before, we create new dataset and renderer before
                // adding data
                mSatelliteIndex[tab][constellationType][svID] = dataSetAll.seriesCount
                mSatelliteConstellationIndex[tab][constellationType][svID] = mDataSetList[tab][constellationType].seriesCount
                val tempSeries = XYSeries(CONSTELLATION_PREFIX[constellationType - 1] + svID)
                tempSeries.add(timeInSeconds, value)
                dataSetAll.addSeries(tempSeries)
                mDataSetList[tab][constellationType].addSeries(tempSeries)
                val tempRenderer = XYSeriesRenderer()
                tempRenderer.lineWidth = 5f
                tempRenderer.color = mColorMap.getColor(svID, constellationType)
                rendererAll.addSeriesRenderer(tempRenderer)
                mRendererList[tab][constellationType].addSeriesRenderer(tempRenderer)
            }
        }

        /**
         * Creates a discontinuity of the satellites that has been seen but not reported in this batch
         * of measurements
         */
        fun fillInDiscontinuity(tab: Int, referenceTimeSeconds: Double) {
            for (dataSet in mDataSetList[tab]) {
                for (i in 0 until dataSet.seriesCount) {
                    if (dataSet.getSeriesAt(i).maxX < referenceTimeSeconds) {
                        dataSet.getSeriesAt(i).add(referenceTimeSeconds, MathHelper.NULL_VALUE)
                    }
                }
            }
        }

        /**
         * Returns a boolean indicating whether the input satellite has been seen.
         */
        private fun hasSeen(constellationType: Int, svID: Int, tab: Int): Boolean {
            return mSatelliteIndex[tab][constellationType].containsKey(svID)
        }

        /**
         * Set up a [XYMultipleSeriesRenderer] with the specs customized per plot tab.
         */
        private fun setUpRenderer(renderer: XYMultipleSeriesRenderer, tabNumber: Int) {
            renderer.xAxisMin = 0.0
            renderer.xAxisMax = 60.0
            renderer.yAxisMin = RENDER_HEIGHTS[tabNumber][0].toDouble()
            renderer.yAxisMax = RENDER_HEIGHTS[tabNumber][1].toDouble()
            renderer.setYAxisAlign(Align.RIGHT, 0)
            renderer.legendTextSize = 30f
            renderer.labelsTextSize = 30f
            renderer.setYLabelsColor(0, Color.BLACK)
            renderer.xLabelsColor = Color.BLACK
            renderer.isFitLegend = true
            renderer.isShowGridX = true
            renderer.margins = intArrayOf(10, 10, 30, 10)
            // setting the plot untouchable
            renderer.setZoomEnabled(false, false)
            renderer.setPanEnabled(false, true)
            renderer.isClickEnabled = false
            renderer.marginsColor = Color.WHITE
            renderer.chartTitle = SharedData.instance.context!!.getResources()
                    .getStringArray(R.array.plot_titles).get(tabNumber)
            renderer.chartTitleTextSize = 50f
        }

        companion object {
            /** The Y min and max of each plot  */
            private val RENDER_HEIGHTS = arrayOf(intArrayOf(5, 45), intArrayOf(-60, 60))

            /**
             *
             *  * A list of constellation prefix
             *  * G : GPS, US Constellation
             *  * S : Satellite-based Augmentation System
             *  * R : GLONASS, Russia Constellation
             *  * J : QZSS, Japan Constellation
             *  * C : BEIDOU China Constellation
             *  * E : GALILEO EU Constellation
             *
             */
            private val CONSTELLATION_PREFIX = arrayOf("G", "S", "R", "J", "C", "E")
        }

        init {
            mDataSetList = Array<MutableList<ArrayMap<Int, Int>>>(numberOfTabs){ MutableList<ArrayMap<Int, Int>> {ArrayMap<Int, Int>} }
            mRendererList = arrayOfNulls<ArrayList<*>>(numberOfTabs)
            mSatelliteIndex = Array(numberOfTabs){ ArrayList<ArrayMap<Int, Int>> {ArrayMap<Int, Int>} }
            mSatelliteConstellationIndex = arrayOfNulls<ArrayList<*>>(numberOfTabs)
            mColorMap = colorMap

            // Preparing data sets and renderer for all six constellations
            for (i in 0 until numberOfTabs) {
                mDataSetList[i] = ArrayList()
                mRendererList[i] = ArrayList()
                mSatelliteIndex[i] = ArrayList()
                mSatelliteConstellationIndex[i] = ArrayList()
                for (k in 0..numberOfConstellations) {
                    mSatelliteIndex[i]!!.add(ArrayMap())
                    mSatelliteConstellationIndex[i].add(ArrayMap())
                    val tempRenderer = XYMultipleSeriesRenderer()
                    setUpRenderer(tempRenderer, i)
                    mRendererList[i]!!.add(tempRenderer)
                    val tempDataSet = XYMultipleSeriesDataset()
                    mDataSetList[i]!!.add(tempDataSet)
                }
            }
        }
    }

    companion object {
        /** Total number of kinds of plot tabs  */
        private const val NUMBER_OF_TABS = 2

        /** The position of the CN0 over time plot tab  */
        private const val CN0_TAB = 0

        /** The position of the prearrange residual plot tab */
        private const val PR_RESIDUAL_TAB = 1

        /** The number of Gnss constellations  */
        private const val NUMBER_OF_CONSTELLATIONS = 6

        /** The X range of the plot, we are keeping the latest one minute visible  */
        private const val TIME_INTERVAL_SECONDS = 60.0

        /** The index in data set we reserved for the plot containing all constellations  */
        private const val DATA_SET_INDEX_ALL = 0

        /** The number of satellites we pick for the strongest satellite signal strength calculation  */
        private const val NUMBER_OF_STRONGEST_SATELLITES = 4

        /** Data format used to format the data in the text view  */
        private val sDataFormat = DecimalFormat("##.#", DecimalFormatSymbols(Locale.US))
        private fun getUniqueSatelliteIdentifier(constellationType: Int, svID: Int): Int {
            return constellationType * 1000 + svID
        }
    }
}