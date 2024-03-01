package com.recipeme.activities

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.recipeme.R
import com.recipeme.adapters.FragmentAdapter
import com.recipeme.fragments.FridgeFragment
import com.recipeme.fragments.GroceryListFragment
import com.recipeme.fragments.RecipesFragment
import com.recipeme.utils.IngredientsData
import java.io.BufferedReader
import java.io.InputStreamReader


class MainActivity : AppCompatActivity(), View.OnClickListener, TabLayout.OnTabSelectedListener{

    private var _tabTitle = arrayOf("Fridge", "GroceryList", "Recipes")
    private lateinit var _textLabel: TextView
    private lateinit var _adapter: FragmentAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var viewPager = findViewById<ViewPager2>(R.id.viewPager)
        var tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        findViewById<ImageButton>(R.id.ibMoreMain).setOnClickListener(this)
        _textLabel = findViewById<TextView>(R.id.tvLabelMain)
        _adapter = FragmentAdapter(this)
        viewPager.adapter = _adapter
        tabLayout.addOnTabSelectedListener(this)
        TabLayoutMediator(tabLayout, viewPager){
            tab, position ->
            tab.text = _tabTitle[position]
        }.attach()

        val inputStream = InputStreamReader(
            assets
                .open("top-1k-ingredients.csv")
        )

        val data = IngredientsData
        val reader = BufferedReader(inputStream)
        reader.readLine()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            var lineSplit = line!!.split(";")
            data.map[lineSplit[1].toInt()] = lineSplit[0]
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        Log.d("Tab" , tab?.text.toString())
        _textLabel.text = tab?.text.toString()
        val btnOne = findViewById<ImageButton>(R.id.ibOneMain)
        val btnTwo = findViewById<ImageButton>(R.id.ibTwoMain)
        when(tab?.position){
            0 ->{
                Log.d("Fragment Position", "0");
                if(btnOne.isInvisible) {
                    btnOne.visibility = VISIBLE
                }
                btnOne.setImageResource(R.drawable.ic_refresh)
                if(!btnTwo.isInvisible) {
                    btnTwo.visibility = INVISIBLE
                }
                //refresh
                findViewById<ImageButton>(R.id.ibOneMain).setOnClickListener() {
                    val f = supportFragmentManager.findFragmentByTag("f0") as FridgeFragment
                    if (f != null) {
                        // Check if the fragment is properly initialized and attached to the activity
                        f.refreshClickFragment("Refresh")
                    } else {
                        // Handle the case where the fragment or _fridgeDao is not properly initialized
                        Log.e(
                            "MainActivity",
                            "Fragment is not properly initialized"
                        )
                    }
                }
            }
            1 ->{
                Log.d("Fragment Position", "1");
                if(btnOne.isInvisible) {
                    btnOne.visibility = VISIBLE
                }
                btnOne.setImageResource(R.drawable.ic_increment)
                if(!btnTwo.isInvisible) {
                    btnTwo.visibility = INVISIBLE
                }
                //add
                btnOne.setOnClickListener() {
                    val f =
                        supportFragmentManager.findFragmentByTag("f1") as GroceryListFragment

                    if (f != null) {
                        // Check if the fragment is properly initialized and attached to the activity
                        f.addClickFragment("Add")
                    } else {
                        // Handle the case where the fragment or _fridgeDao is not properly initialized
                        Log.e(
                            "MainActivity",
                            "Fragment is not properly initialized"
                        )
                    }
                }
            }
            2 ->{
                Log.d("Fragment Position", "2");
                if(btnOne.isInvisible) {
                    btnOne.visibility = VISIBLE
                }
                btnOne.setImageResource(R.drawable.ic_refresh)
                if(!btnTwo.isInvisible) {
                    btnTwo.visibility = INVISIBLE
                }
                //refresh
                btnOne.setOnClickListener() {
                    val f = supportFragmentManager.findFragmentByTag("f2") as RecipesFragment
                    if (f != null) {
                        // Check if the fragment is properly initialized and attached to the activity
                        f.refreshClickFragment("Refresh")
                    } else {
                        // Handle the case where the fragment or _fridgeDao is not properly initialized
                        Log.e(
                            "MainActivity",
                            "Fragment is not properly initialized"
                        )
                    }
                }
            }
        }
    }



    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onClick(v: View?) {

    }



}