package com.recipeme.adapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.recipeme.fragments.FridgeFragment
import com.recipeme.fragments.GroceryListFragment
import com.recipeme.fragments.RecipesFragment

class FragmentAdapter(fragmentActivity: FragmentActivity) :FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }

    private fun getPageTitle(position: Int): String {
        return when(position){
            0 -> "Fridge"
            1 -> "Grocery Lists"
            2 -> "Recipes"
            else -> "Main"
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
        return when(position){
            0 -> {
                FridgeFragment()
            }
            1 -> {
                GroceryListFragment()
            }
            2 -> {
                RecipesFragment()
            }
            else ->{
                GroceryListFragment()
            }
        }
    }

}