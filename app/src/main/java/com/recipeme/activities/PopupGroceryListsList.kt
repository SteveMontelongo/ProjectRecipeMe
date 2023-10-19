package com.recipeme.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.recipeme.R

class PopupGroceryListsList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup_grocery_lists_list)
        val name = findViewById<EditText>(R.id.etGListName)
        val cancelButton = findViewById<Button>(R.id.btnCancelPopUpGroceryListsList)
        val confirmButton = findViewById<Button>(R.id.btnConfirmPopUpGroceryListsList)

        cancelButton.setOnClickListener(){
            finish()
        }
        confirmButton.setOnClickListener(){
            if(name.text.toString().length < 1){
                Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show()
            }else if(name.text.toString().length > 20) {
                Toast.makeText(this, "Please limit character length to 20 characters", Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent()
                intent.putExtra("ListName", name.text.toString())
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}

