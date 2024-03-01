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

class RecipeConfirmationAdapter(private var ingredients: MutableList<Ingredient>, private var listener: GroceryContentOnItemClick) : RecyclerView.Adapter<RecipeConfirmationAdapter.RecipeConfirmationViewHolder>(){

    inner class RecipeConfirmationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nameTextView: TextView = itemView.findViewById<TextView>(R.id.tvItemRecipeConfirmation)
        val statusButton: ImageButton = itemView.findViewById<ImageButton>(R.id.ibItemRecipeConfirmationStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeConfirmationAdapter.RecipeConfirmationViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val recipeListView = inflater.inflate(R.layout.item_recipe_confirmation, parent, false)
        return RecipeConfirmationViewHolder(recipeListView)
    }

    override fun onBindViewHolder(holder: RecipeConfirmationViewHolder, position: Int) {
        val ingredient: Ingredient = ingredients.get(holder.absoluteAdapterPosition)
        holder.nameTextView.text = ingredient.name
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