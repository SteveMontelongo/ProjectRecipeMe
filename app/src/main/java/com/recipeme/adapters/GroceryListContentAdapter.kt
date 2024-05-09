package com.recipeme.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.recipeme.R
import com.recipeme.interfaces.GroceryContentOnItemClick
import com.recipeme.models.Ingredient

class GroceryListContentAdapter(private var ingredients: MutableList<Ingredient>, private var listener: GroceryContentOnItemClick, private val context: Context) : RecyclerView.Adapter<GroceryListContentAdapter.GroceryListContentIngredientViewHolder>(){

    inner class GroceryListContentIngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nameTextView: TextView = itemView.findViewById<TextView>(R.id.tvItemGroceryListContentIngredientName)
        val quantityTextView: TextView = itemView.findViewById<TextView>(R.id.etItemGroceryListContentIngredientQuantity)
        val statusButton: ImageButton = itemView.findViewById<ImageButton>(R.id.ibItemGroceryListContentStatus)
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
        if(ingredient.obtained){
            holder.nameTextView.setTextColor(ContextCompat.getColor(context, R.color.cart_item_checked))
        }else if(ingredient.custom){
            holder.nameTextView.setTextColor(ContextCompat.getColor(context, R.color.custom_item))
        }else{
            holder.nameTextView.setTextColor(ContextCompat.getColor(context, R.color.black))
        }
        holder.quantityTextView.text = ingredient.amount.toString()
        holder.statusButton.setImageResource(
            if(ingredients[holder.absoluteAdapterPosition].obtained){
                R.drawable.ic_cart
            }
            else{
                R.drawable.ic_add_cart_item
            }
        )

        if(ingredients[holder.absoluteAdapterPosition].obtained){
            holder.statusButton.setBackgroundResource(R.drawable.checkout_item_confirm_shape)
        }else{
            holder.statusButton.setBackgroundResource(R.drawable.checkout_item_shape)
        }


        holder.statusButton.setOnClickListener{
            this.listener.onClickStatus(holder.absoluteAdapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }
}