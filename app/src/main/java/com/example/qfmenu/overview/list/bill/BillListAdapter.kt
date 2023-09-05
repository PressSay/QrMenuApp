package com.example.menumanager.overview.list.bill

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.viewmodels.models.Customer
import com.example.qfmenu.R

class BillListAdapter(
    private val context: Context,
    private val dataset: MutableList<Customer>
) : RecyclerView.Adapter<BillListAdapter.ItemViewHolder>() {
    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val parentView = view.findViewById<LinearLayout>(R.id.itemOverviewBill) as ViewGroup
        val btnDetail = parentView.getChildAt(0) as AppCompatButton
        val titleView = parentView.getChildAt(2) as TextView
        val btnTrash = parentView.getChildAt(4) as AppCompatImageButton
    }

    fun getDataset() : MutableList<Customer> {
        return dataset
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapter = LayoutInflater.from(parent.context).inflate(R.layout.item_overview_bill, parent, false)
        return ItemViewHolder(adapter)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.btnDetail.setOnClickListener {
            holder.view.findNavController().navigate(R.id.action_billListFragment_to_billListDetailFragment)
        }
        holder.titleView.text = item.id.toString()
        holder.btnTrash.setOnClickListener {
            dataset.removeAt(holder.adapterPosition)
            notifyItemRemoved(position)
        }
    }
}