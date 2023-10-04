package com.example.qfmenu.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.R
import com.example.qfmenu.database.dao.InvestmentDao
import com.example.qfmenu.database.entity.InvestmentDb
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


class EditCreateInvestmentAdapter(
    private val context: Context,
    private val investmentDao: InvestmentDao
) : ListAdapter<InvestmentDb, EditCreateInvestmentAdapter.ItemViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<InvestmentDb>() {
            override fun areItemsTheSame(oldItem: InvestmentDb, newItem: InvestmentDb): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: InvestmentDb, newItem: InvestmentDb): Boolean {
                return oldItem.investmentName == newItem.investmentName
            }
        }
    }

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

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = currentList[holder.adapterPosition]
        holder.cost.text = item.cost.toString()
        holder.titleItem.text = item.investmentName

        if (item.isDropdown) {
            holder.imgShowCosts.setImageResource(R.drawable.ic_expand_less)
            holder.itemsOfCost.visibility = View.VISIBLE
        } else {
            holder.imgShowCosts.setImageResource(R.drawable.ic_expand)
            holder.itemsOfCost.visibility = View.GONE
        }
        holder.btnShowCost.setOnClickListener {
            currentList[holder.adapterPosition].isDropdown = !item.isDropdown
            notifyItemChanged(holder.adapterPosition)
        }
        holder.btnTrash.setOnClickListener {
            GlobalScope.async {
                investmentDao.delete(item)
            }
//            currentList.removeAt(holder.adapterPosition)
//            notifyItemRemoved(holder.adapterPosition)
        }
    }
}