package com.example.qfmenu.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.example.qfmenu.R
import com.example.qfmenu.database.entity.DishDb
import com.example.qfmenu.viewmodels.SaveStateViewModel

class ReviewAdapter(
    private val context: Context,
    private val saveStateViewModel: SaveStateViewModel
) : ListAdapter<DishDb, ReviewAdapter.ItemViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<DishDb>() {
            override fun areItemsTheSame(oldItem: DishDb, newItem: DishDb): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: DishDb, newItem: DishDb): Boolean {
                return oldItem.dishId == newItem.dishId
            }
        }
    }

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val parentView = view.findViewById<LinearLayout>(R.id.itemConfirmOrderReview) as ViewGroup
        val imgView = parentView.getChildAt(1) as ImageView
        private val contentParentView = parentView.getChildAt(0) as ViewGroup
        val titleItem = (contentParentView.getChildAt(0) as ViewGroup).getChildAt(0) as TextView
        private val subContentParentView = contentParentView.getChildAt(2) as ViewGroup
        val cost = subContentParentView.getChildAt(0) as TextView
        val amount = subContentParentView.getChildAt(1) as TextView
        private val parentReview = subContentParentView.getChildAt(2) as ViewGroup
        val starImg = parentReview.getChildAt(0) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapter = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_confirm_order_review, parent, false)
        return ItemViewHolder(adapter)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = currentList[position]
        holder.imgView.setImageResource(R.drawable.img_image_4)
        holder.titleItem.text = item.name
        holder.cost.text = item.cost.toString()
        holder.amount.visibility = View.GONE
        holder.parentView.setOnClickListener {
            saveStateViewModel.setStateDish(currentList[position])
            holder.view.findNavController()
                .navigate(R.id.action_reviewFragment_to_reviewDetailFragment)
        }
    }
}