package com.recipeme.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.recipeme.R
import com.recipeme.adapters.FragmentAdapter

class MainActivity : AppCompatActivity() {

    var tabTitle = arrayOf("Fridge", "GroceryList", "Recipes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var viewPager = findViewById<ViewPager2>(R.id.viewPager);
        var tabLayout = findViewById<TabLayout>(R.id.tabLayout);

        viewPager.adapter = FragmentAdapter(supportFragmentManager, lifecycle)

        TabLayoutMediator(tabLayout, viewPager){
            tab, position ->
            tab.text = tabTitle[position]
        }.attach()
    }
}