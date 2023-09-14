package com.example.qfmenu.menu.dish.confirm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.viewmodels.DishAmountDb
import com.example.qfmenu.R
import com.example.qfmenu.database.dao.CustomerDishCrossRefDao
import com.example.qfmenu.database.entity.CustomerDishCrossRef
import com.example.qfmenu.viewmodels.SaveStateViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class EditConfirmDishAdapter(
    private val saveStateViewModel: SaveStateViewModel,
    private val customerDishCrossRefDao: CustomerDishCrossRefDao,
    private var dataset: MutableList<DishAmountDb>
) : RecyclerView.Adapter<EditConfirmDishAdapter.ItemViewHolder>() {

    fun getDataset() = this.dataset

    fun setDataset(dataset: MutableList<DishAmountDb>) {
        this.dataset = dataset
    }

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imageViewConfirmOrderEdit)
        val title: TextView = view.findViewById(R.id.textViewTitleConfirmOrderEdit)
        val cost: TextView = view.findViewById(R.id.textViewCostConfirmOrderEdit)
        val amount: TextView = view.findViewById(R.id.textViewAmountConfirmOrderEdit)
        val btnTrash: AppCompatImageButton = view.findViewById(R.id.btnTrashConfirmOrderEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_confirm_order_edit, parent, false)
        return ItemViewHolder(adapterView)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[holder.adapterPosition]
        holder.image.setImageResource(R.drawable.img_image_4)
        holder.title.text = item.dishDb.dishName
        holder.cost.text = item.dishDb.cost.toString()
        holder.amount.text = item.amount.toString()
        holder.btnTrash.setOnClickListener {
            if (saveStateViewModel.stateIsOfflineOrder) {
                GlobalScope.async {
                    val customerDishCrossRef = CustomerDishCrossRef(
                        saveStateViewModel.stateCustomerDb.customerId,
                        item.dishDb.dishId,
                        item.amount,
                        0,
                    )
                    customerDishCrossRefDao.delete(customerDishCrossRef)
                }
            }
            saveStateViewModel.stateDishesByCategories.forEachIndexed { index1, it ->
                it.forEachIndexed { index2, dishAmountDb ->
                    if (dishAmountDb.dishDb.dishId == item.dishDb.dishId) {
                        saveStateViewModel.stateDishesByCategories[index1].removeAt(index2)
                    }
                }
            }
            dataset.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }
    }

}