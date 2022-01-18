package com.github.mattiadellepiane.gnssraw;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.github.mattiadellepiane.gnssraw.data.SharedData;
import com.github.mattiadellepiane.gnssraw.listeners.ServerCommunication;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.mattiadellepiane.gnssraw.ui.main.SectionsPagerAdapter;
import com.github.mattiadellepiane.gnssraw.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final int REQUEST_CODE = 1;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private ServerCommunication serverCommunication;

    private SharedData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        data = new SharedData(this);
        serverCommunication = new ServerCommunication(data);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, data);
        ViewPager2 viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        new TabLayoutMediator(tabs, viewPager, (tab, position) -> {
            tab.setText(sectionsPagerAdapter.TAB_TITLES[position]);
        }).attach();

        checkAndHandlePermissions();
    }


    private void checkAndHandlePermissions(){
        if (ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED) {
            initMeasurementProvider();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initMeasurementProvider();
            }
            else{
                Snackbar sb = Snackbar.make(binding.getRoot(), R.string.require_permissions, Snackbar.LENGTH_LONG);
                sb.setAction("dismiss", view -> sb.dismiss());
                sb.show();
            }
        }
    }

    private void initMeasurementProvider(){
        data.measurementProvider =
                new MeasurementProvider(this, new SensorMeasurements(), serverCommunication);
    }

    public void startLoggingData(View v){
        data.startMeasurements();
    }

    public void stopLoggingData(View v){
        data.stopMeasurements();
    }
}