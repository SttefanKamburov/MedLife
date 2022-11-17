package com.example.medlife.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.medlife.R
import com.example.medlife.ui.adapters.PageAdapter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity);

        val viewPager = findViewById<ViewPager>(R.id.main_activity_view_pager)
        viewPager.adapter = PageAdapter(supportFragmentManager)
        //init()
        //getMedications()
    }
}