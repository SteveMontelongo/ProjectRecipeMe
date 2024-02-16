package com.recipeme.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.recipeme.R
import com.recipeme.interfaces.GroceryIngredientOnQuantityClick
import com.recipeme.models.Ingredient

class GroceryListIngredientAdapter(private var ingredients: List<Ingredient>, private val listener: GroceryIngredientOnQuantityClick) : RecyclerView.Adapter<GroceryListIngredientAdapter.GroceryListIngredientViewHolder>(){

    inner class GroceryListIngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nameTextView = itemView.findViewById<TextView>(R.id.tvIngredientNameGroceryList)
        val quantityTextView = itemView.findViewById<TextView>(R.id.tvIngredientQuantityItemGroceryList)
        val unitLabelTextView = itemView.findViewById<TextView>(R.id.tvIngredientUnitItemGroceryList)
        val decrementButton = itemView.findViewById<ImageButton>(R.id.ibDecrementGroceryList)
        val incrementButton = itemView.findViewById<ImageButton>(R.id.ibIncrementGroceryList)
        //val obtainedButton = itemView.findViewById
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryListIngredientAdapter.GroceryListIngredientViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val groceryListView = inflater.inflate(R.layout.item_grocery_list, parent, false)
        return GroceryListIngredientViewHolder(groceryListView)
    }

    override fun onBindViewHolder(holder: GroceryListIngredientViewHolder, position: Int) {
        val ingredient: Ingredient = ingredients.get(holder.absoluteAdapterPosition)
        holder.nameTextView.text = ingredient.name
        holder.quantityTextView.text = ingredient.amount.toString()
        holder.unitLabelTextView.text = ingredient.unit
        holder.decrementButton.setOnClickListener{
            this.listener.onClickDecrement(holder.absoluteAdapterPosition)
        }
        holder.incrementButton.setOnClickListener{
            this.listener.onClickIncrement(holder.absoluteAdapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

}