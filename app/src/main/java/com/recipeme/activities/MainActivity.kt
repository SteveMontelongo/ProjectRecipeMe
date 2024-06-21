package com.recipeme.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
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
    private lateinit var _background: ImageView
    private var _lastClickTime = 0.0
    private lateinit var _tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var viewPager = findViewById<ViewPager2>(R.id.viewPager)
        _tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        findViewById<ImageButton>(R.id.ibMoreMain).setOnClickListener(this)
        _pageIncrement= findViewById<ImageButton>(R.id.ibPageIncrementMain)
        _pageIncrement.setOnClickListener(this)
        _pageDecrement = findViewById<ImageButton>(R.id.ibPageDecrementMain)
        _pageDecrement.setOnClickListener(this)
        _pageNumber = findViewById<TextView>(R.id.tvPageMain)
        _textLabel = findViewById<TextView>(R.id.tvLabelMain)
        _adapter = FragmentAdapter(this)
        _background = findViewById<ImageView>(R.id.backgroundApp)


        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val backgroundInt = sharedPreferences.getInt("background", R.drawable.recipe_me_plain)
        setBackground(backgroundInt)

//        //set english
//        val editor: SharedPreferences.Editor = sharedPreferences.edit()
//        editor.putString("language", "english")
//        editor.apply()

        //testing
        val languageString = sharedPreferences.getString("language", "english")
        Log.d("Language test", languageString.toString())

        viewPager.adapter = _adapter

        var tabIcon = listOf<Int>(R.drawable.ic_fridge, R.drawable.ic_grocery, R.drawable.ic_recipes)

        var tabTitle = tabTitles(languageString!!)

        _tabLayout.addOnTabSelectedListener(this)
        TabLayoutMediator(_tabLayout, viewPager){
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
        Log.d("Fragment Title" , tab?.text.toString())
        _textLabel.text = tab?.text.toString()
        val btnOne = findViewById<ImageButton>(R.id.ibOneMain)
        val btnTwo = findViewById<ImageButton>(R.id.ibTwoMain)

        when(tab?.position){
            0 ->{
                Log.d("Fragment Position", "0");
                setButton(btnOne, R.drawable.ic_refresh, VISIBLE)
                setButton(btnTwo, R.drawable.ic_help, VISIBLE)
                if(!_pageIncrement.isInvisible) {
                    pageForwardDisable()
                }
                if(!_pageDecrement.isInvisible) {
                    pagePreviousDisable()
                }
                if(!_pageNumber.isInvisible) {
                    _pageNumber.visibility = INVISIBLE
                }

                //autoRefresh
                try{
                    val f = supportFragmentManager.findFragmentByTag("f0") as FridgeFragment
                    if (f != null) {
                        Log.d("Main Fridge", "autoRefresh")
                        // Check if the fragment is properly initialized and attached to the activity
                        f.refreshClickFragment("Refresh")
                    } else {
                        // Handle the case where the fragment or _fridgeDao is not properly initialized
                        Log.e(
                            "MainActivity",
                            "Fragment is not properly initialized"
                        )
                    }
                }catch (e: java.lang.Exception
                ){
                    Log.e("Error", e.toString())
                }
                //manualRefresh
                findViewById<ImageButton>(R.id.ibOneMain).setOnClickListener() {
                    if(isSpamClick())return@setOnClickListener
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

                btnTwo.setOnClickListener() {
                    if(isSpamClick())return@setOnClickListener
                    var intent = Intent(this, InfoActivity::class.java)
                    resultHelpLauncher.launch(intent)
                }
            }
            1 ->{
                Log.d("Fragment Position", "1");
                setButton(btnOne, R.drawable.ic_increment, VISIBLE)
                setButton(btnTwo, R.drawable.ic_help, VISIBLE)
                if(!_pageIncrement.isInvisible) {
                    pageForwardDisable()
                }
                if(!_pageDecrement.isInvisible) {
                    pagePreviousDisable()
                }
                if(!_pageNumber.isInvisible) {
                    _pageNumber.visibility = INVISIBLE
                }
                btnOne.setOnClickListener() {
                    if(isSpamClick())return@setOnClickListener
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

                btnTwo.setOnClickListener() {
                    if(isSpamClick())return@setOnClickListener
                    var intent = Intent(this, InfoActivity::class.java)
                    resultHelpLauncher.launch(intent)
                }
            }
            2 ->{
                Log.d("Fragment Position", "2");
                setButton(btnOne, R.drawable.ic_search, VISIBLE)
                try {
                    val f = supportFragmentManager.findFragmentByTag("f2") as RecipesFragment
                    if(f.isFavoriteTabActive()) {
                        setButton(btnTwo, R.drawable.ic_cached, VISIBLE)
                    }else{
                        setButton(btnTwo, R.drawable.ic_favorite_filled_black, VISIBLE)
                    }
//                    if (_pageIncrement.isInvisible) {
//
//                        if (f.isForwardPageDisabled()) {
//                            pageForwardDisable()
//                        } else {
//                            pageForwardEnable()
//                        }
//                    }
                }catch(e: java.lang.Exception){
                    setButton(btnTwo, R.drawable.ic_favorite_filled_black, VISIBLE)
                    pagePreviousDisable()
                    pageForwardDisable()
                    Log.e("error", e.toString())
                }

                //Search
                btnOne.setOnClickListener() {
                    if(isSpamClick())return@setOnClickListener
                    var sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
                    val languageString = sharedPreferences.getString("language", "english")
                    val f = supportFragmentManager.findFragmentByTag("f2") as RecipesFragment
                    if (f != null) {
                        // Check if the fragment is properly initialized and attached to the activity
                        f.refreshClickFragment("Refresh")
                        pageReset(_pageNumber)
                        pagePreviousDisable()
                        f.pageReset()
                        _textLabel.text = getMsg(0, languageString!!)
                        if(_pageDecrement.isInvisible && _pageNumber.text.toString().toInt() >= 2) {
                            pagePreviousEnable()
                        }else{
                            pagePreviousDisable()
                        }
                        if(_pageNumber.isInvisible) {
                            _pageNumber.visibility = VISIBLE
                        }
                    } else {
                        // Handle the case where the fragment or _fridgeDao is not properly initialized
                        Log.e(
                            "MainActivity",
                            "Fragment is not properly initialized"
                        )
                    }
                }

                //toggle favorites/cache
                btnTwo.setOnClickListener() {
                    if(isSpamClick())return@setOnClickListener
                    var sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
                    val languageString = sharedPreferences.getString("language", "english")

                    val f = supportFragmentManager.findFragmentByTag("f2") as RecipesFragment
                    if (f != null) {
                        pageForwardDisable()
                        pagePreviousDisable()
                        _pageNumber.visibility = INVISIBLE
                        // Check if the fragment is properly initialized and attached to the activity
                        if(f.isFavoriteTabActive()){
                            f.favoriteClickFragment("SAVED")
                            btnTwo.setImageResource(R.drawable.ic_favorite_filled_black)
                            _textLabel.text = getMsg(2, languageString!!)
                        }else{

                            f.favoriteClickFragment("FAVORITE")
                            btnTwo.setImageResource(R.drawable.ic_cached)
                            _textLabel.text = getMsg(1, languageString!!)
                        }
                        pageReset(_pageNumber)
                        pagePreviousDisable()
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

    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){

                R.id.ibPageIncrementMain ->{
                    if(isSpamClick())return
                    val f = supportFragmentManager.findFragmentByTag("f2") as RecipesFragment
                    Log.d("Main", "Increase")
                    if(increasePageNumber(_pageNumber)){
                            f.incrementPage(_pageNumber.text.toString().toInt())
                        }
                }
                R.id.ibPageDecrementMain ->{
                    if(isSpamClick())return
                    val f = supportFragmentManager.findFragmentByTag("f2") as RecipesFragment
                    Log.d("Main", "Decrease")
                    if(decreasePageNumber(_pageNumber)) {
                        f.decrementPage(_pageNumber.text.toString().toInt())
                    }
                }
                R.id.ibMoreMain->{
                    if(isSpamClick())return
                    val intent = Intent(this, SettingsActivity::class.java)
                    resultSettingsLauncher.launch(intent)
                }
            }
        }
    }

    private var resultSettingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            val data:Intent? = result.data
            try {
                val f0 = supportFragmentManager.findFragmentByTag("f0") as FridgeFragment
                val f1 = supportFragmentManager.findFragmentByTag("f1") as GroceryListFragment
                f0.refreshClickFragment("null")
                f1.refreshClickFragment("null")
            }catch(e: Exception){
                Log.e("Error", e.toString())
            }
        }
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val backgroundInt = sharedPreferences.getInt("background", R.drawable.recipe_me_plain)
        Log.d("Theme Test", backgroundInt.toString())
        setBackground(backgroundInt)
    }

    private var resultHelpLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            val data:Intent? = result.data
        }
    }

    private fun increasePageNumber(p: TextView): Boolean{
        var pNumber = p.text.toString().toInt()
        pNumber++
        p.text = pNumber.toString()
        if(pNumber > 1){
            pagePreviousEnable()
        }
        return true
    }

    private fun decreasePageNumber(p: TextView): Boolean{
        var pNumber = p.text.toString().toInt()
        if(pNumber <= 2){
            pagePreviousDisable()
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

    fun pageInvisible(){
        _pageNumber.visibility = INVISIBLE
    }

    override fun pageForwardDisable() {
        if(_pageIncrement.visibility == VISIBLE) {
            _pageIncrement.visibility = INVISIBLE
        }
    }

    override fun pageForwardEnable() {
        if(_pageIncrement.visibility == INVISIBLE) {
            _pageIncrement.visibility = VISIBLE
        }
    }

    override fun pagePreviousDisable() {
        if(_pageDecrement.visibility == VISIBLE) {
            _pageDecrement.visibility = INVISIBLE
        }
    }

    override fun pagePreviousEnable() {
        if(_pageDecrement.visibility == INVISIBLE) {
            _pageDecrement.visibility = VISIBLE
        }
    }

    private fun setButton(button: ImageButton, resId: Int, visibility: Int){
        if(button.visibility != visibility){
            button.visibility = visibility
        }
        button.setImageResource(resId)
    }

    private fun setBackground(resId: Int){
        _background.setImageResource(resId)
        if(resId == R.drawable.recipe_me_dark){
            _tabLayout.setBackgroundColor(resources.getColor(R.color.dark, theme))
        }else{
            _tabLayout.setBackgroundColor(resources.getColor(R.color.white, theme))
        }
    }

    private fun isSpamClick(): Boolean{
        if (SystemClock.elapsedRealtime() -_lastClickTime < 2000){
            Log.d("Click", "Too fast")
            return true
        }
        _lastClickTime = SystemClock.elapsedRealtime().toDouble();
        return false
    }

    private fun tabTitles(lang: String): List<String>{
        when(lang){
            "english" -> {
                return listOf<String>("Fridge", "Grocery", "Recipes")
            }
            "spanish"->{
                return listOf<String>("Refrigerador", "Listas", "Recetas")
            }
        }
        //default
        return listOf<String>("Fridge", "Grocery", "Recipes")
    }

    private fun getMsg(msgCode: Int, lang: String): String{
        if(lang == "english"){
            return when(msgCode){
                0 -> getString(R.string.main_page_label_recipes)
                1 -> getString(R.string.main_page_label_favorite_recipes)
                2 -> getString(R.string.main_page_label_saved_recipes)
                else -> getString(R.string.main_page_label_recipes)
            }
        }else if (lang == "spanish"){
            return when(msgCode){
                0 -> getString(R.string.main_page_label_recipes_sp)
                1 -> getString(R.string.main_page_label_favorite_recipes_sp)
                2 -> getString(R.string.main_page_label_saved_recipes_sp)
                else -> getString(R.string.main_page_label_recipes_sp)
            }
        }
        return "invalid"
    }

}