package com.github.mattiadellepiane.gnssraw.ui.main;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.github.mattiadellepiane.gnssraw.R;
import com.github.mattiadellepiane.gnssraw.data.SharedData;
import com.github.mattiadellepiane.gnssraw.ui.main.tabs.FilesFragment;
import com.github.mattiadellepiane.gnssraw.ui.main.tabs.MapsFragment;
import com.github.mattiadellepiane.gnssraw.utils.gnss.RealTimePositionVelocityCalculator;
import com.github.mattiadellepiane.gnssraw.ui.main.tabs.MeasurementFragment;
import com.github.mattiadellepiane.gnssraw.ui.main.tabs.PlotFragment;
import com.github.mattiadellepiane.gnssraw.ui.main.tabs.SettingsFragment;

/**
 * A [FragmentStateAdapter] that returns the fragment corresponding to
 * the current active tab.
 */
public class SectionsPagerAdapter extends FragmentStateAdapter {

    @StringRes
    public static final int[] TAB_TITLES = new int[] {R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3, R.string.tab_text_4, R.string.tab_text_5};
    private final RealTimePositionVelocityCalculator mRealTimePositionVelocityCalculator;

    private MeasurementFragment mf;
    private PlotFragment pf;
    private SettingsFragment sf;
    private FilesFragment ff;
    private MapsFragment maps;

    public SectionsPagerAdapter(FragmentActivity fa, RealTimePositionVelocityCalculator mRealTimePositionVelocityCalculator) {
        super(fa);
        this.mRealTimePositionVelocityCalculator = mRealTimePositionVelocityCalculator;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new MeasurementFragment();
            case 1:
                PlotFragment p = new PlotFragment();
                mRealTimePositionVelocityCalculator.setPlotFragment(p);
                return p;
            case 2:
                return new MapsFragment();
            case 3:
                return new FilesFragment();
            case 4:
                return new SettingsFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return TAB_TITLES.length;
    }

}