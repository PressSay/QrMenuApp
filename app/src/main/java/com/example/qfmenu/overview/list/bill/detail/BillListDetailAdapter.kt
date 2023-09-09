package com.example.menumanager.overview.list.bill.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.viewmodels.models.Dish
import com.example.qfmenu.R

class BillListDetailAdapter(
    private val context: Context,
    private val dataset: List<Dish>
) : RecyclerView.Adapter<BillListDetailAdapter.ItemViewHolder>() {
    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val parentView = view.findViewById<LinearLayout>(R.id.itemConfirmOrderReview) as ViewGroup
        val imgView = parentView.getChildAt(0) as ImageView
        private val contentParentView = parentView.getChildAt(1) as ViewGroup
        val titleItem = contentParentView.getChildAt(0) as TextView
        private val subContentParentView = contentParentView.getChildAt(2) as ViewGroup
        val cost = subContentParentView.getChildAt(0) as TextView
        val amount = subContentParentView.getChildAt(1) as TextView
        private val parentReview = subContentParentView.getChildAt(2) as ViewGroup
        val starImg = parentReview.getChildAt(0) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapter = LayoutInflater.from(parent.context).inflate(R.layout.item_confirm_order_review, parent, false)
        return ItemViewHolder(adapter)
    }

    override fun getItemCount(): Int {
        return  dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.imgView.setImageResource(R.drawable.img_image_4)
        holder.titleItem.text = item.title
        holder.cost.text = item.cost.toString()
        holder.amount.text = item.amount.toString()
        if (!item.isReview) {
            holder.starImg.visibility = View.GONE
        }
    }

}