package com.example.menumanager.table

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.R
import com.example.qfmenu.viewmodels.models.Table


class WaitingTableAdapter(private val dataset: List<Table>) : RecyclerView.Adapter<WaitingTableAdapter.ItemViewHolder>() {

    class ItemViewHolder(val view : View) : RecyclerView.ViewHolder(view) {
        val btnTable : AppCompatButton = view.findViewById(R.id.btnTableSelect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapter = LayoutInflater.from(parent.context).inflate(R.layout.item_table_select, parent, false)
        return ItemViewHolder(adapter)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.btnTable.text = item.name + "." + item.status
        holder.btnTable.setOnClickListener {
            /*don't forget setup for save then navigate*/

            val navController = holder.view.findNavController()
            val startDestination = navController.graph.startDestination
            val navOptions = NavOptions.Builder()
                .setPopUpTo(startDestination, true)
                .build()
            navController.navigate(startDestination, null, navOptions)
        }
    }
}