package com.recipeme.adapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.recipeme.fragments.FridgeFragment
import com.recipeme.fragments.GroceryListFragment
import com.recipeme.fragments.RecipesFragment

class FragmentAdapter(fragmentActivity: FragmentActivity) :FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }

    fun getPageTitle(position: Int): String {
        when(position){
            0 -> return "Fridge"
            1 -> return "Grocery Lists"
            2 -> return "Recipes"
            else -> return "Main"
        }
    }

    override fun createFragment(position: Int): Fragment {
        Log.d("Manager", getPageTitle(position))
        when(position){
            0 -> {
                return FridgeFragment()
            }
            1 -> {
                return GroceryListFragment()
            }
            2 -> {
                return RecipesFragment()
            }
            else ->{
                throw IllegalArgumentException("Invalid position: $position")
            }
        }

    }

    fun getFragment(position: Int): Fragment? {
        Log.d("Manager2", position.toString())
        when(position){
            0 -> {
                return FridgeFragment()
            }
            1 -> {
                return GroceryListFragment()
            }
            2 -> {
                return RecipesFragment()
            }
            else ->{
                return GroceryListFragment()
            }
        }
    }

}