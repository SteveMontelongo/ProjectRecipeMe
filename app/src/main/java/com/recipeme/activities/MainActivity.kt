package com.recipeme.activities

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isInvisible
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.recipeme.R
import com.recipeme.adapters.FragmentAdapter
import com.recipeme.fragments.FridgeFragment
import com.recipeme.fragments.GroceryListFragment
import com.recipeme.fragments.RecipesFragment
import com.recipeme.interfaces.MainActivityInteraction
import com.recipeme.utils.IngredientsData
import java.io.BufferedReader
import java.io.InputStreamReader


class MainActivity : AppCompatActivity(), View.OnClickListener, TabLayout.OnTabSelectedListener, MainActivityInteraction{

    private lateinit var _textLabel: TextView
    private lateinit var _adapter: FragmentAdapter
    private lateinit var _pageIncrement: ImageButton
    private lateinit var _pageDecrement:ImageButton
    private lateinit var _pageNumber: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var viewPager = findViewById<ViewPager2>(R.id.viewPager)
        var tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        findViewById<ImageButton>(R.id.ibMoreMain).setOnClickListener(this)
        _pageIncrement= findViewById<ImageButton>(R.id.ibPageIncrementMain)
        _pageIncrement.setOnClickListener(this)
        _pageDecrement = findViewById<ImageButton>(R.id.ibPageDecrementMain)
        _pageDecrement.setOnClickListener(this)
        _pageNumber = findViewById<TextView>(R.id.tvPageMain)
        _textLabel = findViewById<TextView>(R.id.tvLabelMain)
        _adapter = FragmentAdapter(this)
        viewPager.adapter = _adapter
        tabLayout.addOnTabSelectedListener(this)
        var tabIcon = listOf<Int>(R.drawable.ic_fridge, R.drawable.ic_grocery, R.drawable.ic_recipes)
        var tabTitle = listOf<String>("Fridge", "Grocery", "Recipes")

        TabLayoutMediator(tabLayout, viewPager){
            tab, position ->
            tab.setIcon(tabIcon[position])
            tab.text = tabTitle[position]
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
                if(!_pageIncrement.isInvisible) {
                    _pageIncrement.visibility = INVISIBLE
                }
                if(!_pageDecrement.isInvisible) {
                    _pageDecrement.visibility = INVISIBLE
                }
                if(!_pageNumber.isInvisible) {
                    _pageNumber.visibility = INVISIBLE
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
                if(!_pageIncrement.isInvisible) {
                    _pageIncrement.visibility = INVISIBLE
                }
                if(!_pageDecrement.isInvisible) {
                    _pageDecrement.visibility = INVISIBLE
                }
                if(!_pageNumber.isInvisible) {
                    _pageNumber.visibility = INVISIBLE
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
                if(_pageNumber.text.toString().toInt() < 2){

                }
                Log.d("Fragment Position", "2");
                if(btnOne.isInvisible) {
                    btnOne.visibility = VISIBLE
                }
                btnOne.setImageResource(R.drawable.ic_search)
                if(!btnTwo.isInvisible) {
                    btnTwo.visibility = INVISIBLE
                }
                if(_pageIncrement.isInvisible) {
                    _pageIncrement.visibility = VISIBLE
                }
                if(_pageDecrement.isInvisible && _pageNumber.text.toString().toInt() >= 2) {
                    _pageDecrement.visibility = VISIBLE
                }else{
                    _pageDecrement.visibility = INVISIBLE
                }
                if(_pageNumber.isInvisible) {
                    _pageNumber.visibility = VISIBLE
                }
                //refresh
                btnOne.setOnClickListener() {
                    val f = supportFragmentManager.findFragmentByTag("f2") as RecipesFragment
                    if (f != null) {
                        // Check if the fragment is properly initialized and attached to the activity
                        f.refreshClickFragment("Refresh")
                        pageReset(_pageNumber)
                        f.pageReset()
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
        when(tab?.position) {
            0 -> {
                //ToDo - Make the refresh automatic
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
    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){

                R.id.ibPageIncrementMain ->{
                    val f = supportFragmentManager.findFragmentByTag("f2") as RecipesFragment
                    Log.d("Main", "Increase")
                    if(increasePageNumber(_pageNumber)){
                            f.incrementPage(_pageNumber.text.toString().toInt())
                        }
                }
                R.id.ibPageDecrementMain ->{
                    val f = supportFragmentManager.findFragmentByTag("f2") as RecipesFragment
                    Log.d("Main", "Decrease")
                    if(decreasePageNumber(_pageNumber)) {
                        f.decrementPage(_pageNumber.text.toString().toInt())
                    }
                }
                R.id.ibMoreMain->{
                    val intent = Intent(this, SettingsActivity::class.java)
                    resultSettingsLauncher.launch(intent)
                }
            }
        }
    }

    var resultSettingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            val data:Intent? = result.data
            val f0 = supportFragmentManager.findFragmentByTag("f0") as FridgeFragment
            val f1 = supportFragmentManager.findFragmentByTag("f1") as GroceryListFragment
            Log.d("Settings", "ResultCode OK")
            f0.refreshClickFragment("null")
            f1.refreshClickFragment("null")
        }
    }

    private fun increasePageNumber(p: TextView): Boolean{
        var pNumber = p.text.toString().toInt()
        pNumber++
        p.text = pNumber.toString()
        if(pNumber > 1){
            if(_pageDecrement.isInvisible) {
                _pageDecrement.visibility = VISIBLE
            }
        }
        return true
    }

    private fun decreasePageNumber(p: TextView): Boolean{
        var pNumber = p.text.toString().toInt()
        if(pNumber <= 2){
            if(!_pageDecrement.isInvisible) {
                _pageDecrement.visibility = INVISIBLE
            }
        }
        if(pNumber > 1){
            pNumber--
            p.text = pNumber.toString()
            return true
        }
        return false
    }

    private fun pageReset(p: TextView){
        p.text = "1"
    }

    override fun pageForwardDisable() {
        _pageIncrement.visibility = INVISIBLE
    }

    override fun pageForwardEnable() {
        _pageIncrement.visibility = VISIBLE
    }

}