package com.example.qfmenu.util

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
import com.example.qfmenu.R
import com.example.qfmenu.database.entity.MenuDb
import com.example.qfmenu.network.entity.Menu
import com.example.qfmenu.repository.MenuRepository
import com.example.qfmenu.viewmodels.MenuViewModel
import com.example.qfmenu.viewmodels.SaveStateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConfigMenuAdapter(
    private val menuViewModel: MenuViewModel,
    private val context: Context,
    private val stateViewModel: SaveStateViewModel,
    private val menuRepository: MenuRepository
) : ListAdapter<MenuDb, ConfigMenuAdapter.MenuEditViewHolder>(DiffCallback) {

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
        return MenuEditViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_config_menu, parent, false)
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
            val selectedP = this.selectedLocation
            val adapterP = holder.adapterPosition
            currentList[selectedP].isUsed = false
            currentList[adapterP].isUsed = true
//
//            notifyItemChanged(this.selectedLocation)
//            notifyItemChanged(holder.adapterPosition)
            CoroutineScope(Dispatchers.IO).launch {
                menuRepository.updateMenuNet(currentList[selectedP])
                menuRepository.updateMenuNet(currentList[adapterP])
                menuViewModel.updateMenu(currentList[selectedP])
                menuViewModel.updateMenu(currentList[adapterP])
            }
            this.selectedLocation = holder.adapterPosition
        }

        holder.btnConfig.setOnClickListener {
            stateViewModel.setStateMenu(currentList[holder.adapterPosition])
            holder.view.findNavController()
                .navigate(R.id.action_configMenuFragment_to_configCategoryFragment)
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

            CoroutineScope(Dispatchers.IO).launch {
                menuViewModel.deleteMenu(currentList[atPosition])
                menuRepository.deleteMenuNet(
                    Menu(
                        currentList[atPosition].menuId,
                        if (currentList[atPosition].isUsed)  1 else 0,
                        currentList[atPosition].name
                    )
                )
            }
        }

        holder.titleItemMenu.text = item.name

    }

}