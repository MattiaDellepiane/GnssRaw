package com.github.mattiadellepiane.gnssraw.ui.main.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mattiadellepiane.gnssraw.R;
import com.github.mattiadellepiane.gnssraw.data.SharedData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MeasurementFragment extends Fragment {

    private SharedData data;
    private TextView serverStatus, sendingData;
    private PlotFragment plotFragment;

    public MeasurementFragment(SharedData data, PlotFragment plotFragment) {
        this.data = data;
        this.plotFragment = plotFragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_measurements, container, false);
        serverStatus = fragment.findViewById(R.id.serverStatus);
        sendingData = fragment.findViewById(R.id.sendingData);

        Button startStop = fragment.findViewById(R.id.startStop);
        startStop.setOnClickListener(view -> {
            if(data.isListeningForMeasurements()){
                sendingData.setText("");
                startStop.setText("START");
                startStop.setBackgroundColor(getResources().getColor(R.color.green,data.getContext().getTheme()));
                data.stopMeasurements();
            }
            else{
                sendingData.setText(R.string.sending_data);
                startStop.setText("STOP");
                startStop.setBackgroundColor(getResources().getColor(R.color.red,data.getContext().getTheme()));
                plotFragment.restartChart();
                data.startMeasurements();
            }

        });

        Button pingServerButton = fragment.findViewById(R.id.pingServerButton);
        pingServerButton.setOnClickListener(view -> {
            checkServerStatus();
        });

        checkServerStatus();
        return fragment;
    }

    private void checkServerStatus(){
        serverStatus.setText("pinging...");
        serverStatus.setTextColor(getResources().getColor(R.color.black, data.getContext().getTheme()));
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            boolean reachable = data.getServerCommunication().isReachable(); //network operation
            getActivity().runOnUiThread(()->{
                if(reachable){
                    serverStatus.setText("reachable");
                    serverStatus.setTextColor(getResources().getColor(R.color.green, data.getContext().getTheme()));
                }
                else{
                    serverStatus.setText("unreachable");
                    serverStatus.setTextColor(getResources().getColor(R.color.red, data.getContext().getTheme()));
                }
            });
        });
    }

}
