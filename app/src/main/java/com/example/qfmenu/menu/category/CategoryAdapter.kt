package com.example.menumanager.menu.category

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.R
import com.example.qfmenu.viewmodels.models.Category

class CategoryAdapter(
    private val context: Context,
    private val dataset: List<Category>
): RecyclerView.Adapter<CategoryAdapter.ItemViewHolder>() {
    class ItemViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val btnCategory = view.findViewById<AppCompatButton>(R.id.btnCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_category, parent, false)
        return ItemViewHolder(adapterView)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.btnCategory.text = item.title
        holder.btnCategory.setOnClickListener {

        }
    }
}