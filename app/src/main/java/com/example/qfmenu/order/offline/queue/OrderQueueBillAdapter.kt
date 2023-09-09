package com.example.menumanager.order.offline.queue

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

class OrderQueueBillAdapter(
    private val context: Context,
    private var dataset: List<Dish>
) : RecyclerView.Adapter<OrderQueueBillAdapter.ItemViewHolder>() {
    fun getDataset(): List<Dish> = dataset
    fun setDataset(dataset: List<Dish>) {
        this.dataset = dataset
    }

    class ItemViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val img = view.findViewById<ImageView>(R.id.imgItemConfirmOrderView)!!
        private val descriptionItemConfirmOrderView = view.findViewById<LinearLayout>(R.id.descriptionItemConfirmOrderView) as ViewGroup
        val titleItem = descriptionItemConfirmOrderView.getChildAt(0) as TextView
        private val itemsCost = descriptionItemConfirmOrderView.getChildAt(2) as ViewGroup
        val cost = itemsCost.getChildAt(0) as TextView
        val amount = itemsCost.getChildAt(1) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapter = LayoutInflater.from(parent.context).inflate(R.layout.item_confirm_order_view, parent, false)
        return ItemViewHolder(adapter)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.img.setImageResource(R.drawable.img_image_4)
        holder.titleItem.text = item.title
        holder.cost.text = item.cost.toString()
        holder.amount.text = item.amount.toString()
    }
}