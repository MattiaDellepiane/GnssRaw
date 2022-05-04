package com.github.mattiadellepiane.gnssraw.ui.main.tabs

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.github.mattiadellepiane.gnssraw.R
import com.github.mattiadellepiane.gnssraw.data.SharedData
import android.widget.TextView
import android.content.Intent
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import android.widget.Button
import com.github.mattiadellepiane.gnssraw.services.BackgroundMeasurementService
import android.os.Build
import java.util.concurrent.Executors
import androidx.fragment.app.Fragment

class MeasurementFragment : Fragment() {
    private var serverStatus: TextView? = null
    private var sendingData: TextView? = null
    private var startStop: MaterialButton? = null
    private var upMap: Button? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragment = inflater.inflate(R.layout.fragment_measurements, container, false)
        serverStatus = fragment.findViewById(R.id.serverStatus)
        sendingData = fragment.findViewById(R.id.sendingData)
        upMap = fragment.findViewById(R.id.updateMap)
        upMap!!.setOnClickListener { SharedData.instance.serverCommunication!!.location }
        startStop = fragment.findViewById(R.id.startStop)
        startStop!!.setOnClickListener {
            if (SharedData.instance.isListeningForMeasurements) {
                sendingData!!.visibility = View.INVISIBLE
                startStop!!.text = "START"
                startStop!!.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_start))
                startStop!!.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                upMap!!.isEnabled = false
                stopMeasurementService()
            } else {
                sendingData!!.visibility = View.VISIBLE
                startStop!!.text = "STOP"
                startStop!!.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_stop))
                startStop!!.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                upMap!!.isEnabled = true
                if (SharedData.instance.plotFragment != null) SharedData.instance.plotFragment!!.restartChart()
                startMeasurementService()
            }
        }
        if (SharedData.instance.isListeningForMeasurements) {
            sendingData!!.visibility = View.VISIBLE
            startStop!!.text = "STOP"
            startStop!!.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_stop))
            startStop!!.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
        }
        val pingServerButton = fragment.findViewById<Button>(R.id.pingServerButton)
        pingServerButton.setOnClickListener { view: View? -> checkServerStatus() }
        checkServerStatus()
        return fragment
    }

    private fun startMeasurementService() {
        val context = requireContext().applicationContext
        val intent = Intent(context, BackgroundMeasurementService::class.java) // Build the intent for the service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        }
    }

    private fun stopMeasurementService() {
        val context = requireContext().applicationContext
        val serviceIntent = Intent(context, BackgroundMeasurementService::class.java)
        context.stopService(serviceIntent)
    }

    override fun onDestroy() {
        val context = requireContext().applicationContext
        val serviceIntent = Intent(context, BackgroundMeasurementService::class.java)
        context.stopService(serviceIntent)
        super.onDestroy()
    }

    private fun checkServerStatus() {
        serverStatus!!.text = "pinging..."
        serverStatus!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            val reachable: Boolean = SharedData.instance.serverCommunication!!.isReachable //network operation
            requireActivity().runOnUiThread {
                if (reachable) {
                    serverStatus!!.text = "reachable"
                    serverStatus!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                } else {
                    serverStatus!!.text = "unreachable"
                    serverStatus!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                }
            }
        }
    }
}