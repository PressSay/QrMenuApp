package com.example.qfmenu.order

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
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
import com.example.qfmenu.viewmodels.CustomerOrderQueue
import com.example.qfmenu.viewmodels.CustomerViewModel
import com.example.qfmenu.viewmodels.DishAmountDb
import com.example.qfmenu.viewmodels.SaveStateViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class OrderListUnconfirmedAdapter(
    private val isOffline: Boolean,
    private val context: Context,
    private val saveStateViewModel: SaveStateViewModel,
    private val customerViewModel: CustomerViewModel,
    private val customerCrossRefDao: CustomerDishCrossRefDao
) : ListAdapter<CustomerDb, OrderListUnconfirmedAdapter.ItemViewHolder>(DiffCallback) { // cần chuyển sang AdapterList


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
        val btnCheckBox: AppCompatCheckBox = view.findViewById(R.id.btnSelectOrderPrepare)
        val btnConfig: AppCompatImageButton = view.findViewById(R.id.btnConfigOrderPrepare)
        val btnTrash: AppCompatImageButton = view.findViewById(R.id.btnTrashOrderPrepare)
        val statusCheck: RelativeLayout = view.findViewById(R.id.statusCheckBtnOrderPrepare)
        val iconOrder: ImageView = view.findViewById(R.id.icOrderPrepare)
        val name: TextView = view.findViewById(R.id.nameOrderPrepare)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapter =
            LayoutInflater.from(parent.context).inflate(R.layout.item_order_prepare, parent, false)
        return ItemViewHolder(adapter)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun addCustomerOrder(position: Int) {
        GlobalScope.async {
            if (saveStateViewModel.stateCustomerOrderQueuesPos.size < currentList.size) {
                for (i in 0..currentList.size) {
                    saveStateViewModel.stateCustomerOrderQueuesPos.add(-1)
                }
            }
            saveStateViewModel.stateCustomerOrderQueuesPos[position] = position



            val customerDbCurrent =
                async { customerViewModel.getCustomer(currentList[position].customerId) }.await()
            val dishesAmountDb = async {
                customerViewModel.getCustomerDishes(customerDbCurrent.customerId)
            }.await().map {
                DishAmountDb(
                    async { customerCrossRefDao.getDish(it.dishId) }.await(),
                    it.amount
                )
            }
//                    val tableId = async { customerViewModel.getOrder(currentList[position].customerId) }.await().tableCreatorId

            saveStateViewModel.stateCustomerOrderQueues.add(
                CustomerOrderQueue(
                    customerDbCurrent,
//                        TableDb(tableId, "free")
                    dishesAmountDb = dishesAmountDb
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @OptIn(DelicateCoroutinesApi::class)
    private fun removeCustomerOrder(position: Int) {
        GlobalScope.async {
            if (saveStateViewModel.stateCustomerOrderQueuesPos.size > position) {
                saveStateViewModel.stateCustomerOrderQueuesPos[position] = -1
            }

            val customerDbCurrent =
                async { customerViewModel.getCustomer(currentList[position].customerId) }.await()
//                    val tableId = async { customerViewModel.getOrder(currentList[position].customerId) }.await().tableCreatorId

            saveStateViewModel.stateCustomerOrderQueues.removeIf { it.customerDb.customerId == customerDbCurrent.customerId }
        }
    }

    private fun colorCheck(holder: ItemViewHolder, position: Int) {
        if (holder.btnCheckBox.isChecked) {
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
            holder.statusCheck.setBackgroundResource(R.color.green_surface_variant)
            holder.iconOrder.setColorFilter(
                ContextCompat.getColor(
                    context,
                    R.color.green_on_surface_variant
                )
            )
            holder.name.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.green_on_surface_variant
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = currentList[position]

        if (position < saveStateViewModel.stateCustomerOrderQueuesPos.size) {

            if (saveStateViewModel.stateCustomerOrderQueuesPos[position] > -1) {
                holder.btnCheckBox.isChecked = true
                addCustomerOrder(position)
            }
        }

        colorCheck(holder, position)

        GlobalScope.async {
            val it = async(Dispatchers.IO) {
                customerViewModel.getOrder(item.customerId)
            }.await()

            holder.name.text = it.tableCreatorId.toString()
        }

//        holder.name.text = item.tableDb.tableId.toString()

        holder.btnCheckBox.setOnClickListener {
            if (holder.btnCheckBox.isChecked) {
                addCustomerOrder(position)
            } else {
                removeCustomerOrder(position)
            }
            colorCheck(holder, position)
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
                    saveStateViewModel.setStateDishesDb(
                        dishesAmountDb
                    )
                    saveStateViewModel.stateIsOfflineOrder = true
                    holder.view.findNavController()
                        .navigate(R.id.action_orderListUnconfirmedFragment_to_editConfirmDishFragment)
                }
            } else if (!this.isOffline) {
                holder.view.findNavController()
                    .navigate(R.id.action_editOnlineOrderFragment_to_editConfirmDishFragment)
            }
        }

        holder.btnTrash.setOnClickListener {
            customerViewModel.deleteCustomer(currentList[holder.adapterPosition])
            notifyItemRemoved(holder.adapterPosition)
        }


    }


}