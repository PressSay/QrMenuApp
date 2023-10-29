package com.example.qfmenu.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.viewmodels.DishAmountDb
import com.example.qfmenu.R
import com.example.qfmenu.database.dao.CustomerDishDao
import com.example.qfmenu.database.entity.CustomerDishDb
import com.example.qfmenu.viewmodels.SaveStateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class ConfirmDishAdapter(
    private val context: Context,
    private var total: Int,
    private val dishConfirmTotal: TextView,
    private val saveStateViewModel: SaveStateViewModel,
    private val customerDishDao: CustomerDishDao,
    private var dataset: MutableList<DishAmountDb>
) : RecyclerView.Adapter<ConfirmDishAdapter.ItemViewHolder>() {

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
            .inflate(R.layout.item_confirm_order_config, parent, false)
        return ItemViewHolder(adapterView)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[holder.adapterPosition]
        holder.image.setImageResource(R.drawable.img_image_4)
        holder.title.text = item.dishDb.name
        holder.cost.text = item.dishDb.cost.toString()
        holder.amount.text = item.amount.toString()
        holder.btnTrash.setOnClickListener {
            if (saveStateViewModel.stateIsOffOnOrder) {
                CoroutineScope(Dispatchers.IO).launch {
                    val customerDishDb = CustomerDishDb(
                        saveStateViewModel.stateCustomerDb.customerId,
                        item.dishDb.dishId,
                        item.amount,
                        0,
                    )
                    customerDishDao.delete(customerDishDb)
                }
            }
            saveStateViewModel.stateDishesByCategories.forEachIndexed { index1, it ->
                it.forEachIndexed { index2, dishAmountDb ->
                    if (dishAmountDb.dishDb.dishId == item.dishDb.dishId) {
                        saveStateViewModel.stateDishesByCategories[index1].removeAt(index2)
                    }
                }
            }

            total -= (item.dishDb.cost * item.amount.toInt())
            val totalCurrency = NumberFormat.getNumberInstance(Locale.US).format(total)
            dishConfirmTotal.text = context.getString(R.string.total, "$totalCurrency\$")

            dataset.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }
    }

}