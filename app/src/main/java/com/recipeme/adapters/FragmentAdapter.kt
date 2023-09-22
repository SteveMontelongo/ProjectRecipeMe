package com.recipeme.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.recipeme.fragments.FridgeFragment
import com.recipeme.fragments.GroceryListFragment
import com.recipeme.fragments.RecipesFragment

class FragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return FridgeFragment()
            1 -> return GroceryListFragment()
            2 -> return RecipesFragment()
            else -> return GroceryListFragment()
        }
    }


}