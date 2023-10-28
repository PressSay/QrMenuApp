package com.example.qfmenu.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.viewmodels.DishViewModel
import com.example.qfmenu.R
import com.example.qfmenu.database.entity.DishDb
import com.example.qfmenu.viewmodels.SaveStateViewModel

class ConfigDishAdapter (
    private val dishViewModel: DishViewModel,
    private val context: Context,
    private val stateViewDishModel: SaveStateViewModel,
) : ListAdapter<DishDb, ConfigDishAdapter.ItemViewHolder>(DiffCallback) {
    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val btnConfig =
            view.findViewById<AppCompatImageButton>(R.id.btnConfigItemEditCategoryOrDish)!!
        val titleDishOrCategory = view.findViewById<TextView>(R.id.titleItemEditCategoryOrDish)!!
        val btnTrash =
            view.findViewById<AppCompatImageButton>(R.id.btnTrashItemEditCategoryOrDish)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapter = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_config_category_vs_dish, parent, false)
        return ItemViewHolder(adapter)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: DishDb =
            currentList[holder.adapterPosition]
        holder.btnConfig.setOnClickListener {
            stateViewDishModel.setStateDish(currentList[holder.adapterPosition])
            holder.view.findNavController()
                .navigate(R.id.action_configDishFragment_to_detailDishFragment)
        }
        holder.titleDishOrCategory.text = item.dishName

        holder.btnTrash.setOnClickListener {
            dishViewModel.deleteDish(item)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<DishDb>() {
            override fun areItemsTheSame(oldItem: DishDb, newItem: DishDb): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: DishDb, newItem: DishDb): Boolean {
                return oldItem.dishName == newItem.dishName
            }
        }
    }
}