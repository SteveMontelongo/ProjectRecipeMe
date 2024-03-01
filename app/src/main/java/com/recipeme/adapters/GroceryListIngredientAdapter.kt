package com.recipeme.adapters

import android.graphics.Color
import android.graphics.drawable.Drawable
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
        val nameTextView: TextView = itemView.findViewById<TextView>(R.id.tvIngredientNameGroceryList)
        val quantityTextView: TextView = itemView.findViewById<TextView>(R.id.tvIngredientQuantityItemGroceryList)
        val unitLabelTextView: TextView = itemView.findViewById<TextView>(R.id.tvIngredientUnitItemGroceryList)
        val decrementButton: ImageButton = itemView.findViewById<ImageButton>(R.id.ibDecrementGroceryList)
        val incrementButton: ImageButton = itemView.findViewById<ImageButton>(R.id.ibIncrementGroceryList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryListIngredientAdapter.GroceryListIngredientViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val groceryListView = inflater.inflate(R.layout.item_grocery_list, parent, false)
        return GroceryListIngredientViewHolder(groceryListView)
    }

    override fun onBindViewHolder(holder: GroceryListIngredientViewHolder, position: Int) {
        val ingredient: Ingredient = ingredients[holder.absoluteAdapterPosition]
        holder.nameTextView.text = ingredient.name
        holder.quantityTextView.text = ingredient.amount.toString()
        holder.unitLabelTextView.text = ingredient.unit
        if(ingredient.amount.toInt() == 1){
            holder.decrementButton.setBackgroundResource(R.drawable.small_delete_button_shape)
            holder.decrementButton.setImageResource(R.drawable.ic_delete)
        }else{
            holder.decrementButton.setBackgroundResource(R.drawable.small_item_button_shape)
            holder.decrementButton.setImageResource(R.drawable.ic_decrement)
        }
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