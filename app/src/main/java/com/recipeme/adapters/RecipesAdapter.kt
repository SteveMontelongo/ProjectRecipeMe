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
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.recipeme.R
import com.recipeme.interfaces.GroceryListOnItemClick
import com.recipeme.interfaces.RecipeOnClickItem
import com.recipeme.models.GroceryList
import com.recipeme.models.Recipe
import com.recipeme.models.RecipeResponse

class RecipesAdapter(private var recipes: List<Recipe>, private var listener: RecipeOnClickItem,private var context: Context) : RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder>(){

    inner class RecipesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nameTextView: TextView = itemView.findViewById<TextView>(R.id.tvRecipeNameItem)
        val imageView: ImageView = itemView.findViewById<ImageView>(R.id.ivRecipeItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesAdapter.RecipesViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val recipesView = inflater.inflate(R.layout.item_recipe, parent, false)
        return RecipesViewHolder(recipesView)
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        val recipe: Recipe = recipes[holder.absoluteAdapterPosition]
        holder.nameTextView.text = recipe.name
        Glide.with(context).applyDefaultRequestOptions(
            RequestOptions()
                .placeholder(R.drawable.ic_recipes).format(DecodeFormat.PREFER_ARGB_8888)
                .override(SIZE_ORIGINAL).transform(RoundedCorners(25))
                .error(R.drawable.ic_recipes)).load(recipe.image).into(holder.imageView)

        holder.imageView.setOnClickListener{
            this.listener.onClickItem(holder.absoluteAdapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return recipes.size

    }

}