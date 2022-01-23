package com.github.mattiadellepiane.gnssraw.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.github.mattiadellepiane.gnssraw.MeasurementProvider;
import com.github.mattiadellepiane.gnssraw.R;
import com.github.mattiadellepiane.gnssraw.listeners.ServerCommunication;
import com.github.mattiadellepiane.gnssraw.ui.main.tabs.FilesFragment;

public class SharedData{
    //Singleton
    private SharedData(){}

    public static SharedData getInstance(){
        return BillPughSingleton.INSTANCE;
    }

    private static class BillPughSingleton{
        private static final SharedData INSTANCE = new SharedData();
    }

    //Inizio
    public MeasurementProvider measurementProvider;
    private boolean listeningForMeasurements = false;
    private ServerCommunication serverCommunication;
    private Context context;
    private FilesFragment filesFragment;

    public FilesFragment getFilesFragment(){
        return filesFragment;
    }

    public void setFilesFragment(FilesFragment filesFragment){
        this.filesFragment = filesFragment;
    }

    public Context getContext(){
        return context;
    }

    public void setContext(Context context){
        this.context = context;
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
        measurementProvider.registerAll();
    }

    public void stopMeasurements(){
        setListeningForMeasurements(false);
        measurementProvider.unRegisterAll();
    }

    //Preferences Getters
    public boolean isSensorsEnabled() {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sensors_enabled", false);
    }
    public boolean isServerEnabled() {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("server_enabled", false);
    }
    public String getServerAddress() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("server_address", context.getString(R.string.server_ip_default));
    }
    public int getServerPort() {
        int port;
        try{
            port = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context)
                    .getString("server_port", context.getString(R.string.server_port_default)));
        }catch(NumberFormatException | ClassCastException e){
            port = Integer.parseInt(context.getString(R.string.server_port_default));
        }
        return port;
    }
}
