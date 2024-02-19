package com.recipeme.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.recipeme.R
import com.recipeme.adapters.FragmentAdapter
import com.recipeme.databases.AppDatabase
import com.recipeme.utils.IngredientsData
import java.io.BufferedReader
import java.io.InputStreamReader


class MainActivity : AppCompatActivity() {

    var tabTitle = arrayOf("Fridge", "GroceryList", "Recipes")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "recipe-me-database").build()


        var viewPager = findViewById<ViewPager2>(R.id.viewPager);
        var tabLayout = findViewById<TabLayout>(R.id.tabLayout);

        viewPager.adapter = FragmentAdapter(supportFragmentManager, lifecycle)

        TabLayoutMediator(tabLayout, viewPager){
            tab, position ->
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
            //Log.d("line", line!!)
            var lineSplit = line!!.split(";")
            data.map.set(lineSplit[1].toInt(), lineSplit[0])
        }

        Log.d("map", data.map.keys.toString())


//        val csvReader = CSVReaderBuilder(FileReader("src/main/res/resources/top-1k-ingredients.csv"))
//            .withCSVParser(CSVParserBuilder().withSeparator(';').build())
//            .build()
//
//// Maybe do something with the header if there is one
//        val header = csvReader.readNext()
//
//// Read the rest
//        var line: Array<String>? = csvReader.readNext()
//        while (line != null) {
//            // Do something with the data
//            println(line[0])
//
//            line = csvReader.readNext()
//        }

    }
}