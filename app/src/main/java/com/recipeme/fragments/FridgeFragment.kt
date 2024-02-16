package com.recipeme.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.room.Database
import androidx.room.Room
import com.recipeme.R
import com.recipeme.adapters.FridgeAdapter
import com.recipeme.daos.FridgeDao
import com.recipeme.daos.GroceryListDao
import com.recipeme.databases.AppDatabase
import com.recipeme.interfaces.FridgeOnItemClick
import com.recipeme.models.GroceryList
import com.recipeme.models.Ingredient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FridgeFragment : Fragment(), View.OnClickListener, FridgeOnItemClick{
    lateinit var db : AppDatabase
    lateinit var fridgeDao: FridgeDao
    lateinit var ingredients: MutableList<Ingredient>
    lateinit var recyclerview: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "recipe-me-database"
        ).build()
        fridgeDao = db.fridgeDao()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){

        recyclerview = view.findViewById<RecyclerView>(R.id.rvFridgeIngredients)
        ingredients = emptyList<Ingredient>().toMutableList()
        //set adapter
        recyclerview.adapter = FridgeAdapter(ingredients, this, requireContext())
        recyclerview.setLayoutManager(LinearLayoutManager(this.context))
        view.findViewById<ImageButton>(R.id.iBtnRefreshFridge).setOnClickListener(this)
        GlobalScope.launch {
            var ingredientsToUpdate: MutableList<Ingredient> = emptyList<Ingredient>().toMutableList()
            ingredientsToUpdate.addAll(fridgeDao.getAll())
            Handler(Looper.getMainLooper()).post{
                Log.d("Fridge Loading", ingredientsToUpdate.toString())
                ingredients.addAll(ingredientsToUpdate)
                recyclerview.adapter?.notifyDataSetChanged()
            }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //view?.findViewById<ImageButton>(R.id.ibDeleteFridge)?.setOnClickListener(this)

        return inflater.inflate(R.layout.fragment_fridge, container, false)
    }

    override fun onClick(v: View?) {
        if(v !=null){
            when(v.id){
                //nothing for now
                R.id.iBtnRefreshFridge->{
                    GlobalScope.launch{
                        var ingredientsToUpdate: MutableList<Ingredient>
                        ingredientsToUpdate = emptyList<Ingredient>().toMutableList()
                        ingredientsToUpdate.addAll(fridgeDao.getAll())

                        Handler(Looper.getMainLooper()).post(){
                            ingredients.clear()
                            ingredients.addAll(ingredientsToUpdate)
                            Log.d("Fridge Refreshing", ingredientsToUpdate.toString())
                            recyclerview.adapter?.notifyDataSetChanged()
                        }
                    }

                }
            }
        }
    }

    override fun onDelete(position: Int) {
        GlobalScope.launch {
            var ingredient = ingredients[position]
            GlobalScope.launch {
                fridgeDao.deleteIngredient(ingredient)
                Handler(Looper.getMainLooper()).post{
                    ingredients.remove(ingredient)
                    recyclerview.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

}