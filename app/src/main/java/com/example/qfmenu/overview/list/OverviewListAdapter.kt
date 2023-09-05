package com.example.menumanager.overview.list

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.R

class OverviewListAdapter(
    private val context: Context,
    private val dataset: MutableList<Overview>
) : RecyclerView.Adapter<OverviewListAdapter.ItemViewHolder>() {
    fun getDataset(): MutableList<Overview> {
        return dataset
    }

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val parentView = view.findViewById<LinearLayout>(R.id.itemOverview) as ViewGroup
        val btnBillList = parentView.getChildAt(1) as AppCompatImageButton
        val titleOverView = parentView.getChildAt(3) as TextView
        val btnTrash = parentView.getChildAt(5) as AppCompatImageButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapter = LayoutInflater.from(parent.context).inflate(R.layout.item_overview, parent, false)
        return ItemViewHolder(adapter)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.btnBillList.setOnClickListener {
            holder.view.findNavController().navigate(R.id.action_overviewListFragment_to_billListFragment)
        }
        holder.titleOverView.text = item.id
        holder.btnTrash.setOnClickListener {
            dataset.removeAt(holder.adapterPosition)
            notifyItemRemoved(position)
        }
    }
}