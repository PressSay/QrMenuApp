package com.example.menumanager.review

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.R
import com.example.qfmenu.viewmodels.models.Review


class ReviewStoreOrDishAdapter(
    private val isStore: Boolean,
    private val context: Context,
    private val dataset: MutableList<Review>
) : RecyclerView.Adapter<ReviewStoreOrDishAdapter.ItemViewHolder>() {
    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val items = view.findViewById<LinearLayout>(R.id.itemReviewPersonAdmin) as ViewGroup
        val description = items.getChildAt(2) as TextView
        private val subItems = items.getChildAt(0) as ViewGroup
        val imgPerson = subItems.getChildAt(0) as ImageView
        val btnTrash = subItems.getChildAt(2) as AppCompatImageButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapter = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review_person_admin, parent, false)
        return ItemViewHolder(adapter)
    }

    fun getDatset(): MutableList<Review> = dataset

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[holder.adapterPosition]
        holder.items.setOnClickListener {
            if (isStore) {
                holder.view.findNavController()
                    .navigate(R.id.action_reviewStoreListFragment_to_storeReivewCommentFragment)
            } else {
                holder.view.findNavController()
                    .navigate(R.id.action_reviewListDetailAdminFragment_to_dishReviewFragment)
            }
        }
        holder.btnTrash.setOnClickListener {
            dataset.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }
        holder.description.text = item.description
    }
}