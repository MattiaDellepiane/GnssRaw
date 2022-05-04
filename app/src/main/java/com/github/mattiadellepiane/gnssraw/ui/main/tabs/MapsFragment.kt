package com.github.mattiadellepiane.gnssraw.ui.main.tabs

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mattiadellepiane.gnssraw.R
import com.github.mattiadellepiane.gnssraw.data.SharedData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {
    private var googleMap: GoogleMap? = null
    private var lastMarker: Marker? = null
    private val callback: OnMapReadyCallback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        this@MapsFragment.googleMap = googleMap
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragmentView: View = inflater.inflate(R.layout.fragment_maps, container, false)
        SharedData.instance.mapsFragment = this
        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment: SupportMapFragment? = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback)
        }
    }

    fun update(location: Location) {
        if (context == null) return
        val tmp = LatLng(location.latitude, location.longitude)
        if (lastMarker != null) lastMarker!!.remove()
        lastMarker = googleMap!!.addMarker(MarkerOptions().position(tmp))
        googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(tmp))
    }

    fun update(lat: Double, lng: Double, Q: Int) {
        requireActivity().runOnUiThread {
            Log.v("PROVAAA", Q.toString())
            if (context == null) return@runOnUiThread
            val tmp = LatLng(lat, lng)
            if (lastMarker != null) lastMarker!!.remove()
            var markerOptions: MarkerOptions? = null
            markerOptions = when (Q) {
                1 -> MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                2 -> MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                4 -> MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                else -> MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            }
            lastMarker = googleMap!!.addMarker(markerOptions.position(tmp))
            googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(tmp))
        }
    }

    private fun convertDegreeAngleToDouble(degrees: Double, minutes: Double, seconds: Double): Double {
        return degrees + minutes / 60 + seconds / 3600
    }
}