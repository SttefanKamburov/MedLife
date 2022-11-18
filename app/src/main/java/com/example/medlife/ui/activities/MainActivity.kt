package com.example.medlife.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.medlife.R
import com.example.medlife.Utils
import com.example.medlife.models.Medication
import com.example.medlife.ui.adapters.MedicationsRecyclerAdapter
import com.example.medlife.ui.fragments.CalendarFragment
import com.example.medlife.ui.fragments.MedicationFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback

class MainActivity : AppCompatActivity(), View.OnClickListener{

    companion object{
        private const val PAGES_COUNT       = 2
        private const val PAGE_CALENDAR     = 0
        private const val PAGE_MEDICATIONS  = 1
    }

    private var medicationFragment              : MedicationFragment? = null
    private var calendarFragment                : CalendarFragment? = null

    private lateinit var viewPager              : ViewPager
    private lateinit var calendarBtn            : TextView
    private lateinit var medicationsBtn         : TextView

    private lateinit var dimView                : View
    private lateinit var bottomSheet            : View
    private lateinit var bottomSheetBehavior    : BottomSheetBehavior<*>
    private lateinit var medicationsAdapter     : MedicationsRecyclerAdapter

    private val medicationsList                 : ArrayList<Medication> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        viewPager       = findViewById(R.id.view_pager)
        calendarBtn     = findViewById(R.id.calendar_button)
        medicationsBtn  = findViewById(R.id.medications_button)
        bottomSheet     = findViewById(R.id.bottom_sheet)
        dimView         = findViewById(R.id.bottom_sheet_dim_view)

        viewPager.adapter = PageAdapter(supportFragmentManager)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                setNavigationButtons(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        setNavigationButtons(PAGE_CALENDAR)

        bottomSheetBehavior = BottomSheetBehavior.from<View>(bottomSheet)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                dimView.visibility = if (slideOffset > 0) View.VISIBLE else View.GONE
                dimView.alpha = slideOffset
            }
        })

        medicationsAdapter = MedicationsRecyclerAdapter(this, medicationsList, false)
        medicationsAdapter.setOnItemClickListener(object : MedicationsRecyclerAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {

            }
        })
        val recyclerView: RecyclerView  = findViewById(R.id.all_medications_recyclerview)
        recyclerView.layoutManager      = LinearLayoutManager(this)
        recyclerView.adapter            = medicationsAdapter

        findViewById<TextView>(R.id.toolbar_title_text_view).text = getString(R.string.app_name)
        findViewById<ImageView>(R.id.back_arrow_image_view).visibility = View.GONE

        dimView.setOnClickListener(this)
        calendarBtn.setOnClickListener(this)
        medicationsBtn.setOnClickListener(this)
    }

    private fun setNavigationButtons(page : Int){
        calendarBtn.setTextColor(ContextCompat.getColor(this, if (page == PAGE_CALENDAR) R.color.main_purple else R.color.grey))
        medicationsBtn.setTextColor(ContextCompat.getColor(this, if (page == PAGE_MEDICATIONS) R.color.main_purple else R.color.grey))

        calendarBtn.setCompoundDrawablesWithIntrinsicBounds(
            null,
            Utils.tintDrawable(ContextCompat.getDrawable(this,  R.drawable.calendar_icon)!!, ContextCompat.getColor(this, if (page == PAGE_CALENDAR) R.color.main_purple else R.color.grey)),
            null,
            null)
        medicationsBtn.setCompoundDrawablesWithIntrinsicBounds(
            null,
            Utils.tintDrawable(ContextCompat.getDrawable(this,  R.drawable.medications_icon)!!, ContextCompat.getColor(this, if (page == PAGE_MEDICATIONS) R.color.main_purple else R.color.grey)),
            null,
            null)
    }

    override fun onClick(v: View?) {
        if(v?.id == calendarBtn.id)
            viewPager.currentItem = PAGE_CALENDAR
        else if(v?.id == medicationsBtn.id)
            viewPager.currentItem = PAGE_MEDICATIONS
        else if (v?.id == dimView.id)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun showBottomSheet(){
        if(medicationFragment != null){
            medicationsList.clear()
            medicationsList.addAll(medicationFragment!!.getList())
            medicationsAdapter.notifyDataSetChanged()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onBackPressed() {
        if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        else
            super.onBackPressed()
    }

    fun goToAddEditMedication(medication : Medication?){
        val intent = Intent(this, AddEditMedicationActivity::class.java)
        if(medication != null)
            intent.putExtra(Utils.INTENT_TRANSFER_MEDICATION_ID, medication.id)
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
        override fun getCount(): Int = PAGES_COUNT

        override fun getItem(position: Int): Fragment {
            return if(position == PAGE_MEDICATIONS){
                if(medicationFragment == null)
                    medicationFragment = MedicationFragment()
                medicationFragment!!

            } else{
                if(calendarFragment == null)
                    calendarFragment = CalendarFragment()
                calendarFragment!!
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when(position) {
                PAGE_CALENDAR -> { return getString(R.string.calendar) }
                PAGE_MEDICATIONS -> { return getString(R.string.medications) }
            }
            return super.getPageTitle(position)
        }
    }
}