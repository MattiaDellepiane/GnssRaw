package com.github.mattiadellepiane.gnssraw.ui.main.tabs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mattiadellepiane.gnssraw.R;
import com.github.mattiadellepiane.gnssraw.data.SharedData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment {

    private GoogleMap googleMap;
    private Marker lastMarker;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsFragment.this.googleMap = googleMap;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_maps, container, false);
        SharedData.getInstance().setMapsFragment(this);
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    public void update(Location location){
        if(getContext() == null)
            return;
        LatLng tmp = new LatLng(location.getLatitude(), location.getLongitude());
        if(lastMarker != null)
            lastMarker.remove();
        lastMarker = googleMap.addMarker(new MarkerOptions().position(tmp));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(tmp));
    }

    public void update(double lat, double lng, int Q){
        getActivity().runOnUiThread(() -> {
            Log.v("PROVAAA", String.valueOf(Q));
            if (getContext() == null)
                return;
            LatLng tmp = new LatLng(lat, lng);
            if (lastMarker != null)
                lastMarker.remove();
            MarkerOptions markerOptions = null;
            switch(Q){
                case 1:
                    markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    break;
                case 2:
                    markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    break;
                case 4:
                    markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    break;
                default:
                    markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    break;
            }
            lastMarker = googleMap.addMarker(markerOptions.position(tmp));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(tmp));
        });
    }

    private double convertDegreeAngleToDouble(double degrees, double minutes, double seconds){
        return degrees + (minutes/60) + (seconds/3600);
    }
}