package com.github.mattiadellepiane.gnssraw.listeners;

import android.location.GnssClock;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.Location;
import android.os.Build;

import com.github.mattiadellepiane.gnssraw.R;
import com.github.mattiadellepiane.gnssraw.data.SharedData;

import java.util.Locale;

public abstract class MeasurementListener {

    private static final String COMMENT_START = "# ";
    private static final String VERSION_TAG = "Version: ";
    private static final String CURRENT_TIME_MILLIS = "%CURRENT_TIME_MILLIS%"; //placeholder


    protected abstract void initResources();
    protected abstract void releaseResources();
    protected abstract void write(String s);

    public final void startLogging(){
        initResources();
        write(getHeader());
    }

    public final void stopLogging(){
        releaseResources();
    }

    public void onLocationChanged(Location location) {
        String locationStream =
                String.format(
                        Locale.US,
                        "Fix,%s,%f,%f,%f,%f,%f,%d",
                        location.getProvider(),
                        location.getLatitude(),
                        location.getLongitude(),
                        location.getAltitude(),
                        location.getSpeed(),
                        location.getAccuracy(),
                        location.getTime());
        write(locationStream);
        //SharedData.getInstance().getMapsFragment().update(location);
    }

    public void onGnssMeasurementsReceived(GnssMeasurementsEvent event){
        GnssClock gnssClock = event.getClock();
        String clockInfo = getClockInfo(gnssClock); //Il clock è in comune a tutti i measurement dello stesso evento eccetto per currentTimeMillis
        StringBuilder sb = new StringBuilder("NB," + event.getMeasurements().size());
        for (GnssMeasurement measurement : event.getMeasurements()) {
            sb.append(",")
                    .append(clockInfo.replace(CURRENT_TIME_MILLIS, String.valueOf(System.currentTimeMillis())))
                    .append(getMeasurementInfo(measurement));
        }
        sb.append(",FB");
        //Inviare
        write(sb.toString());
    }

    public void onGnssNavigationMessageReceived(GnssNavigationMessage event){
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

    private String getClockInfo(GnssClock clock){
        String clockInfo = String.format(
                "Raw,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                CURRENT_TIME_MILLIS,
                clock.getTimeNanos(),
                clock.hasLeapSecond() ? clock.getLeapSecond() : "",
                clock.hasTimeUncertaintyNanos() ? clock.getTimeUncertaintyNanos() : "",
                clock.getFullBiasNanos(),
                clock.hasBiasNanos() ? clock.getBiasNanos() : "",
                clock.hasBiasUncertaintyNanos() ? clock.getBiasUncertaintyNanos() : "",
                clock.hasDriftNanosPerSecond() ? clock.getDriftNanosPerSecond() : "",
                clock.hasDriftUncertaintyNanosPerSecond()
                        ? clock.getDriftUncertaintyNanosPerSecond()
                        : "",
                clock.getHardwareClockDiscontinuityCount() + ",");
        return clockInfo;
    }

    private String getMeasurementInfo(GnssMeasurement measurement){
        String measurementInfo = String.format(
                "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                measurement.getSvid(),
                measurement.getTimeOffsetNanos(),
                measurement.getState(),
                measurement.getReceivedSvTimeNanos(),
                measurement.getReceivedSvTimeUncertaintyNanos(),
                measurement.getCn0DbHz(),
                measurement.getPseudorangeRateMetersPerSecond(),
                measurement.getPseudorangeRateUncertaintyMetersPerSecond(),
                measurement.getAccumulatedDeltaRangeState(),
                measurement.getAccumulatedDeltaRangeMeters(),
                measurement.getAccumulatedDeltaRangeUncertaintyMeters(),
                measurement.hasCarrierFrequencyHz() ? measurement.getCarrierFrequencyHz() : "",
                measurement.hasCarrierCycles() ? measurement.getCarrierCycles() : "",
                measurement.hasCarrierPhase() ? measurement.getCarrierPhase() : "",
                measurement.hasCarrierPhaseUncertainty()
                        ? measurement.getCarrierPhaseUncertainty()
                        : "",
                measurement.getMultipathIndicator(),
                measurement.hasSnrInDb() ? measurement.getSnrInDb() : "",
                measurement.getConstellationType(),
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                        && measurement.hasAutomaticGainControlLevelDb()
                        ? measurement.getAutomaticGainControlLevelDb()
                        : "");

        return measurementInfo;
    }

    private String getHeader(){
        StringBuilder header = new StringBuilder();
        header.append(COMMENT_START)
                .append("\n")
                .append(COMMENT_START)
                .append("Header Description:")
                .append("\n")
                .append(COMMENT_START)
                .append("\n")
                .append(COMMENT_START)
                .append(VERSION_TAG);
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String fileVersion =
                SharedData.getInstance().getContext().getString(R.string.app_version)
                        + " Platform: "
                        + Build.VERSION.RELEASE
                        + " "
                        + "Manufacturer: "
                        + manufacturer
                        + " "
                        + "Model: "
                        + model;
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
                        + "ConstellationType,AgcDb");
        header.append("\n")
                .append(COMMENT_START)
                .append("\n")
                .append(COMMENT_START)
                .append("Fix,Provider,Latitude,Longitude,Altitude,Speed,Accuracy,(UTC)TimeInMs")
                .append("\n")
                .append(COMMENT_START)
                /*.append("\n")
                .append(COMMENT_START)
                .append("Nav,Svid,Type,Status,MessageId,Sub-messageId,Data(Bytes)")
                .append("\n")
                .append(COMMENT_START)*/
                .append("\n");
                return header.toString();
    }

}