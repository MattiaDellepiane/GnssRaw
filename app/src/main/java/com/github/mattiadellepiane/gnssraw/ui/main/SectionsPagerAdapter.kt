package com.github.mattiadellepiane.gnssraw.ui.main

import com.github.mattiadellepiane.gnssraw.R
import com.github.mattiadellepiane.gnssraw.ui.main.tabs.PlotFragment
import androidx.fragment.app.FragmentActivity
import com.github.mattiadellepiane.gnssraw.utils.gnss.RealTimePositionVelocityCalculator
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.github.mattiadellepiane.gnssraw.ui.main.tabs.MeasurementFragment
import com.github.mattiadellepiane.gnssraw.ui.main.tabs.SettingsFragment
import com.github.mattiadellepiane.gnssraw.ui.main.tabs.FilesFragment
import com.github.mattiadellepiane.gnssraw.ui.main.tabs.MapsFragment
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

/**
 * A [FragmentStateAdapter] that returns the fragment corresponding to
 * the current active tab.
 */
class SectionsPagerAdapter(fa: FragmentActivity?, private val mRealTimePositionVelocityCalculator: RealTimePositionVelocityCalculator) : FragmentStateAdapter(fa!!) {
    private val mf: MeasurementFragment? = null
    private val pf: PlotFragment? = null
    private val sf: SettingsFragment? = null
    private val ff: FilesFragment? = null
    private val maps: MapsFragment? = null
    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return MeasurementFragment()
            1 -> {
                val p = PlotFragment()
                mRealTimePositionVelocityCalculator.setPlotFragment(p)
                return p
            }
            2 -> return MapsFragment()
            3 -> return FilesFragment()
            4 -> return SettingsFragment()
        }
        return SettingsFragment()
    }

    override fun getItemCount(): Int {
        return TAB_TITLES.size
    }

    companion object {
        @StringRes
        val TAB_TITLES = intArrayOf(R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3, R.string.tab_text_4, R.string.tab_text_5)
    }
}