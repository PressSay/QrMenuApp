package com.example.qfmenu.ui.menu.category

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.R
import com.example.qfmenu.database.entity.CategoryDb
import com.example.qfmenu.database.entity.MenuDb
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.example.qfmenu.viewmodels.models.Category

class CategoryAdapter(
    private val context: Context,
    private val saveStateViewModel: SaveStateViewModel
): ListAdapter<CategoryDb, CategoryAdapter.ItemViewHolder>(DiffCallback) {
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<CategoryDb>() {
            override fun areItemsTheSame(oldItem: CategoryDb, newItem: CategoryDb): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: CategoryDb, newItem: CategoryDb): Boolean {
                return oldItem.categoryId == newItem.categoryId
            }
        }
    }

    class ItemViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val btnCategory = view.findViewById<AppCompatButton>(R.id.btnCategory)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_category, parent, false)
        return ItemViewHolder(adapterView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = currentList[position]
        holder.btnCategory.text = item.categoryName
        holder.btnCategory.setOnClickListener {
            saveStateViewModel.stateCategoryPositionMenu = holder.adapterPosition
            val categoryPos = holder.adapterPosition
            if (categoryPos < saveStateViewModel.stateDishesByCategories.size) {

            } else {
                for (i in (1..categoryPos)) {
                    saveStateViewModel.stateDishesByCategories.add(
                        mutableListOf()
                    )
                }
                Log.d("ForEach", "true")
            }

            holder.view.findNavController().popBackStack()
        }
    }
}