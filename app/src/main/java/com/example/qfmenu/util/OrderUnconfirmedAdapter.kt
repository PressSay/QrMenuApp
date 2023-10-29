package com.example.qfmenu.util

import android.content.Context
import android.os.Build
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
import com.example.qfmenu.database.dao.CustomerDishDao
import com.example.qfmenu.database.entity.CustomerDb
import com.example.qfmenu.viewmodels.CustomerOrderQueue
import com.example.qfmenu.viewmodels.CustomerViewModel
import com.example.qfmenu.viewmodels.DishAmountDb
import com.example.qfmenu.viewmodels.SaveStateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class OrderUnconfirmedAdapter(
    private val isOffline: Boolean,
    private val context: Context,
    private val saveStateViewModel: SaveStateViewModel,
    private val customerViewModel: CustomerViewModel,
    private val customerCrossRefDao: CustomerDishDao
) : ListAdapter<CustomerDb, OrderUnconfirmedAdapter.ItemViewHolder>(DiffCallback) { // cần chuyển sang AdapterList


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
//                    val tableId = async { customerViewModel.getOrder(currentList[position].customerId) }.await().tableId

            saveStateViewModel.stateCustomerOrderQueues.add(
                CustomerOrderQueue(
                    customerDbCurrent,
//                        TableDb(tableId, "free")
                    dishesAmountDb = dishesAmountDb
                )
            )
        }
    }


    private fun removeCustomerOrder(position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            if (saveStateViewModel.stateCustomerOrderQueuesPos.size > position) {
                saveStateViewModel.stateCustomerOrderQueuesPos[position] = -1
            }

            val customerDbCurrent =
                async { customerViewModel.getCustomer(currentList[position].customerId) }.await()
//                    val tableId = async { customerViewModel.getOrder(currentList[position].customerId) }.await().tableId

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                saveStateViewModel.stateCustomerOrderQueues.removeIf { it.customerDb.customerId == customerDbCurrent.customerId }
            } else {
                for (i in 0..<saveStateViewModel.stateCustomerOrderQueues.size) {
                    if (saveStateViewModel.stateCustomerOrderQueues[i].customerDb.customerId == customerDbCurrent.customerId) {
                        saveStateViewModel.stateCustomerOrderQueues.removeAt(i)
                        break
                    }
                }
            }
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

        if ( position < saveStateViewModel.stateCustomerOrderQueuesPos.size ) {

            if (saveStateViewModel.stateCustomerOrderQueuesPos[position] > -1) {
                holder.btnCheckBox.isChecked = true
                addCustomerOrder(position)
            }
        }

        colorCheck(holder, position)

        CoroutineScope(Dispatchers.Main).launch {
            val it = async(Dispatchers.IO) {
                customerViewModel.getOrder(item.customerId)
            }.await()

            holder.name.text = it.tableId.toString()
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
            CoroutineScope(Dispatchers.Main).launch {
                saveStateViewModel.stateCustomerOrderQueues = mutableListOf()
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
                saveStateViewModel.stateIsOffOnOrder = true
                holder.view.findNavController()
                    .navigate(R.id.action_orderUnconfirmedFragment_to_confirmDishFragment)
            }
        }

        holder.btnTrash.setOnClickListener {
            customerViewModel.deleteCustomer(currentList[holder.adapterPosition])
            notifyItemRemoved(holder.adapterPosition)
        }


    }


}