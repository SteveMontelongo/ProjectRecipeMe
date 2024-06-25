package com.recipeme.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.recipeme.R
import com.recipeme.interfaces.GroceryListOnItemClick
import com.recipeme.models.GroceryList

class GroceryListsListAdapter(private var groceryLists: List<GroceryList>, private var clickListener: GroceryListOnItemClick) : RecyclerView.Adapter<GroceryListsListAdapter.GroceryListsListViewHolder>(){

    inner class GroceryListsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nameTextView: TextView = itemView.findViewById(R.id.tvGroceryListsList)
        val dateTextView: TextView = itemView.findViewById(R.id.tvGroceryListsListDate)
        val deleteButton: ImageButton = itemView.findViewById(R.id.ibDeleteGroceryListsList)
        val editButton: ImageButton = itemView.findViewById(R.id.ibEditGroceryListsList)
        val launchButton: ImageButton = itemView.findViewById(R.id.ibLaunchGroceryListsList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryListsListAdapter.GroceryListsListViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val groceryListsListView = inflater.inflate(R.layout.item_grocery_lists_list, parent, false)
        return GroceryListsListViewHolder(groceryListsListView)
    }

    override fun onBindViewHolder(holder: GroceryListsListViewHolder, position: Int) {
        val groceryList: GroceryList = groceryLists.get(holder.absoluteAdapterPosition)
        holder.nameTextView.text = groceryList.name
        holder.dateTextView.text = groceryList.timeCreated
        holder.nameTextView.setOnClickListener{
            this.clickListener.onClickItem(holder.absoluteAdapterPosition, groceryList.name)
        }
        holder.deleteButton.setOnLongClickListener{
            this.clickListener.onClickDelete(holder.absoluteAdapterPosition)
            true
        }
        holder.editButton.setOnClickListener{
            this.clickListener.onClickEdit(holder.absoluteAdapterPosition, groceryList.name)
        }
        holder.launchButton.setOnClickListener{
            this.clickListener.onClickLaunch(holder.absoluteAdapterPosition, groceryList.name)
        }
    }

    override fun getItemCount(): Int {
        return groceryLists.size

    }

}