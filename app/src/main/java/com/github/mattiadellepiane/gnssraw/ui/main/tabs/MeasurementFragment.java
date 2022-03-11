package com.github.mattiadellepiane.gnssraw.ui.main.tabs;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mattiadellepiane.gnssraw.R;
import com.github.mattiadellepiane.gnssraw.data.SharedData;
import com.github.mattiadellepiane.gnssraw.debug.AllCallbacksBase;
import com.github.mattiadellepiane.gnssraw.services.BackgroundMeasurementService;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MeasurementFragment extends Fragment {

    private TextView serverStatus, sendingData;
    private MaterialButton startStop;
    private Button upMap;

    public MeasurementFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_measurements, container, false);
        serverStatus = fragment.findViewById(R.id.serverStatus);
        sendingData = fragment.findViewById(R.id.sendingData);
        upMap = fragment.findViewById(R.id.updateMap);

        upMap.setOnClickListener(view -> {
            SharedData.getInstance().getServerCommunication().getLocation();
        });

        startStop = fragment.findViewById(R.id.startStop);
        startStop.setOnClickListener(view -> {
            if(SharedData.getInstance().isListeningForMeasurements()){
                sendingData.setVisibility(View.INVISIBLE);
                startStop.setText("START");
                startStop.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_start));
                startStop.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
                upMap.setEnabled(false);
                stopMeasurementService();
            }
            else{
                sendingData.setVisibility(View.VISIBLE);
                startStop.setText("STOP");
                startStop.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_stop));
                startStop.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
                upMap.setEnabled(true);
                if(SharedData.getInstance().getPlotFragment() != null)
                    SharedData.getInstance().getPlotFragment().restartChart();
                startMeasurementService();
            }

        });

        if (SharedData.getInstance().isListeningForMeasurements()) {
            sendingData.setVisibility(View.VISIBLE);
            startStop.setText("STOP");
            startStop.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_stop));
            startStop.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
        }

        Button pingServerButton = fragment.findViewById(R.id.pingServerButton);
        pingServerButton.setOnClickListener(view -> checkServerStatus());

        checkServerStatus();
        return fragment;
    }

    private void startMeasurementService(){
        Context context = getContext().getApplicationContext();
        Intent intent = new Intent(context, BackgroundMeasurementService.class); // Build the intent for the service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        }
    }

    private void stopMeasurementService(){
        Context context = getContext().getApplicationContext();
        Intent serviceIntent = new Intent(context, BackgroundMeasurementService.class);
        context.stopService(serviceIntent);
    }

    @Override
    public void onDestroy() {
        Context context = getContext().getApplicationContext();
        Intent serviceIntent = new Intent(context, BackgroundMeasurementService.class);
        context.stopService(serviceIntent);
        super.onDestroy();
    }

    private void checkServerStatus(){
        serverStatus.setText("pinging...");
        serverStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            boolean reachable = SharedData.getInstance().getServerCommunication().isReachable(); //network operation
            getActivity().runOnUiThread(()->{
                if(reachable){
                    serverStatus.setText("reachable");
                    serverStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                }
                else{
                    serverStatus.setText("unreachable");
                    serverStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                }
            });
        });
    }
}
