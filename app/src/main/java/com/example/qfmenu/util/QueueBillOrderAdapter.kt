package com.example.qfmenu.util

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.R
import com.example.qfmenu.network.NetworkRetrofit
import com.example.qfmenu.viewmodels.DishAmountDb
import com.squareup.picasso.Picasso
import java.io.IOException
import java.text.NumberFormat
import java.util.Locale

class QueueBillOrderAdapter(
    private val context: Context,
    private var dataset: List<DishAmountDb>
) : RecyclerView.Adapter<QueueBillOrderAdapter.ItemViewHolder>() {
    fun getDataset() = dataset
    fun setDataset(dataset: List<DishAmountDb>) {
        this.dataset = dataset
    }

    class ItemViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val img = view.findViewById<ImageView>(R.id.imgItemConfirmOrderView)!!
        private val descriptionItemConfirmOrderView = view.findViewById<LinearLayout>(R.id.descriptionItemConfirmOrderView) as ViewGroup
        val titleItem = (descriptionItemConfirmOrderView.getChildAt(0) as ViewGroup).getChildAt(0) as TextView
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
        try {
            Picasso.get().load("${NetworkRetrofit.BASE_URL}/${item.dishDb.image}")
                .transform(RoundedTransformation(48F, 0))
                .fit().centerCrop().into(holder.img)
        } catch (networkError: IOException) {
            Log.d("NoInternet", true.toString())
            holder.img.setImageResource(R.drawable.img_image_4)
        }
        holder.titleItem.text = item.dishDb.name
        val formattedAmount = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(item.dishDb.cost)
        holder.cost.text = formattedAmount
        holder.amount.text = item.amount.toString()
    }
}