package com.github.mattiadellepiane.gnssraw.ui.main;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.github.mattiadellepiane.gnssraw.R;
import com.github.mattiadellepiane.gnssraw.data.SharedData;
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
    public static final int[] TAB_TITLES = new int[] {R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};

    private final SharedData data;
    private MeasurementFragment mf;
    private PlotFragment pf;
    private SettingsFragment sf;

    public SectionsPagerAdapter(FragmentActivity fa, SharedData data, RealTimePositionVelocityCalculator mRealTimePositionVelocityCalculator) {
        super(fa);
        this.data = data;
        mf = new MeasurementFragment(data);
        pf = new PlotFragment();
        mRealTimePositionVelocityCalculator.setPlotFragment(pf);
        sf = new SettingsFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return mf;
            case 1:
                return pf;
            case 2:
                return sf;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

}