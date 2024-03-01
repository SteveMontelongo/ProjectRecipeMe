package com.recipeme.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.recipeme.R
import com.recipeme.adapters.FridgeAdapter
import com.recipeme.daos.FridgeDao
import com.recipeme.databases.AppDatabase
import com.recipeme.interfaces.FridgeOnItemClick
import com.recipeme.interfaces.MainFragmentInteraction
import com.recipeme.models.Ingredient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FridgeFragment : Fragment(), View.OnClickListener, FridgeOnItemClick, MainFragmentInteraction{
    private lateinit var _db : AppDatabase
    private lateinit var _fridgeDao: FridgeDao
    private lateinit var _ingredients: MutableList<Ingredient>
    private lateinit var _recyclerview: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        _db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "recipe-me-database"
        ).build()
        _fridgeDao = _db.fridgeDao()
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("FridgeFragment", "OnCDetach")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("FridgeFragment", "OnDestory")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Save state information to the outState bundle
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
//            _fridgeDao = _db.fridgeDao()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        Log.d("Fragment", "OnViewCreated")

        _recyclerview = view.findViewById<RecyclerView>(R.id.rvFridgeIngredients)
        _ingredients = emptyList<Ingredient>().toMutableList()
        _recyclerview.adapter = FridgeAdapter(_ingredients, this, requireContext())
        _recyclerview.layoutManager = LinearLayoutManager(this.context)
//        view.findViewById<ImageButton>(R.id.iBtnRefreshFridge).setOnClickListener(this)
        GlobalScope.launch {
            var ingredientsToUpdate: MutableList<Ingredient> = emptyList<Ingredient>().toMutableList()
            ingredientsToUpdate.addAll(_fridgeDao.getAll())
            Handler(Looper.getMainLooper()).post{
                _ingredients.addAll(ingredientsToUpdate)
                _recyclerview.adapter?.notifyDataSetChanged()
            }
        }

        val activity = activity
        if (activity != null) {
            // Fragment is attached to an activity
            // You can access the activity's properties or call its methods here
            Log.d("activity attached", activity.toString())
        } else {
            // Fragment is not attached to any activity
            // Handle the case where the fragment is not attached to an activity
            Log.d("activity attached", "false")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //edit
//        val bundle = arguments
//        val msg = bundle?.getStringArrayList("mText")
//        if(msg !=null){
//            var toast = Toast.makeText(view?.context, "Hi", Toast.LENGTH_SHORT).show()
//
//        }
        Log.d("Fragment", "OnCreateView")
        return inflater.inflate(R.layout.fragment_fridge, container, false)
    }

    override fun onClick(v: View?) {
        if(v !=null){
            when(v.id){
//                R.id.iBtnRefreshFridge->{
//                    GlobalScope.launch{
//                        var ingredientsToUpdate: MutableList<Ingredient>
//                        ingredientsToUpdate = emptyList<Ingredient>().toMutableList()
//                        ingredientsToUpdate.addAll(_fridgeDao.getAll())
//
//                        Handler(Looper.getMainLooper()).post(){
//                            _ingredients.clear()
//                            _ingredients.addAll(ingredientsToUpdate)
//                            _recyclerview.adapter?.notifyDataSetChanged()
//                        }
//                    }
//
//                }
            }
        }
    }

    override fun onDelete(position: Int) {
        GlobalScope.launch {
            var ingredient = _ingredients[position]
            GlobalScope.launch {
                _fridgeDao.deleteIngredient(ingredient)
                Handler(Looper.getMainLooper()).post{
                    _ingredients.remove(ingredient)
                    _recyclerview.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    override fun refreshClickFragment(data: String) {
        GlobalScope.launch{
            var ingredientsToUpdate: MutableList<Ingredient> = emptyList<Ingredient>().toMutableList()
            ingredientsToUpdate.addAll(_fridgeDao.getAll())

            Handler(Looper.getMainLooper()).post(){
                _ingredients.clear()
                _ingredients.addAll(ingredientsToUpdate)
                _recyclerview.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun addClickFragment(data: String) {
        TODO("Not yet implemented")
    }

}