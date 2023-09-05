package com.example.menumanager.order.offline.queue

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.R

class OrderQueueAdapter(
    private val onCLick: (Array<Int>) -> Unit,
    private val context: Context,
    private val dataset: List<OrderQueue>
) : RecyclerView.Adapter<OrderQueueAdapter.ItemViewHolder>() {

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val btnQueue = view.findViewById<AppCompatImageButton>(R.id.itemOrderQueue)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapter =
            LayoutInflater.from(parent.context).inflate(R.layout.item_order_queue, parent, false)
        return ItemViewHolder(adapter)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    val getDataset get() = dataset

    private var positionSelected = -1

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        holder.btnQueue.setBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.green_on_primary
            )
        )
        holder.btnQueue.setColorFilter(ContextCompat.getColor(context, R.color.green_secondary))
        if (dataset[position].isSelected) {
            this.positionSelected = holder.adapterPosition
            holder.btnQueue.setColorFilter(ContextCompat.getColor(context, R.color.green_primary))
        }

        holder.btnQueue.setOnClickListener {
            if (position != positionSelected) {
                dataset[position].isSelected = !dataset[position].isSelected
                dataset[positionSelected].isSelected = !dataset[positionSelected].isSelected
                notifyItemChanged(position)
                notifyItemChanged(positionSelected)
                onCLick(arrayOf(position, positionSelected))
            }
        }
    }
}