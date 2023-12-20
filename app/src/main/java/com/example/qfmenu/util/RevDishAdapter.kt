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
import com.example.qfmenu.database.entity.CustomerDishDb
import com.example.qfmenu.database.entity.ReviewDishDb
import com.example.qfmenu.network.entity.RevDish
import com.example.qfmenu.repository.ReviewRepository
import com.example.qfmenu.viewmodels.SaveStateViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar

class RevDishAdapter(
    private val isStore: Boolean,
    private val reviewDao: ReviewDao,
    private val saveStateViewModel: SaveStateViewModel,
    private val reviewRepository: ReviewRepository,
    private val context: Context,
) : ListAdapter<ReviewDishDb, RevDishAdapter.ItemViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ReviewDishDb>() {
            override fun areItemsTheSame(oldItem: ReviewDishDb, newItem: ReviewDishDb): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: ReviewDishDb, newItem: ReviewDishDb): Boolean {
                return oldItem.dishId == newItem.dishId && oldItem.customerId == newItem.customerId
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
            .inflate(R.layout.item_review_person, parent, false)
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
                try {
                    reviewDao.deleteRevDish(reviewDb.dishId, reviewDb.customerId)
                    reviewRepository.deleteRevDishNet(RevDish(reviewDb.dishId, reviewDb.customerId, reviewDb.description!!, reviewDb.isThumbUp!!))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
//            dataset.removeAt(holder.adapterPosition)
//            notifyItemRemoved(holder.adapterPosition)
        }
        holder.description.text = item.description
    }
}