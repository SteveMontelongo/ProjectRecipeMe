package com.recipeme.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.recipeme.R
import com.recipeme.activities.GroceryListContentActivity
import com.recipeme.activities.GroceryListEditActivity
import com.recipeme.activities.PopupGroceryListsList
import com.recipeme.adapters.GroceryListsListAdapter
import com.recipeme.daos.GroceryListDao
import com.recipeme.databases.AppDatabase
import com.recipeme.interfaces.GroceryListOnItemClick
import com.recipeme.models.GroceryList
import com.recipeme.models.Ingredient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GroceryListFragment : Fragment(), View.OnClickListener, GroceryListOnItemClick{
    lateinit var db: AppDatabase
    lateinit var groceryListDao: GroceryListDao
    lateinit var grocerylists: MutableList<GroceryList>
    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "recipe-me-database"
        ).build()
        groceryListDao = db.groceryListDao()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        grocerylists = emptyList<GroceryList>().toMutableList()
        //grocerylists = emptyList<GroceryList>().toMutableList()
            GlobalScope.launch{
                val entities = emptyList<GroceryList>().toMutableList()
                context?.let{
                    if(groceryListDao.getAll().isNotEmpty()){
                         entities.addAll(groceryListDao.getAll())
                    }
                }

                Log.e("TAG", "onViewCreated: " + groceryListDao.getAll())
                //grocerylists = groceryListDao.getAll()
                Handler(Looper.getMainLooper()).post {
                    // Code here will run in UI thread
                    for(entity in entities){
                        grocerylists.add(entity)
                    }
                    //Log.d("Post : ", grocerylists.toString())
                    recyclerView.adapter?.notifyDataSetChanged()
                }
            }
            //groceryListDao.getAll()

        val groceryListsListAdapter = GroceryListsListAdapter(grocerylists, this)
        recyclerView=
            view.findViewById<RecyclerView>(R.id.rvGroceryListsList)
        recyclerView.adapter = groceryListsListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)
//        Log.d("Pre : ", grocerylists.toString())
//        grocerylists.add(GroceryList("1", "20", emptyList<Ingredient>().toMutableList()))
//        Log.d("TEST : ", grocerylists.toString())
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
            val dataListDate = data?.getStringExtra("ListDate")
            Log.d("Test", dataListName.toString())
            Log.d("Date" , dataListDate.toString())
            grocerylists.add(GroceryList(dataListName.toString(), dataListDate.toString(),
                emptyList<Ingredient>().toMutableList()))
            val indexInsert = grocerylists.size
            recyclerView.adapter?.notifyItemInserted(indexInsert)
            //recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    var resultItemLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        Log.d("Result Code", result.resultCode.toString())
        if(result.resultCode == Activity.RESULT_OK){
            val data:Intent? = result.data
            //no data processing
        }
    }

    var resultEditLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        Log.d("Result Code", result.resultCode.toString())
        if(result.resultCode == Activity.RESULT_OK){
            val data:Intent? = result.data
            val dataNewListName = data?.getStringExtra("ListName")
            val dataNewListPosition = data?.getIntExtra("Position", 0)
            Log.d("New List Name", dataNewListName.toString())
            Log.d("New List Position", dataNewListPosition.toString())
            if (dataNewListPosition != null) {
                grocerylists[dataNewListPosition].name = dataNewListName.toString()
            }
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    override fun onClickDelete(position: Int) {
        Log.d("Delete", position.toString())
        GlobalScope.launch{
            val entities = emptyList<GroceryList>().toMutableList()

            context?.let{
                groceryListDao.delete(grocerylists[position])
            }

            Log.e("DELETE", "Deleted: " + grocerylists[position])
            Handler(Looper.getMainLooper()).post {
                // Code here will run in UI thread
                //Log.d("Post : ", grocerylists.toString())
                grocerylists.removeAt(position)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }
        recyclerView.adapter?.notifyItemRemoved(position)
    }

    override fun onClickEdit(position: Int, name: String) {
        Log.d("Edit", position.toString() + " : " + name)
        val intent = Intent(this.context, GroceryListEditActivity::class.java)
        intent.putExtra("ListName", name)
        intent.putExtra("Position", position)
        resultEditLauncher.launch(intent)
    }

    override fun onClickItem(position: Int, name: String) {
        Log.d("Edit", position.toString() + " : " + name)
        val intent = Intent(this.context, GroceryListContentActivity::class.java)
        intent.putExtra("ListName", name)
        intent.putExtra("Position", position)
        resultItemLauncher.launch(intent)
    }

}

