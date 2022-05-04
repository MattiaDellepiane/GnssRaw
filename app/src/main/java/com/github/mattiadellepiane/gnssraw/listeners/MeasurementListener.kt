package com.github.mattiadellepiane.gnssraw.listeners

import com.github.mattiadellepiane.gnssraw.R
import com.github.mattiadellepiane.gnssraw.data.SharedData
import java.util.Locale
import android.os.Build
import java.lang.StringBuilder
import android.location.*

abstract class MeasurementListener {
    protected abstract fun initResources()
    protected abstract fun releaseResources()
    protected abstract fun write(s: String?)
    fun startLogging() {
        initResources()
        //write(getHeader());
    }

    fun stopLogging() {
        releaseResources()
    }

    open fun onLocationChanged(location: Location) {
        val locationStream = String.format(
                Locale.US,
                "Fix,%s,%f,%f,%f,%f,%f,%d",
                location.provider,
                location.latitude,
                location.longitude,
                location.altitude,
                location.speed,
                location.accuracy,
                location.time)
        write(locationStream)
        //SharedData.getInstance().getMapsFragment().update(location);
    }

    open fun onGnssMeasurementsReceived(event: GnssMeasurementsEvent) {
        val gnssClock = event.clock
        val clockInfo = getClockInfo(gnssClock) //Il clock è in comune a tutti i measurement dello stesso evento eccetto per currentTimeMillis
        val sb = StringBuilder("NB," + event.measurements.size)
        for (measurement in event.measurements) {
            sb.append(",")
                    .append(clockInfo.replace(CURRENT_TIME_MILLIS, System.currentTimeMillis().toString()))
                    .append(getMeasurementInfo(measurement))
        }
        sb.append(",FB")
        //Inviare
        write(sb.toString())
    }

    open fun onGnssNavigationMessageReceived(event: GnssNavigationMessage) {
        /*StringBuilder builder = new StringBuilder("Nav");
        builder.append(RECORD_DELIMITER);
        builder.append(navigationMessage.getSvid());
        builder.append(RECORD_DELIMITER);
        builder.append(navigationMessage.getType());
        builder.append(RECORD_DELIMITER);

        int status = navigationMessage.getStatus();
        builder.append(status);
        builder.append(RECORD_DELIMITER);
        builder.append(navigationMessage.getMessageId());
        builder.append(RECORD_DELIMITER);
        builder.append(navigationMessage.getSubmessageId());
        byte[] data = navigationMessage.getData();
        for (byte word : data) {
            builder.append(RECORD_DELIMITER);
            builder.append(word);
        }
        write(builder.toString());*/
    }

    private fun getClockInfo(clock: GnssClock): String {
        return String.format(
                "Raw,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                CURRENT_TIME_MILLIS,
                clock.timeNanos,
                if (clock.hasLeapSecond()) clock.leapSecond else "",
                if (clock.hasTimeUncertaintyNanos()) clock.timeUncertaintyNanos else "",
                clock.fullBiasNanos,
                if (clock.hasBiasNanos()) clock.biasNanos else "",
                if (clock.hasBiasUncertaintyNanos()) clock.biasUncertaintyNanos else "",
                if (clock.hasDriftNanosPerSecond()) clock.driftNanosPerSecond else "",
                if (clock.hasDriftUncertaintyNanosPerSecond()) clock.driftUncertaintyNanosPerSecond else "", clock.hardwareClockDiscontinuityCount.toString() + ",")
    }

    private fun getMeasurementInfo(measurement: GnssMeasurement): String {
        return String.format(
                "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                measurement.svid,
                measurement.timeOffsetNanos,
                measurement.state,
                measurement.receivedSvTimeNanos,
                measurement.receivedSvTimeUncertaintyNanos,
                measurement.cn0DbHz,
                measurement.pseudorangeRateMetersPerSecond,
                measurement.pseudorangeRateUncertaintyMetersPerSecond,
                measurement.accumulatedDeltaRangeState,
                measurement.accumulatedDeltaRangeMeters,
                measurement.accumulatedDeltaRangeUncertaintyMeters,
                if (measurement.hasCarrierFrequencyHz()) measurement.carrierFrequencyHz else "",
                if (measurement.hasCarrierCycles()) measurement.carrierCycles else "",
                if (measurement.hasCarrierPhase()) measurement.carrierPhase else "",
                if (measurement.hasCarrierPhaseUncertainty()) measurement.carrierPhaseUncertainty else "",
                measurement.multipathIndicator,
                if (measurement.hasSnrInDb()) measurement.snrInDb else "",
                measurement.constellationType,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                        && measurement.hasAutomaticGainControlLevelDb()) measurement.automaticGainControlLevelDb else "")
    }

    /*.append("\n")
                .append(COMMENT_START)
                .append("Nav,Svid,Type,Status,MessageId,Sub-messageId,Data(Bytes)")
                .append("\n")
                .append(COMMENT_START)*/
    private val header: String
        private get() {
            val header = StringBuilder()
            header.append(COMMENT_START)
                    .append("\n")
                    .append(COMMENT_START)
                    .append("Header Description:")
                    .append("\n")
                    .append(COMMENT_START)
                    .append("\n")
                    .append(COMMENT_START)
                    .append(VERSION_TAG)
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            val fileVersion: String = (SharedData.instance.context!!.getString(R.string.app_version)
                    + " Platform: "
                    + Build.VERSION.RELEASE
                    + " "
                    + "Manufacturer: "
                    + manufacturer
                    + " "
                    + "Model: "
                    + model)
            header.append(fileVersion)
                    .append("\n")
                    .append(COMMENT_START)
                    .append("\n")
                    .append(COMMENT_START)
                    .append("Raw,UTCTimeMillis,TimeNanos,LeapSecond,TimeUncertaintyNanos,FullBiasNanos,"
                            + "BiasNanos,BiasUncertaintyNanos,DriftNanosPerSecond,DriftUncertaintyNanosPerSecond,"
                            + "HardwareClockDiscontinuityCount,Svid,TimeOffsetNanos,State,ReceivedSvTimeNanos,"
                            + "ReceivedSvTimeUncertaintyNanos,Cn0DbHz,PseudorangeRateMetersPerSecond,"
                            + "PseudorangeRateUncertaintyMetersPerSecond,"
                            + "AccumulatedDeltaRangeState,AccumulatedDeltaRangeMeters,"
                            + "AccumulatedDeltaRangeUncertaintyMeters,CarrierFrequencyHz,CarrierCycles,"
                            + "CarrierPhase,CarrierPhaseUncertainty,MultipathIndicator,SnrInDb,"
                            + "ConstellationType,AgcDb")
            header.append("\n")
                    .append(COMMENT_START)
                    .append("\n")
                    .append(COMMENT_START)
                    .append("Fix,Provider,Latitude,Longitude,Altitude,Speed,Accuracy,(UTC)TimeInMs")
                    .append("\n")
                    .append(COMMENT_START) /*.append("\n")
                .append(COMMENT_START)
                .append("Nav,Svid,Type,Status,MessageId,Sub-messageId,Data(Bytes)")
                .append("\n")
                .append(COMMENT_START)*/
                    .append("\n")
            return header.toString()
        }

    companion object {
        private const val COMMENT_START = "# "
        private const val VERSION_TAG = "Version: "
        private const val CURRENT_TIME_MILLIS = "%CURRENT_TIME_MILLIS%" //placeholder
    }
}