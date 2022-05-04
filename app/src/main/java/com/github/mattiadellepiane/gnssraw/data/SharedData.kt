package com.github.mattiadellepiane.gnssraw.data

import com.github.mattiadellepiane.gnssraw.R
import com.github.mattiadellepiane.gnssraw.ui.main.tabs.PlotFragment

import com.github.mattiadellepiane.gnssraw.ui.main.tabs.FilesFragment
import com.github.mattiadellepiane.gnssraw.ui.main.tabs.MapsFragment

import com.github.mattiadellepiane.gnssraw.MeasurementProvider
import com.github.mattiadellepiane.gnssraw.listeners.ServerCommunication
import java.lang.NumberFormatException
import java.lang.ClassCastException

import android.content.*
import android.util.Log
import androidx.preference.PreferenceManager

class SharedData{  //Singleton

    //Inizio
    var measurementProvider: MeasurementProvider? = null
    var isListeningForMeasurements = false
    var serverCommunication: ServerCommunication? = null
    var context: Context? = null
    var filesFragment: FilesFragment? = null
    var mapsFragment: MapsFragment? = null
    var plotFragment: PlotFragment? = null
    fun startMeasurements() {
        isListeningForMeasurements = true
        measurementProvider!!.registerAll()
    }

    fun stopMeasurements() {
        isListeningForMeasurements = false
        measurementProvider!!.unRegisterAll()
    }

    //Preferences Getters
    val isSensorsEnabled: Boolean
        get() = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sensors_enabled", false)
    val isServerEnabled: Boolean
        get() = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("server_enabled", false)
    val serverAddress: String?
        get() = PreferenceManager.getDefaultSharedPreferences(context).getString("server_address", context!!.getString(R.string.server_ip_default))
    val fullTracking: Boolean
        get() = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("full_tracking", false)
    val serverPort: Int
        get() {
            val port: Int
            port = try {
                PreferenceManager.getDefaultSharedPreferences(context)
                        .getString("server_port", context!!.getString(R.string.server_port_default))!!.toInt()
            } catch (e: NumberFormatException) {
                context!!.getString(R.string.server_port_default).toInt()
            } catch (e: ClassCastException) {
                context!!.getString(R.string.server_port_default).toInt()
            }
            return port
        }

    companion object {
        val instance = SharedData();
    }
}