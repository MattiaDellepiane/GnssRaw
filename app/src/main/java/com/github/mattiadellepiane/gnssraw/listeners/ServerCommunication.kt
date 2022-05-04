package com.github.mattiadellepiane.gnssraw.listeners

import com.github.mattiadellepiane.gnssraw.R
import com.github.mattiadellepiane.gnssraw.data.SharedData
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket
import java.net.InetSocketAddress
import java.lang.Thread
import android.util.Log

class ServerCommunication : MeasurementListener() {
    private var socket: Socket? = null
    private var out: PrintWriter? = null
    private var `in`: BufferedReader? = null
    private val executor: ExecutorService
    private var headerSent = false
    private val port = 5088
    private val debugTag: String
        private get() = SharedData.instance.context!!.getString(R.string.debug_tag)

    override fun initResources() {
        if (SharedData.instance.isServerEnabled) {
            executor.execute {
                try {
                    Log.v(debugTag, "Server ip: " + SharedData.instance.serverAddress)
                    socket = Socket(SharedData.instance.serverAddress, SharedData.instance.serverPort)
                    out = PrintWriter(socket!!.getOutputStream(), true)
                    `in` = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                    Log.v("PROVA", "risorse inizializzate")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun releaseResources() {
        executor.execute {
            if (out != null) out!!.close()
            try {
                if (`in` != null) `in`!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun write(s: String?) {
        Log.v("AAAA", s!!)
        if (SharedData.instance.isListeningForMeasurements && SharedData.instance.isServerEnabled) {
            Log.v(debugTag, "Invio messaggio al server")
            executor.execute {
                if (out != null) {
                    if (!headerSent) {
                        headerSent = true
                    } else {
                        location
                    }
                    out!!.println(s)
                }
            }
            Log.v(debugTag, "Messaggio inviato al server")
        }
    }

    val isReachable: Boolean
        get() {
            var s: Socket? = null
            try {
                s = Socket()
                s.connect(InetSocketAddress(SharedData.instance.serverAddress, SharedData.instance.serverPort), 3000)
            } catch (e: IOException) {
                return false
            } finally {
                try {
                    s!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return true
        }

    //out.println("R");
    val location: Unit
        get() {
            Thread {
                val s: Socket
                try {
                    s = Socket()
                    s.connect(InetSocketAddress(SharedData.instance.serverAddress, port), 3000)
                    val `in` = BufferedReader(InputStreamReader(s.getInputStream()))
                    val out = PrintWriter(s.getOutputStream(), true)
                    //out.println("R");
                    var input: String? = null
                    if (`in`.readLine().also { input = it } != null) {
                        val params = input!!.split("\\s+").toTypedArray()
                        val lat = params[2].toDouble()
                        val lng = params[3].toDouble()
                        val quota = params[4].toDouble()
                        val qfix = params[5].toInt()
                        val nsat = params[6].toInt()
                        val sdn = params[7].toDouble()
                        val sde = params[8].toDouble()
                        val sdu = params[9].toDouble()
                        if (SharedData.instance.mapsFragment != null) {
                            SharedData.instance.mapsFragment!!.update(lat, lng, qfix)
                        }
                    }
                    out.close()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            }.start()
        }

    init {
        executor = Executors.newSingleThreadExecutor()
        SharedData.instance.serverCommunication = this;
    }
}