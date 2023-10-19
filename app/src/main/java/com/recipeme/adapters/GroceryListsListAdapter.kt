package com.recipeme.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.recipeme.R
import com.recipeme.models.GroceryList

class GroceryListsListAdapter(private var groceryLists: List<GroceryList>) : RecyclerView.Adapter<GroceryListsListAdapter.GroceryListsListViewHolder>(){

    inner class GroceryListsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nameTextView = itemView.findViewById<TextView>(R.id.tvGroceryListsList)
        val dateTextView = itemView.findViewById<TextView>(R.id.tvGroceryListsListDate)
        val deleteButton = itemView.findViewById<ImageButton>(R.id.ibDeleteGroceryListsList)
        val editButton = itemView.findViewById<ImageButton>(R.id.ibEditGroceryListsList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryListsListAdapter.GroceryListsListViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val groceryListsListView = inflater.inflate(R.layout.item_grocery_lists_list, parent, false)
        return GroceryListsListViewHolder(groceryListsListView)
    }

    override fun onBindViewHolder(holder: GroceryListsListViewHolder, position: Int) {
        val groceryList: GroceryList = groceryLists.get(position)
        holder.nameTextView.text = groceryList.name
        holder.dateTextView.text = groceryList.timeCreated
        val delete = holder.deleteButton
        val edit = holder.editButton
    }

    override fun getItemCount(): Int {
        return groceryLists.size

    }

}