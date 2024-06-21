package com.recipeme.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.recipeme.R

class LanguagesActivity : AppCompatActivity() {
    private lateinit var _background: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_languages)

        var sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val languageString = sharedPreferences.getString("language", "english")
        var pageLabel = findViewById<TextView>(R.id.languagesLabel)
        pageLabel.text = getMsg(0, languageString!!)
        val cancelBtn = findViewById<ImageButton>(R.id.btnCancelLanguages)
        val englishBtn = findViewById<Button>(R.id.btnEnglish)
        englishBtn.text = getMsg(1, languageString!!)
        val spanishBtn = findViewById<Button>(R.id.btnSpanish)
        spanishBtn.text = getMsg(2, languageString!!)
        _background = findViewById<ImageView>(R.id.backgroundAppLanguages)
        val backgroundInt = sharedPreferences.getInt("background", R.drawable.recipe_me_plain)
        setBackground(backgroundInt)
        englishBtn.setOnClickListener(){
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("language", "english")
            editor.apply()
            pageLabel.text = getMsg(0, "english")
            englishBtn.text = getMsg(1, "english")
            spanishBtn.text = getMsg(2, "english")
        }
        spanishBtn.setOnClickListener(){
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("language", "spanish")
            editor.apply()
            pageLabel.text = getMsg(0, "spanish")
            englishBtn.text = getMsg(1, "spanish")
            spanishBtn.text = getMsg(2, "spanish")
        }

        cancelBtn.setOnClickListener{
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun getMsg(msgCode: Int, lang: String): String{
        if(lang == "english"){
            return when(msgCode){
                0 -> getString(R.string.settings_languages)
                1 -> getString(R.string.english_label)
                2 -> getString(R.string.spanish_label)
                else -> getString(R.string.settings_languages)
            }
        }else if (lang == "spanish"){
            return when(msgCode){
                0 -> getString(R.string.settings_languages_sp)
                1 -> getString(R.string.english_label_sp)
                2 -> getString(R.string.spanish_label_sp)
                else -> getString(R.string.settings_languages_sp)
            }
        }
        return "invalid"
    }
    private fun setBackground(resId: Int){
        _background.setImageResource(resId)
    }

}