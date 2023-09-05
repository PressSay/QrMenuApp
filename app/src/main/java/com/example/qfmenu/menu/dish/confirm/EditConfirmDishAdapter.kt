package com.example.menumanager.menu.dish.confirm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.viewmodels.models.Dish
import com.example.qfmenu.R

class EditConfirmDishAdapter(
    private val dataset: MutableList<Dish>
) : RecyclerView.Adapter<EditConfirmDishAdapter.ItemViewHolder>() {

    fun getDataset() = this.dataset

    class ItemViewHolder(val view : View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imageViewConfirmOrderEdit)
        val title: TextView = view.findViewById(R.id.textViewTitleConfirmOrderEdit)
        val cost: TextView = view.findViewById(R.id.textViewCostConfirmOrderEdit)
        val amount: TextView = view.findViewById(R.id.textViewAmountConfirmOrderEdit)
        val btnTrash : AppCompatImageButton = view.findViewById(R.id.btnTrashConfirmOrderEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterView = LayoutInflater.from(parent.context).inflate(R.layout.item_confirm_order_edit, parent, false)
        return ItemViewHolder(adapterView)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[holder.adapterPosition]
        holder.image.setImageResource(item.imgResourceId)
        holder.title.text = item.title
        holder.cost.text = item.cost
        holder.amount.text = item.amount.toString()
        holder.btnTrash.setOnClickListener {
            dataset.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }
    }

}