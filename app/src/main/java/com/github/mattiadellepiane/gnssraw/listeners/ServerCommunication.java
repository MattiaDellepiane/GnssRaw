package com.github.mattiadellepiane.gnssraw.listeners;


import android.location.GnssClock;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.GnssStatus;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.github.mattiadellepiane.gnssraw.MeasurementListener;
import com.github.mattiadellepiane.gnssraw.R;
import com.github.mattiadellepiane.gnssraw.data.SharedData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Locale;

public class ServerCommunication implements MeasurementListener {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private SharedData data; //address and port here
    private static final char RECORD_DELIMITER = ',';
    private static final String COMMENT_START = "# ";
    private static final String VERSION_TAG = "Version: ";

    public ServerCommunication(SharedData data){
        this.data = data;
        data.setServerCommunication(this);
    }

    private String getDebugTag(){
        return data.getContext().getString(R.string.debug_tag);
    }

    public void startCommunication(){
        Log.v(getDebugTag(), "Listening: " + data.isListeningForMeasurements() + ", serverenabled: " + data.isServerEnabled());

        if(data.isListeningForMeasurements() && data.isServerEnabled()) {
            new Thread(() -> {
                try {
                    Log.v(getDebugTag(), "Server ip: " + data.getServerAddress());
                    socket = new Socket(data.getServerAddress(), data.getServerPort());
                    out = new PrintWriter(socket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    startNewLog();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void sendMessage(String s){
        if(data.isListeningForMeasurements() && data.isServerEnabled()) {
            Log.v(getDebugTag(), "Invio messaggio al server");
            if(out != null) {
                new Thread(() -> {
                    out.println(s);
                }).start();
            }
            Log.v(getDebugTag(), "Messaggio inviato al server");
        }
    }

    public void stopCommunication(){
        if(out != null)
            out.close();
        try {
            if(in != null)
                in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startNewLog(){
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
                data.getContext().getString(R.string.app_version)
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
                .append("Raw,ElapsedRealtimeMillis,TimeNanos,LeapSecond,TimeUncertaintyNanos,FullBiasNanos,"
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
                .append("\n")
                .append(COMMENT_START)
                .append("Nav,Svid,Type,Status,MessageId,Sub-messageId,Data(Bytes)")
                .append("\n")
                .append(COMMENT_START)
                .append("\n");

        out.println(header.toString());
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
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
            sendMessage(locationStream);

    }

    @Override
    public void onLocationStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onGnssMeasurementsReceived(GnssMeasurementsEvent event) {
        GnssClock gnssClock = event.getClock();
        for (GnssMeasurement measurement : event.getMeasurements()) {
            try {
                writeGnssMeasurementToFile(gnssClock, measurement);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onGnssMeasurementsStatusChanged(int status) {

    }

    @Override
    public void onGnssNavigationMessageReceived(GnssNavigationMessage navigationMessage) {
        StringBuilder builder = new StringBuilder("Nav");
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
        sendMessage(builder.toString());
    }

    @Override
    public void onGnssNavigationMessageStatusChanged(int status) {

    }

    @Override
    public void onGnssStatusChanged(GnssStatus gnssStatus) {

    }

    @Override
    public void onListenerRegistration(String listener, boolean result) {

    }

    @Override
    public void onNmeaReceived(long l, String s) {

    }

    @Override
    public void onTTFFReceived(long l) {

    }

    private void writeGnssMeasurementToFile(GnssClock clock, GnssMeasurement measurement)
            throws IOException {
        String clockStream =
                String.format(
                        "Raw,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                        SystemClock.elapsedRealtime(),
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

        String measurementStream =
                String.format(
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

        String total = clockStream + measurementStream;
        sendMessage(total);
    }

    public boolean isReachable() {
        Socket s = null;
        boolean reachable = false;
        try {
            s = new Socket();
            s.connect(new InetSocketAddress(data.getServerAddress(), data.getServerPort()),3000);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            out.println("PING");
            String response = in.readLine();
            if(response != null && response.equalsIgnoreCase("OK")){
                reachable = true;
            }
            out.close();
        } catch (IOException e) {
            return false;
        }
        return reachable;
    }
}
