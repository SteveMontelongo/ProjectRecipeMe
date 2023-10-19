package com.recipeme.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.recipeme.R
import com.recipeme.activities.PopupGroceryListsList
import com.recipeme.adapters.GroceryListsListAdapter
import com.recipeme.models.GroceryList
import com.recipeme.models.Ingredient
import java.text.DateFormat
import java.util.Date

class GroceryListFragment : Fragment(), View.OnClickListener{
    lateinit var grocerylists: MutableList<GroceryList>
    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        grocerylists = emptyList<GroceryList>().toMutableList()
        val groceryListsListAdapter = GroceryListsListAdapter(grocerylists)
        recyclerView=
            view.findViewById<RecyclerView>(R.id.rvGroceryListsList)
        recyclerView.adapter = groceryListsListAdapter
        recyclerView.setLayoutManager(LinearLayoutManager(this.context))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_grocery_list, container, false)
        val addNewListButton = view.findViewById<Button>(R.id.btnAddNewList)
        addNewListButton?.setOnClickListener(this)
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GroceryListFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id){
                R.id.btnAddNewList ->{
                    val intent = Intent(this.context, PopupGroceryListsList::class.java)
                    resultLauncher.launch(intent)
                }
            }
        }
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            val data:Intent? = result.data
            val dataListName = data?.getStringExtra("ListName")
            Log.d("Test", dataListName.toString())
            val currentDate = DateFormat.getDateInstance().format(Date())
            Log.d("Date" , currentDate.toString())
            grocerylists.add(GroceryList(dataListName.toString(), currentDate, emptyList<Ingredient>()))
            val indexInsert = grocerylists.size
            recyclerView.adapter?.notifyItemInserted(indexInsert)
            //recyclerView.adapter?.notifyDataSetChanged()
        }
    }
}

