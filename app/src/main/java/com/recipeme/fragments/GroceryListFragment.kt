package com.recipeme.fragments

import android.app.Activity
import android.content.Context
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
import android.widget.ImageButton
import android.widget.Toast
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
import com.recipeme.interfaces.MainFragmentInteraction
import com.recipeme.models.GroceryList
import com.recipeme.models.Ingredient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GroceryListFragment : Fragment(), View.OnClickListener, GroceryListOnItemClick, MainFragmentInteraction{
    private lateinit var _db: AppDatabase
    private lateinit var _groceryListDao: GroceryListDao
    private lateinit var _grocerylists: MutableList<GroceryList>
    private lateinit var _recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        _db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "recipe-me-database"
        ).build()
        _groceryListDao = _db.groceryListDao()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("GroceryFragment", "OnAttached")

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _grocerylists = emptyList<GroceryList>().toMutableList()
        loadGroceryLists()
        val groceryListsListAdapter = GroceryListsListAdapter(_grocerylists, this)
        _recyclerView = view.findViewById<RecyclerView>(R.id.rvGroceryListsList)
        _recyclerView.adapter = groceryListsListAdapter
        _recyclerView.layoutManager = LinearLayoutManager(this.context)
    }

    private fun loadGroceryLists(){
        _grocerylists.clear()
        GlobalScope.launch{
            val entities = emptyList<GroceryList>().toMutableList()
            context?.let{
                if(_groceryListDao.getAll().isNotEmpty()){
                    entities.addAll(_groceryListDao.getAll())
                }
            }
            Handler(Looper.getMainLooper()).post {
                for(entity in entities){
                    _grocerylists.add(entity)
                }
                _recyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_grocery_list, container, false)
        return view
    }


    override fun onClick(v: View?) {

    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            val data:Intent? = result.data
            val dataListName = data?.getStringExtra("ListName")
            val dataListDate = data?.getStringExtra("ListDate")
            _grocerylists.add(GroceryList(dataListName.toString(), dataListDate.toString(),
                emptyList<Ingredient>().toMutableList()))
            val indexInsert = _grocerylists.size
            _recyclerView.adapter?.notifyItemInserted(indexInsert)
        }
    }

    var resultItemLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            val data:Intent? = result.data
        }
    }

    var resultEditLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            val data:Intent? = result.data
            val dataNewListName = data?.getStringExtra("ListName")
            val dataNewListPosition = data?.getIntExtra("Position", 0)
            if (dataNewListPosition != null) {
                _grocerylists[dataNewListPosition].name = dataNewListName.toString()
            }
            _recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    override fun onClickDelete(position: Int) {
        GlobalScope.launch{
            context?.let{
                _groceryListDao.delete(_grocerylists[position])
            }
            Handler(Looper.getMainLooper()).post {
                _grocerylists.removeAt(position)
                _recyclerView.adapter?.notifyDataSetChanged()
            }
        }
        _recyclerView.adapter?.notifyItemRemoved(position)
    }

    override fun onClickEdit(position: Int, name: String) {
        val intent = Intent(this.context, GroceryListEditActivity::class.java)
        intent.putExtra("ListName", name)
        intent.putExtra("Position", position)
        resultEditLauncher.launch(intent)
    }

    override fun onClickItem(position: Int, name: String) {
        val intent = Intent(this.context, GroceryListContentActivity::class.java)
        intent.putExtra("ListName", name)
        intent.putExtra("Position", position)
        resultItemLauncher.launch(intent)
    }

    override fun refreshClickFragment(data: String) {
        Log.d("Refreshing GroceryLists", "Testing")
        loadGroceryLists()
    }

    override fun addClickFragment(data: String) {
        val intent = Intent(this.context, PopupGroceryListsList::class.java)
        resultLauncher.launch(intent)
    }

}

