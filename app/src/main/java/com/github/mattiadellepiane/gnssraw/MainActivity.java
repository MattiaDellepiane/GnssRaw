package com.github.mattiadellepiane.gnssraw;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.github.mattiadellepiane.gnssraw.data.SharedData;
import com.github.mattiadellepiane.gnssraw.listeners.FileLogger;
import com.github.mattiadellepiane.gnssraw.utils.gnss.RealTimePositionVelocityCalculator;
import com.github.mattiadellepiane.gnssraw.listeners.ServerCommunication;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

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
    private FileLogger fileLogger;
    private RealTimePositionVelocityCalculator mRealTimePositionVelocityCalculator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher_foreground);// set drawable icon
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedData.getInstance().setContext(this.getApplicationContext());
        serverCommunication = new ServerCommunication();
        fileLogger = new FileLogger();
        //Set RealTimePositionVelocityCalculator
        mRealTimePositionVelocityCalculator = new RealTimePositionVelocityCalculator();
        mRealTimePositionVelocityCalculator.setMainActivity(this);
        mRealTimePositionVelocityCalculator.setResidualPlotMode(
                RealTimePositionVelocityCalculator.RESIDUAL_MODE_DISABLED, null /* fixedGroundTruth */);
        //
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, mRealTimePositionVelocityCalculator);
        ViewPager2 viewPager = binding.viewPager;
        viewPager.setUserInputEnabled(false);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(5); //Preload fragments (it also solves crashes if start button is clicked before visiting the plot fragment)
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
        SharedData.getInstance().measurementProvider =
                new MeasurementProvider(this, new SensorMeasurements(), serverCommunication, fileLogger, mRealTimePositionVelocityCalculator);
    }

}