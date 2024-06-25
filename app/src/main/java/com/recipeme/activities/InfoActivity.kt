package com.recipeme.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.recipeme.R

class InfoActivity : AppCompatActivity() {
    private lateinit var _background: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val languageString = sharedPreferences.getString("language", "english")
        val cancelBtn = findViewById<ImageButton>(R.id.btnCancelInfo)
        _background = findViewById(R.id.backgroundAppInfo)
        val backgroundInt = sharedPreferences.getInt("background", R.drawable.recipe_me_plain)
        setBackground(backgroundInt)
        cancelBtn.setOnClickListener{
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
        }
        val pageLabel = findViewById<TextView>(R.id.infoLabel)
        val msg1 = findViewById<TextView>(R.id.groceryInfo1)
        val msg2 = findViewById<TextView>(R.id.groceryInfo2)
        val msg3 = findViewById<TextView>(R.id.groceryInfo3)
        val msg4 = findViewById<TextView>(R.id.groceryInfo4)
        val msg5 = findViewById<TextView>(R.id.groceryInfo5)
        val msg6 = findViewById<TextView>(R.id.groceryInfo6)
        val msg7 = findViewById<TextView>(R.id.groceryInfo7)
        val msg8 = findViewById<TextView>(R.id.groceryInfo8)
        val msg9 = findViewById<TextView>(R.id.groceryInfo9)
        pageLabel.text = getMsg(0, languageString!!)
        msg1.text = getMsg(1, languageString)
        msg2.text = getMsg(2, languageString)
        msg3.text = getMsg(3, languageString)
        msg4.text = getMsg(4, languageString)
        msg5.text = getMsg(5, languageString)
        msg6.text = getMsg(6, languageString)
        msg7.text = getMsg(7, languageString)
        msg8.text = getMsg(8, languageString)
        msg9.text = getMsg(9, languageString)

    }

    private fun getMsg(msgCode: Int, lang: String): String{
        if(lang == "english"){
            return when(msgCode){
                0 -> getString(R.string.info_label)
                1 -> getString(R.string.info_text_1)
                2 -> getString(R.string.info_text_2)
                3 -> getString(R.string.info_text_3)
                4 -> getString(R.string.info_text_4)
                5 -> getString(R.string.info_text_5)
                6 -> getString(R.string.info_text_6)
                7 -> getString(R.string.info_text_7)
                8 -> getString(R.string.info_text_8)
                9 -> getString(R.string.info_text_9)
                else -> getString(R.string.info_label)
            }
        }else if (lang == "spanish"){
            return when(msgCode){
                0 -> getString(R.string.info_label_sp)
                1 -> getString(R.string.info_text_1_sp)
                2 -> getString(R.string.info_text_2_sp)
                3 -> getString(R.string.info_text_3_sp)
                4 -> getString(R.string.info_text_4_sp)
                5 -> getString(R.string.info_text_5_sp)
                6 -> getString(R.string.info_text_6_sp)
                7 -> getString(R.string.info_text_7_sp)
                8 -> getString(R.string.info_text_8_sp)
                9 -> getString(R.string.info_text_9_sp)
                else -> getString(R.string.info_label_sp)
            }
        }
        return "invalid"
    }

    private fun setBackground(resId: Int){
        _background.setImageResource(resId)
    }
}