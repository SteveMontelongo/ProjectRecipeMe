package com.recipeme.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.recipeme.R


class ThemesActivity : AppCompatActivity(), OnClickListener {
    private lateinit var _sharedPreferences: SharedPreferences
    private lateinit var _background: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_themes)

        _sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val cancelBtn = findViewById<ImageButton>(R.id.btnCancelThemes)
        val plainBtn = findViewById<Button>(R.id.btnPainTheme).setOnClickListener(this)
        val iceBtn = findViewById<Button>(R.id.btnIcecubesTheme).setOnClickListener(this)
        val fruitBtn = findViewById<Button>(R.id.btnFruityTheme).setOnClickListener(this)
        _background = findViewById<ImageView>(R.id.backgroundAppThemes)

        val backgroundInt = _sharedPreferences.getInt("background", R.drawable.recipe_me_plain)
        setBackground(backgroundInt)

        cancelBtn.setOnClickListener{
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onClick(v: View?) {
        if(v != null){
            val editor: SharedPreferences.Editor = _sharedPreferences.edit()
            when(v.id){
                R.id.btnPainTheme->{
                    val backgroundResId = R.drawable.recipe_me_plain
                    editor.putInt("background", backgroundResId)
                    _background.setImageResource(R.drawable.recipe_me_plain)
                    editor.apply()
                }
                R.id.btnIcecubesTheme->{
                    val backgroundResId = R.drawable.recipe_me_icecubes
                    editor.putInt("background", backgroundResId)
                    _background.setImageResource(R.drawable.recipe_me_icecubes)
                    editor.apply()
                }
                R.id.btnFruityTheme->{
                    val backgroundResId = R.drawable.recipe_me_fruity
                    editor.putInt("background", backgroundResId)
                    _background.setImageResource(R.drawable.recipe_me_fruity)
                    editor.apply()
                }
            }
        }
    }

    private fun setBackground(resId: Int){
        _background.setImageResource(resId)
    }
}
