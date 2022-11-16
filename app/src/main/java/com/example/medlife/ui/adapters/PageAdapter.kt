package com.example.medlife.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.medlife.MedicationFragment

class PageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 1;
    }

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> {
                return MedicationFragment()
            }
            1 -> {
            }
        }

        return MedicationFragment();
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position) {
            0 -> {
                return "Medications"
            }
            1 -> {
                return "Calendar"
            }
        }
        return super.getPageTitle(position)
    }
}