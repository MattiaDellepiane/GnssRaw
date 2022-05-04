package com.github.mattiadellepiane.gnssraw.listeners

import com.github.mattiadellepiane.gnssraw.R
import com.github.mattiadellepiane.gnssraw.data.SharedData
import java.io.File
import android.os.Environment
import java.io.IOException
import java.io.PrintWriter
import android.icu.text.SimpleDateFormat
import java.util.*

class FileLogger : MeasurementListener() {
    private var out: PrintWriter? = null
    private var file: File? = null
    private val FOLDER: String
    private var currentFileName: String? = null
    override fun initResources() {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
        val now = formatter.format(Date())
        val fileName = String.format(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "%s_%s.txt", "$FOLDER/GNSS_Log", now)
        currentFileName = "GNSS_Log_$now.txt"
        file = createFile(fileName)
        try {
            out = PrintWriter(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun releaseResources() {
        if (out != null) out!!.close()
        if (SharedData.instance.filesFragment != null) SharedData.instance.filesFragment!!.addFileView(currentFileName)
    }

    override fun write(s: String?) {
        if (out != null && SharedData.instance.isListeningForMeasurements) out!!.println(s)
    }

    private fun createFile(s: String): File {
        val f = File(s)
        if (!f.parentFile.exists()) {
            f.parentFile.mkdirs()
        }
        try {
            f.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return f
    }

    init {
        FOLDER = SharedData.instance.context!!.getString(R.string.app_documents_folder)
    }
}