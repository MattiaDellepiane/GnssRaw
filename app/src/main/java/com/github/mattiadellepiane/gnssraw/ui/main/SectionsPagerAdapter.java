package com.github.mattiadellepiane.gnssraw.ui.main;

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

    private MeasurementFragment mf;
    private PlotFragment pf;
    private SettingsFragment sf;
    private FilesFragment ff;
    private MapsFragment maps;

    public SectionsPagerAdapter(FragmentActivity fa, RealTimePositionVelocityCalculator mRealTimePositionVelocityCalculator) {
        super(fa);
        pf = new PlotFragment();
        mf = new MeasurementFragment(pf);
        mRealTimePositionVelocityCalculator.setPlotFragment(pf);
        sf = new SettingsFragment();
        ff = new FilesFragment();
        maps = new MapsFragment();
        SharedData.getInstance().setFilesFragment(ff);
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
                return maps;
            case 3:
                return ff;
            case 4:
                return sf;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return TAB_TITLES.length;
    }

}