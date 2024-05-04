package com.recipeme.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.recipeme.R
import com.recipeme.interfaces.FridgeOnItemClick
import com.recipeme.models.Ingredient

class FridgeAdapter(var ingredients: MutableList<Ingredient>, private var listener: FridgeOnItemClick, private var context: Context) : RecyclerView.Adapter<FridgeAdapter.FridgeIngredientViewHolder>(){

    inner class FridgeIngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val itemImageView: ImageView = itemView.findViewById<ImageView>(R.id.ivIngredientFridge)
        val nameTextView: TextView = itemView.findViewById<TextView>(R.id.tvIngredientNameFridge)
        val deleteButton: ImageButton = itemView.findViewById<ImageButton>(R.id.ibDeleteFridge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FridgeAdapter.FridgeIngredientViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val fridgeView = inflater.inflate(R.layout.item_fridge, parent, false)
        return FridgeIngredientViewHolder(fridgeView)
    }

    override fun onBindViewHolder(holder: FridgeIngredientViewHolder, position: Int) {
        val ingredient: Ingredient = ingredients.get(holder.absoluteAdapterPosition)
        Glide.with(context).load(ingredient.image).apply(
            RequestOptions().override(100, 100).fitCenter().transform(
                RoundedCorners(35))
            .placeholder(R.mipmap.ic_fruit)
            .error(R.mipmap.ic_fruit)).into(holder.itemImageView)
        holder.nameTextView.text = ingredient.name
        holder.deleteButton.setOnLongClickListener{
            this.listener.onDelete(holder.absoluteAdapterPosition)
            true
        }
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }
}