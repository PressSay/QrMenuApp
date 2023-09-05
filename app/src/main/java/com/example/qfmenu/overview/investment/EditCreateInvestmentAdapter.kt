package com.example.menumanager.overview.investment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.R
import com.example.qfmenu.viewmodels.models.Investment

class EditCreateInvestmentAdapter(
    val context: Context,
    val dataset: MutableList<Investment>
) : RecyclerView.Adapter<EditCreateInvestmentAdapter.ItemViewHolder>() {
    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val itemsOfTitle = view.findViewById<LinearLayout>(R.id.itemsOfTitleInvestment) as ViewGroup
        private val relativeItemsOfTitle = itemsOfTitle.getChildAt(0) as ViewGroup
        private val linearRelativeItemsOfTitle = relativeItemsOfTitle.getChildAt(1) as ViewGroup

        val btnTrash = itemsOfTitle.getChildAt(2) as ImageButton
        val btnShowCost = relativeItemsOfTitle.getChildAt(0) as AppCompatButton
        val imgShowCosts = linearRelativeItemsOfTitle.getChildAt(0) as ImageView
        val titleItem = linearRelativeItemsOfTitle.getChildAt(2) as TextView

        val itemsOfCost = view.findViewById<LinearLayout>(R.id.itemsOfCostInvestment) as ViewGroup

        val cost = itemsOfCost.getChildAt(2) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapter = LayoutInflater.from(parent.context).inflate(R.layout.item_investment, parent, false)
        return ItemViewHolder(adapter)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[holder.adapterPosition]
        holder.cost.text = item.cost.toString()
        holder.titleItem.text = item.name

        if (item.isDropdown) {
            holder.imgShowCosts.setImageResource(R.drawable.ic_expand_less)
            holder.itemsOfCost.visibility = View.VISIBLE
        } else {
            holder.imgShowCosts.setImageResource(R.drawable.ic_expand)
            holder.itemsOfCost.visibility = View.GONE
        }
        holder.btnShowCost.setOnClickListener {
            dataset[holder.adapterPosition].isDropdown = !item.isDropdown
            notifyItemChanged(holder.adapterPosition)
        }
        holder.btnTrash.setOnClickListener {
            dataset.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }
    }
}