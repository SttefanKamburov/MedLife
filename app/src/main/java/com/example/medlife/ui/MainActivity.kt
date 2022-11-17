package com.example.medlife.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.medlife.R
import com.example.medlife.Utils
import com.example.medlife.models.Medication
import com.example.medlife.ui.fragments.CalendarFragment
import com.example.medlife.ui.fragments.MedicationFragment

class MainActivity : AppCompatActivity() {

    var medicationFragment : MedicationFragment? = null
    var calendarFragment   : CalendarFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity);

        val viewPager = findViewById<ViewPager>(R.id.main_activity_view_pager)
        viewPager.adapter = PageAdapter(supportFragmentManager)
        viewPager.currentItem = 0
    }

    fun goToAddEditMedication(medication : Medication?){
        val intent = Intent(this, MedicationEditActivity::class.java)
        if(medication != null)
            intent.putExtra(Utils.INTENT_TRANSFER_MEDICATION, medication)
        addEditMedicationLauncher.launch(intent)
    }

    private val addEditMedicationLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if(medicationFragment != null)
                    medicationFragment!!.getMedications()
            }
        }

    inner class PageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int = 1

        override fun getItem(position: Int): Fragment {
            return if(position == 1){
                if(calendarFragment == null)
                    calendarFragment = CalendarFragment()
                calendarFragment!!
            } else{
                if(medicationFragment == null)
                    medicationFragment = MedicationFragment()
                medicationFragment!!
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when(position) {
                0 -> { return "Medications" }
                1 -> { return "Calendar" }
            }
            return super.getPageTitle(position)
        }
    }
}