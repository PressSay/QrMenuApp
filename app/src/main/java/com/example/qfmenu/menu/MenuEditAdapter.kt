package com.example.menumanager.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.R
import com.example.qfmenu.viewmodels.models.Menu

class MenuEditAdapter(
    private val context: Context,
    private val dataset: MutableList<Menu>
) : RecyclerView.Adapter<MenuEditAdapter.ItemViewHolder>() {

    fun getDataset(): MutableList<Menu> {
        return dataset
    }

    private var selectedLocation: Int = -1

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val btnUse = view.findViewById<AppCompatButton>(R.id.btnUseItemEditMenu)!!
        val btnConfig = view.findViewById<AppCompatImageButton>(R.id.btnConfigItemEditMenu)!!
        val titleItemMenu = view.findViewById<TextView>(R.id.titleItemEditMenu)!!
        val btnTrash = view.findViewById<AppCompatImageButton>(R.id.btnTrashItemEditMenu)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_edit_menu, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[holder.adapterPosition]

        holder.btnUse.setBackgroundColor(ContextCompat.getColor(context, R.color.green_on_primary))
        holder.btnUse.setTextColor(ContextCompat.getColor(context, R.color.green_primary))

        if (item.isSelect) {
            holder.btnUse.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.green_secondary_container
                )
            )
            holder.btnUse.setTextColor(ContextCompat.getColor(context, R.color.green_primary))
            this.selectedLocation = holder.adapterPosition
        }

        holder.btnUse.setOnClickListener {
            dataset[this.selectedLocation].isSelect = false
            dataset[holder.adapterPosition].isSelect = true
            notifyItemChanged(this.selectedLocation)
            notifyItemChanged(holder.adapterPosition)
            this.selectedLocation = holder.adapterPosition
        }

        holder.btnConfig.setOnClickListener {
            holder.view.findNavController()
                .navigate(R.id.action_editCreateMenuFragment_to_editCreateCategoryFragment)
        }

        holder.btnTrash.setOnClickListener {
            dataset.removeAt(holder.adapterPosition)
            if (this.selectedLocation == holder.adapterPosition && dataset.size > 0) {
                dataset[0].isSelect = true
                this.selectedLocation = 0
            }
            notifyItemRemoved(holder.adapterPosition)
            if (dataset.size > 0) {
                notifyItemChanged(0)
            }
        }

        holder.titleItemMenu.text = item.title

    }
}