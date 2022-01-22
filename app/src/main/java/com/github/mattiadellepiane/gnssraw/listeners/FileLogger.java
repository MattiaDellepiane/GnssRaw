package com.github.mattiadellepiane.gnssraw.listeners;

import android.icu.text.SimpleDateFormat;
import android.os.Environment;

import com.github.mattiadellepiane.gnssraw.R;
import com.github.mattiadellepiane.gnssraw.data.SharedData;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class FileLogger extends MeasurementListener{

    private PrintWriter out;
    private File file;
    private final String FOLDER;
    private String currentFileName;

    public FileLogger(){
        FOLDER = SharedData.getInstance().getContext().getString(R.string.app_documents_folder);
    }

    @Override
    protected void initResources() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String now = formatter.format(new Date());
        String fileName = String.format(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "%s_%s.txt", FOLDER + "/GNSS_Log", now);
        currentFileName = "GNSS_Log_" + now + ".txt";
        file = createFile(fileName);
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
        if(SharedData.getInstance().getFilesFragment() != null)
            SharedData.getInstance().getFilesFragment().addFileView(currentFileName);
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
