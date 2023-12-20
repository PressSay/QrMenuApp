package com.example.qfmenu.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.R
import com.example.qfmenu.database.entity.CustomerDb
import com.example.qfmenu.repository.CustomerRepository
import com.example.qfmenu.viewmodels.CustomerOrderQueue
import com.example.qfmenu.viewmodels.CustomerViewModel
import com.example.qfmenu.viewmodels.DishAmountDb
import com.example.qfmenu.viewmodels.SaveStateViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class BillAdapter(
    private val context: Context,
    private val saveStateViewModel: SaveStateViewModel,
    private val customerViewModel: CustomerViewModel,
    private val customerRepository: CustomerRepository
) : ListAdapter<CustomerDb, BillAdapter.ItemViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<CustomerDb>() {
            override fun areItemsTheSame(oldItem: CustomerDb, newItem: CustomerDb): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: CustomerDb, newItem: CustomerDb): Boolean {
                return oldItem.customerId == newItem.customerId
            }
        }
    }

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val parentView = view.findViewById<LinearLayout>(R.id.itemOverviewBill) as ViewGroup
        val btnDetail = parentView.getChildAt(0) as AppCompatButton
        val titleView = parentView.getChildAt(2) as TextView
        val btnTrash = parentView.getChildAt(4) as AppCompatImageButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapter = LayoutInflater.from(parent.context).inflate(R.layout.item_overview_bill, parent, false)
        return ItemViewHolder(adapter)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = currentList[position]
        holder.btnDetail.setOnClickListener {
            GlobalScope.async {

                val customerDishCrossRefDao =
                    customerViewModel.getCustomerDishCrossRefDao()

                val dishesAmountDb = async(Dispatchers.IO) {
                    customerDishCrossRefDao.getListByCustomerId(item.customerId)
                        .map {
                            DishAmountDb(
                                async(Dispatchers.IO) { customerDishCrossRefDao.getDish(it.dishId) }.await(),
                                it.amount
                            )
                        }
                }.await()

                saveStateViewModel.stateCustomerOrderQueue = CustomerOrderQueue(
                    customerDb = item,
                    dishesAmountDb
                )

                holder.view.findNavController().navigate(R.id.action_billFragment_to_billDetailFragment)
            }


        }
        holder.titleView.text = item.customerId.toString()
        holder.btnTrash.setOnClickListener {
            customerViewModel.deleteCustomer(item, customerRepository)
//            dataset.removeAt(holder.adapterPosition)
//            notifyItemRemoved(position)
        }
    }
}