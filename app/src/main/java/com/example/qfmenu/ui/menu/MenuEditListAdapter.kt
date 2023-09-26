package com.example.qfmenu.ui.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.viewmodels.MenuViewModel
import com.example.qfmenu.R
import com.example.qfmenu.database.entity.MenuDb
import com.example.qfmenu.viewmodels.SaveStateViewModel

class MenuEditListAdapter(
    private val menuViewModel: MenuViewModel,
    private val context: Context,
    private val stateViewModel: SaveStateViewModel
) : ListAdapter<MenuDb, MenuEditListAdapter.MenuEditViewHolder>(DiffCallback) {

    class MenuEditViewHolder(val view: View) :
        RecyclerView.ViewHolder(view) {
        val btnUse = view.findViewById<AppCompatButton>(R.id.btnUseItemEditMenu)!!
        val btnConfig = view.findViewById<AppCompatImageButton>(R.id.btnConfigItemEditMenu)!!
        val titleItemMenu = view.findViewById<TextView>(R.id.titleItemEditMenu)!!
        val btnTrash = view.findViewById<AppCompatImageButton>(R.id.btnTrashItemEditMenu)!!
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<MenuDb>() {
            override fun areItemsTheSame(oldItem: MenuDb, newItem: MenuDb): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: MenuDb, newItem: MenuDb): Boolean {
                return oldItem.menuId == newItem.menuId
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuEditViewHolder {
        return MenuEditListAdapter.MenuEditViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_edit_menu, parent, false)
        )
    }

    private var selectedLocation: Int = -1

    override fun onBindViewHolder(holder: MenuEditViewHolder, position: Int) {
        val item = currentList[holder.adapterPosition]

        holder.btnUse.setBackgroundColor(ContextCompat.getColor(context, R.color.green_on_primary))
        holder.btnUse.setTextColor(ContextCompat.getColor(context, R.color.green_primary))

        if (item.isUsed) {
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
            currentList[this.selectedLocation].isUsed = false
            currentList[holder.adapterPosition].isUsed = true
//
//            notifyItemChanged(this.selectedLocation)
//            notifyItemChanged(holder.adapterPosition)

            menuViewModel.updateMenu(currentList[this.selectedLocation])
            menuViewModel.updateMenu(currentList[holder.adapterPosition])
            this.selectedLocation = holder.adapterPosition
        }

        holder.btnConfig.setOnClickListener {
            stateViewModel.setStateMenu(currentList[holder.adapterPosition])
            holder.view.findNavController()
                .navigate(R.id.action_editCreateMenuFragment_to_editCreateCategoryFragment)
        }

        holder.btnTrash.setOnClickListener {
            val atPosition = holder.adapterPosition
            val newList = currentList.toMutableList()
            newList.removeAt(atPosition)

            if (this.selectedLocation == atPosition && newList.size > 0) {
                newList[0].isUsed = true
                menuViewModel.updateMenu(newList[0])
                this.selectedLocation = 0
            }

            menuViewModel.deleteMenu(currentList[atPosition])
        }

        holder.titleItemMenu.text = item.menuName

    }

}