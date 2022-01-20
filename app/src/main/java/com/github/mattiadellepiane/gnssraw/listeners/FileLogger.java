package com.github.mattiadellepiane.gnssraw.listeners;

import android.icu.text.SimpleDateFormat;
import android.os.Environment;
import android.util.Log;

import com.github.mattiadellepiane.gnssraw.data.SharedData;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class FileLogger extends MeasurementListener{

    private PrintWriter out;
    private final String FILE_NAME = "/GnssRaw/GNSS_Log";

    public FileLogger(SharedData data){
        super(data);
    }

    @Override
    protected void initResources() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date now = new Date();
        String fileName = String.format(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "%s_%s.txt", FILE_NAME, formatter.format(now));
        File file = createFile(fileName);
        try {
            out = new PrintWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void releaseResources() {
        if(out != null)
            out.close();
    }

    @Override
    protected void write(String s) {
        if(out != null)
            out.println(s);
    }

    private File createFile(String s){
        File f = new File(s);
        if(!f.getParentFile().exists()){
            f.getParentFile().mkdirs();
        }
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }
}
