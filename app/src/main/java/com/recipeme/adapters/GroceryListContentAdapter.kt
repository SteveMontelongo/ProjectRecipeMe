package com.recipeme.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.recipeme.R
import com.recipeme.interfaces.GroceryContentOnItemClick
import com.recipeme.models.Ingredient

class GroceryListContentAdapter(private var ingredients: MutableList<Ingredient>, private var listener: GroceryContentOnItemClick) : RecyclerView.Adapter<GroceryListContentAdapter.GroceryListContentIngredientViewHolder>(){

    inner class GroceryListContentIngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nameTextView = itemView.findViewById<TextView>(R.id.tvItemGroceryListContentIngredientName)
        val quantityTextView = itemView.findViewById<TextView>(R.id.etItemGroceryListContentIngredientQuantity)
        //val unitLabelTextView = itemView.findViewById<TextView>(R.id.tvIngredientUnitItemGroceryList)
        val statusButton = itemView.findViewById<ImageButton>(R.id.ibItemGroceryListContentStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryListContentAdapter.GroceryListContentIngredientViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val groceryListView = inflater.inflate(R.layout.item_grocery_list_content, parent, false)
        return GroceryListContentIngredientViewHolder(groceryListView)
    }

    override fun onBindViewHolder(holder: GroceryListContentIngredientViewHolder, position: Int) {
        val ingredient: Ingredient = ingredients.get(holder.absoluteAdapterPosition)
        holder.nameTextView.text = ingredient.name
        holder.quantityTextView.text = ingredient.amount.toString()
        //holder.unitLabelTextView.text = ingredient.unit
        holder.statusButton.setImageResource(
            if(ingredients[holder.absoluteAdapterPosition].obtained) R.drawable.ic_confirm_checked else R.drawable.ic_confirm_unchecked)
        holder.statusButton.setOnClickListener{
            this.listener.onClickStatus(holder.absoluteAdapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }
}