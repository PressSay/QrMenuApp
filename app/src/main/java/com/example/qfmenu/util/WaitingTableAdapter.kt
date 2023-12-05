package com.example.qfmenu.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.R
import com.example.qfmenu.database.entity.TableDb
import com.example.qfmenu.repository.CustomerRepository
import com.example.qfmenu.viewmodels.CustomerViewModel
import com.example.qfmenu.viewmodels.SaveStateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class WaitingTableAdapter(
    private val customerViewModel: CustomerViewModel,
    private val saveStateViewModel: SaveStateViewModel,
    private val context: Context,
    private val customerRepository: CustomerRepository
) : ListAdapter<TableDb, WaitingTableAdapter.ItemViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<TableDb>() {
            override fun areItemsTheSame(oldItem: TableDb, newItem: TableDb): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: TableDb, newItem: TableDb): Boolean {
                return oldItem.tableId == newItem.tableId
            }
        }
    }

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val btnTable: AppCompatButton = view.findViewById(R.id.btnTableSelect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapter =
            LayoutInflater.from(parent.context).inflate(R.layout.item_table, parent, false)
        return ItemViewHolder(adapter)
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = currentList[position]
        "${item.tableId}.${item.status}".also { holder.btnTable.text = it }

        if (item.status == context.getString(R.string.lock)) {
            holder.btnTable.setBackgroundResource(R.drawable.button_err)
            holder.btnTable.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.green_error
                )
            )
        }

        holder.btnTable.setOnClickListener {
            /*don't forget setup for save then navigate*/
            if (!saveStateViewModel.stateIsTableUnClock) {

                CoroutineScope(Dispatchers.IO).launch {
                    customerViewModel.insertCustomer(
                        saveStateViewModel.stateCustomerDb.copy(name = "tableId: ${item.tableId}"), "Cash", "normal", 0, item.tableId, customerRepository
                    )
                }

                saveStateViewModel.stateDishesByCategories = mutableListOf()
                saveStateViewModel.stateCategoryPosition = 0

//                saveStateViewModel.stateCustomerWithSelectDishes.add(
//                    CustomerWithSelectDishes(
//                        saveStateViewModel.stateCustomerDb,
//                        saveStateViewModel.stateDishes,
//                        item
//                    )
//                )

                AlertDialog.Builder(context)
                    .setTitle("Add Bill")
                    .setMessage("successful manipulation") // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.ok,
                        DialogInterface.OnClickListener { dialog, which ->
                            val navController = holder.view.findNavController()
                            val startDestination = navController.graph.startDestination
                            val navOptions = NavOptions.Builder()
                                .setPopUpTo(startDestination, true)
                                .build()
                            navController.navigate(startDestination, null, navOptions)
                            saveStateViewModel.stateIsTableUnClock = true
                        }) // A null listener allows the button to dismiss the dialog and take no further action.
//                        .setNegativeButton(android.R.string.no, null)
//                        .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            } else {
                saveStateViewModel.stateTableDb = item
                holder.view.findNavController().navigate(R.id.action_tableOrderFragment_to_configTableFragment)
            }
        }
    }
}