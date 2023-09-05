package com.example.menumanager.order

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
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.R
import com.example.qfmenu.viewmodels.models.Customer

class OrderListAdapter(
    private val isOffline: Boolean,
    private val context: Context,
    private val dataset: MutableList<Customer>
) : RecyclerView.Adapter<OrderListAdapter.ItemViewHolder>() {

    fun getDataset() = this.dataset

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

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        holder.statusCheck.setBackgroundResource(R.color.green_surface_variant)
        holder.iconOrder.setColorFilter(ContextCompat.getColor(context, R.color.green_on_surface_variant))
        holder.name.setTextColor(ContextCompat.getColor(context, R.color.green_on_surface_variant))
        holder.name.text = item.table.name

        holder.btnCheckBox.setOnClickListener {
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
                holder.iconOrder.setColorFilter(ContextCompat.getColor(context, R.color.green_on_surface_variant))
                holder.name.setTextColor(ContextCompat.getColor(context, R.color.green_on_surface_variant))
            }
        }

        holder.btnConfig.setOnClickListener {
            if (isOffline) {
                holder.view.findNavController().navigate(R.id.action_orderListUnconfirmedFragment_to_editConfirmDishFragment)
            } else if (!isOffline) {
                holder.view.findNavController().navigate(R.id.action_editOnlineOrderFragment_to_editConfirmDishFragment)
            }
        }

        holder.btnTrash.setOnClickListener {
            dataset.removeAt(position)
            notifyDataSetChanged()
        }
    }
}