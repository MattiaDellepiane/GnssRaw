package com.github.mattiadellepiane.gnssraw.ui.main.tabs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
    private TextView serverStatus, isSendingData;

    public MeasurementFragment(SharedData data) {
        this.data = data;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_measurements, container, false);
        serverStatus = fragment.findViewById(R.id.serverStatus);
        isSendingData = fragment.findViewById(R.id.isSendingData);

        Button start = fragment.findViewById(R.id.startButton);
        start.setOnClickListener(view -> {
            isSendingData.setText("true");
            isSendingData.setTextColor(getResources().getColor(R.color.green, data.getContext().getTheme()));
            data.startMeasurements();
        });
        Button stop = fragment.findViewById(R.id.stopButton);
        stop.setOnClickListener(view -> {
            isSendingData.setText("false");
            isSendingData.setTextColor(getResources().getColor(R.color.red, data.getContext().getTheme()));
            data.stopMeasurements();
        });

        Button pingServerButton = fragment.findViewById(R.id.pingServerButton);
        pingServerButton.setOnClickListener(view -> {
            checkServerStatus();
        });

        checkServerStatus();
        //fragment.post(()-> checkServerStatus());
        return fragment;
    }

    private void checkServerStatus(){
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
