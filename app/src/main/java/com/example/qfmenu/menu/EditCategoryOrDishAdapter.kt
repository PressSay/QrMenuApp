package com.example.menumanager.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.viewmodels.models.Dish
import com.example.qfmenu.R
import com.example.qfmenu.viewmodels.models.Category

class EditCategoryOrDishAdapter<T>(
    private val isCategory: Boolean,
    private val context: Context,
    private val dataset: MutableList<T>
) : RecyclerView.Adapter<EditCategoryOrDishAdapter.ItemViewHolder>() {
    class ItemViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val btnConfig = view.findViewById<AppCompatImageButton>(R.id.btnConfigItemEditCategoryOrDish)!!
        val titleDishOrCategory = view.findViewById<TextView>(R.id.titleItemEditCategoryOrDish)!!
        val btnTrash = view.findViewById<AppCompatImageButton>(R.id.btnTrashItemEditCategoryOrDish)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapter = LayoutInflater.from(parent.context).inflate(R.layout.item_edit_category_or_dish, parent, false)
        return ItemViewHolder(adapter)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {


        if (isCategory) {
            val item: Category =
                dataset[holder.adapterPosition] as Category
            holder.btnConfig.setOnClickListener {
                holder.view.findNavController().navigate(R.id.action_editCreateCategoryFragment_to_editCreateDishFragment)
            }
            holder.titleDishOrCategory.text = item.title
        } else {
            val item: Dish = dataset[holder.adapterPosition] as Dish
            holder.btnConfig.setOnClickListener {
                holder.view.findNavController().navigate(R.id.action_editCreateDishFragment_to_editCreateDetailDishFragment)
            }
            holder.titleDishOrCategory.text = item.title
        }

        holder.btnTrash.setOnClickListener {
            dataset.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }

    }
}