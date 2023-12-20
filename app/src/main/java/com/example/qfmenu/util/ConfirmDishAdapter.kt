package com.example.qfmenu.util

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.R
import com.example.qfmenu.database.dao.CustomerDishDao
import com.example.qfmenu.database.entity.CustomerDishDb
import com.example.qfmenu.network.NetworkRetrofit
import com.example.qfmenu.viewmodels.DishAmountDb
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
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
        try {
            Picasso.get().load("${NetworkRetrofit.BASE_URL}/${item.dishDb.image}")
                .transform(RoundedTransformation(48F, 0))
                .fit().centerCrop().into(holder.image)
        } catch (networkError: IOException) {
            Log.d("NoInternet", true.toString())
            holder.image.setImageResource(R.drawable.img_image_4)
        }

        holder.title.text = item.dishDb.name
        val costVND = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(item.dishDb.cost)
        holder.cost.text = costVND
        holder.amount.text = item.amount.toString()
        holder.btnTrash.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                if (saveStateViewModel.stateIsOffOnOrder) {

                    val customerDishDb = CustomerDishDb(
                        customerId = saveStateViewModel.stateCustomerDb.customerId,
                        dishId = item.dishDb.dishId,
                        amount = item.amount,
                        promotion = 0,
                    )
                    customerDishDao.delete(customerDishDb)
                }

                saveStateViewModel.stateDishesByCategories.forEach { ele1 ->
                        ele1.value.remove(item)
                }
            }

            total -= (item.dishDb.cost * item.amount.toInt())
            val totalCurrency = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(total)
            dishConfirmTotal.text = context.getString(R.string.total, totalCurrency)

            dataset.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }
    }

}