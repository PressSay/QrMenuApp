package com.example.qfmenu.order

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.R
import com.example.qfmenu.database.dao.CustomerDishCrossRefDao
import com.example.qfmenu.database.entity.CustomerDb
import com.example.qfmenu.viewmodels.CustomerViewModel
import com.example.qfmenu.viewmodels.DishAmountDb
import com.example.qfmenu.viewmodels.SaveStateViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class OrderListAdapter(
    private val isOffline: Boolean,
    private val context: Context,
    private val saveStateViewModel: SaveStateViewModel,
    private val customerViewModel: CustomerViewModel,
    private val customerCrossRefDao: CustomerDishCrossRefDao
    ) : ListAdapter<CustomerDb, OrderListAdapter.ItemViewHolder>(DiffCallback) { // cần chuyển sang AdapterList


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

    class ItemViewHolder(val view : View) : RecyclerView.ViewHolder(view) {
        val btnCheckBox : AppCompatCheckBox = view.findViewById(R.id.btnSelectOrderPrepare)
        val btnConfig : AppCompatImageButton = view.findViewById(R.id.btnConfigOrderPrepare)
        val btnTrash : AppCompatImageButton = view.findViewById(R.id.btnTrashOrderPrepare)
        val statusCheck : RelativeLayout = view.findViewById(R.id.statusCheckBtnOrderPrepare)
        val iconOrder : ImageView = view.findViewById(R.id.icOrderPrepare)
        val name : TextView = view.findViewById(R.id.nameOrderPrepare)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapter = LayoutInflater.from(parent.context).inflate(R.layout.item_order_prepare, parent, false)
        return ItemViewHolder(adapter)
    }

    val stateCustomerWithSelectDishesToBillPos = mutableListOf<Int>()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = currentList[position]

        holder.statusCheck.setBackgroundResource(R.color.green_surface_variant)
        holder.iconOrder.setColorFilter(ContextCompat.getColor(context, R.color.green_on_surface_variant))
        holder.name.setTextColor(ContextCompat.getColor(context, R.color.green_on_surface_variant))


        GlobalScope.async {
            val it = async(Dispatchers.IO) {
                customerViewModel.getOrder(item.customerId)
            }.await()

            holder.name.text = it.tableCreatorId.toString()
        }


//        holder.name.text = item.tableDb.tableId.toString()




        holder.btnCheckBox.setOnClickListener {
            if (holder.btnCheckBox.isChecked) {
                stateCustomerWithSelectDishesToBillPos.add(position)
//                saveStateViewModel.stateCustomerWithSelectDishesToBill.add()
                holder.name.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green_primary
                    )
                )
                holder.statusCheck.setBackgroundResource(R.color.green_secondary_container)
                holder.iconOrder.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.green_primary
                    )
                )
            } else {
                var indexList = 0
                stateCustomerWithSelectDishesToBillPos.forEachIndexed { index, element ->
                    if (element == holder.adapterPosition) {
                        indexList = index
                    }
                }
                stateCustomerWithSelectDishesToBillPos.removeAt(indexList)
//                saveStateViewModel.stateCustomerWithSelectDishesToBill.remove(item) // xoa sai vi tri
                holder.statusCheck.setBackgroundResource(R.color.green_surface_variant)
                holder.iconOrder.setColorFilter(ContextCompat.getColor(context, R.color.green_on_surface_variant))
                holder.name.setTextColor(ContextCompat.getColor(context, R.color.green_on_surface_variant))
            }
        }

        holder.btnConfig.setOnClickListener {
            if (isOffline) {
                GlobalScope.async {
                    saveStateViewModel.setStateCustomer(currentList[position])
                    val dishesAmountDb = async(Dispatchers.IO) {
                        customerViewModel.getCustomerDishes(currentList[position].customerId)
                    }.await().map {
                        DishAmountDb(
                            dishDb = async(Dispatchers.IO) { customerCrossRefDao.getDish(it.dishId) }.await(),
                            amount = it.amount,
                        )
                    }
                    // thêm thể loại vào đây
//                    đây là danh sách xóa
                    saveStateViewModel.setStateDishesDb(
                        dishesAmountDb
                    )
                    saveStateViewModel.stateIsOfflineOrder = true
                    holder.view.findNavController().navigate(R.id.action_orderListUnconfirmedFragment_to_editConfirmDishFragment)
                }
            } else if (!this.isOffline) {
                holder.view.findNavController().navigate(R.id.action_editOnlineOrderFragment_to_editConfirmDishFragment)
            }
        }

        holder.btnTrash.setOnClickListener {
            customerViewModel.deleteCustomer(currentList[holder.adapterPosition])
            notifyItemRemoved(holder.adapterPosition)
        }
    }
}