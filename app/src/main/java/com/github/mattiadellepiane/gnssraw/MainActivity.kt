package com.github.mattiadellepiane.gnssraw

import android.Manifest
import android.os.Bundle
import android.view.View
import com.github.mattiadellepiane.gnssraw.data.SharedData
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.github.mattiadellepiane.gnssraw.utils.gnss.RealTimePositionVelocityCalculator
import com.github.mattiadellepiane.gnssraw.ui.main.SectionsPagerAdapter
import com.github.mattiadellepiane.gnssraw.listeners.ServerCommunication
import androidx.appcompat.app.AppCompatActivity
import com.github.mattiadellepiane.gnssraw.listeners.FileLogger
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.github.mattiadellepiane.gnssraw.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var serverCommunication: ServerCommunication? = null
    private var fileLogger: FileLogger? = null
    private var mRealTimePositionVelocityCalculator: RealTimePositionVelocityCalculator? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        SharedData.instance.context = this.applicationContext
        serverCommunication = ServerCommunication()
        fileLogger = FileLogger()
        mRealTimePositionVelocityCalculator = RealTimePositionVelocityCalculator()
        mRealTimePositionVelocityCalculator!!.setMainActivity(this)
        mRealTimePositionVelocityCalculator!!.setResidualPlotMode(
                RealTimePositionVelocityCalculator.Companion.RESIDUAL_MODE_DISABLED,
                null /* fixedGroundTruth */)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, mRealTimePositionVelocityCalculator!!)
        val viewPager = binding!!.viewPager
        viewPager.isUserInputEnabled = false
        viewPager.adapter = sectionsPagerAdapter
        viewPager.offscreenPageLimit = 5 //Preload fragments (it also solves crashes if start button is clicked before visiting the plot fragment)
        val tabs = binding!!.tabs
        TabLayoutMediator(tabs, viewPager) { tab: TabLayout.Tab, position: Int -> tab.setText(SectionsPagerAdapter.Companion.TAB_TITLES.get(position)) }.attach()
        checkAndHandlePermissions()
    }

    private fun checkAndHandlePermissions() {
        if (ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED) {
            initMeasurementProvider()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initMeasurementProvider()
            } else {
                val sb: Snackbar = Snackbar.make(binding!!.root, R.string.require_permissions, Snackbar.LENGTH_LONG)
                sb.setAction("dismiss") { view: View? -> sb.dismiss() }
                sb.show()
            }
        }
    }

    private fun initMeasurementProvider() {
        SharedData.instance.measurementProvider = serverCommunication?.let {
            fileLogger?.let { it1 ->
                mRealTimePositionVelocityCalculator?.let { it2 ->
                    MeasurementProvider(
                            this,
                            SensorMeasurements(),
                            it,
                            it1,
                            it2)
                }
            }
        }
    }

    override fun onDestroy() {
        if (SharedData.instance.isListeningForMeasurements) SharedData.instance.stopMeasurements()
        super.onDestroy()
    }

    companion object {
        private const val REQUEST_CODE = 1
        private val REQUIRED_PERMISSIONS = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}