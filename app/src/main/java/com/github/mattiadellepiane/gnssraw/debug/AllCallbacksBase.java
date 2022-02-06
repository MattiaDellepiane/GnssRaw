package com.github.mattiadellepiane.gnssraw.debug;

import android.util.Log;

import androidx.fragment.app.Fragment;


public abstract class AllCallbacksBase extends Fragment {

    protected String getClassName(){
        return "Classe: " + this.getClass().getSimpleName();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.v(getClassName(), "onStart");
    }


    @Override
    public void onPause()
    {
        super.onPause();
        Log.v(getClassName(), "onPause");
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Log.v(getClassName(), "onStop");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.v(getClassName(), "onResume");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.v(getClassName(), "onDestroy");
    }



}
