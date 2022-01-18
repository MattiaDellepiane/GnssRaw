package com.github.mattiadellepiane.gnssraw.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.github.mattiadellepiane.gnssraw.MeasurementProvider;
import com.github.mattiadellepiane.gnssraw.R;
import com.github.mattiadellepiane.gnssraw.listeners.ServerCommunication;

/*
Shared data between the three fragments (immune to views lifecycles)
 */
public class SharedData {

    private boolean hasPermission = false;
    public MeasurementProvider measurementProvider;
    private Context context;
    private boolean listeningForMeasurements = false;
    SharedPreferences preferences;
    private ServerCommunication serverCommunication;

    public SharedData(Context context){
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Context getContext(){
        return context;
    }

    public boolean isListeningForMeasurements() {
        return listeningForMeasurements;
    }

    public void setListeningForMeasurements(boolean listeningForMeasurements) {
        this.listeningForMeasurements = listeningForMeasurements;
    }

    public void setServerCommunication(ServerCommunication serverCommunication) {
        this.serverCommunication = serverCommunication;
    }
    public ServerCommunication getServerCommunication(){
        return serverCommunication;
    }

    public void startMeasurements(){
        setListeningForMeasurements(true);
        serverCommunication.startCommunication();
        measurementProvider.registerAll();
    }

    public void stopMeasurements(){
        setListeningForMeasurements(false);
        serverCommunication.stopCommunication();
        measurementProvider.unRegisterAll();
    }

    //Preferences Getters
    public boolean isSensorsEnabled() {
        return preferences.getBoolean("sensors_enabled", false);
    }
    public boolean isServerEnabled() {
        return preferences.getBoolean("server_enabled", false);
    }
    public String getServerAddress() {
        return preferences.getString("server_address", context.getString(R.string.server_ip_default));
    }
    public int getServerPort() {
        int port;
        try{
            port = Integer.valueOf(preferences.getString("server_port", context.getString(R.string.server_port_default)));
        }catch(NumberFormatException | ClassCastException e){
            port = Integer.valueOf(R.string.server_port_default);
        }
        return port;
    }

}
