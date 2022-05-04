package com.github.mattiadellepiane.gnssraw.debug


import android.util.Log
import androidx.fragment.app.Fragment

abstract class AllCallbacksBase : Fragment() {
    protected val className: String
        protected get() = "Classe: " + this.javaClass.simpleName

    override fun onStart() {
        super.onStart()
        Log.v(className, "onStart")
    }

    override fun onPause() {
        super.onPause()
        Log.v(className, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.v(className, "onStop")
    }

    override fun onResume() {
        super.onResume()
        Log.v(className, "onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(className, "onDestroy")
    }
}