package com.example.qfmenu.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.R
import com.example.qfmenu.database.dao.ReviewDao
import com.example.qfmenu.database.entity.ReviewCustomerCrossRef
import com.example.qfmenu.database.entity.ReviewDb
import com.example.qfmenu.database.entity.ReviewDishCrossRef
import com.example.qfmenu.viewmodels.SaveStateViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ReviewStoreOrDishAdapter(
    private val isStore: Boolean,
    private val reviewDao: ReviewDao,
    private val saveStateViewModel: SaveStateViewModel,
    private val context: Context,
) : ListAdapter<ReviewDb, ReviewStoreOrDishAdapter.ItemViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ReviewDb>() {
            override fun areItemsTheSame(oldItem: ReviewDb, newItem: ReviewDb): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: ReviewDb, newItem: ReviewDb): Boolean {
                return oldItem.reviewId == newItem.reviewId
            }
        }
    }

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


    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = currentList[holder.adapterPosition]
        holder.items.setOnClickListener {

        }
        holder.btnTrash.setOnClickListener {
            GlobalScope.launch {
                val reviewDb = currentList[position]
                if (!isStore) {
                    val dishDb = saveStateViewModel.stateDishDb
                    val reviewDishCrossRef = ReviewDishCrossRef(
                        dishId = dishDb.dishId,
                        reviewId = reviewDb.reviewId
                    )
                    reviewDao.deleteReviewDishCrossRef(reviewDishCrossRef)
                } else {
                    val customerDb = saveStateViewModel.stateCustomerDb
                    val reviewCustomerCrossRef = ReviewCustomerCrossRef(
                        reviewDb.reviewId,
                        customerDb.customerId
                    )
                    reviewDao.deleteReviewCustomerCrossRef(reviewCustomerCrossRef)
                }
                reviewDao.delete(reviewDb)
            }
//            dataset.removeAt(holder.adapterPosition)
//            notifyItemRemoved(holder.adapterPosition)
        }
        holder.description.text = item.description
    }
}