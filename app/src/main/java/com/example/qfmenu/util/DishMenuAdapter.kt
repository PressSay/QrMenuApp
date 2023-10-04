package com.example.qfmenu.util

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.R
import com.example.qfmenu.viewmodels.DishAmountDb
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.squareup.picasso.Picasso
import java.io.IOException

class DishMenuAdapter(
    private val context: Context,
    private val btnBuy: AppCompatButton,
    private val saveStateViewModel: SaveStateViewModel
) : ListAdapter<DishAmountDb, DishMenuAdapter.ItemViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<DishAmountDb>() {
            override fun areItemsTheSame(oldItem: DishAmountDb, newItem: DishAmountDb): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: DishAmountDb, newItem: DishAmountDb): Boolean {
                return oldItem.dishDb.dishId == newItem.dishDb.dishId
            }
        }
    }

    private var _listSelected: MutableList<DishAmountDb> = mutableListOf()
    val listSelected get() = _listSelected
    fun setListSelected(listSelected: MutableList<DishAmountDb>) {
        _listSelected = listSelected
    }

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.item_menu_img)
        val title: TextView = view.findViewById(R.id.item_menu_title)
        val description: TextView = view.findViewById(R.id.item_menu_description)
        val cost: TextView = view.findViewById(R.id.item_menu_cost)
        val amount: TextView = view.findViewById(R.id.item_menu_amount)
        val btnPlus: AppCompatButton = view.findViewById(R.id.itemMenuPlusBtn)
        val btnMinus: AppCompatButton = view.findViewById(R.id.itemMenuMinusBtn)
        val iconMinus: ImageView = view.findViewById(R.id.itemMenuIconMinus)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu, parent, false)

        return ItemViewHolder(adapterView)
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var item = currentList[position]

        if (saveStateViewModel.stateCategoryPosition < saveStateViewModel.stateDishesByCategories.size) {
            saveStateViewModel.stateDishesByCategories[saveStateViewModel.stateCategoryPosition].forEach {
                if (it.dishDb.dishId == item.dishDb.dishId) {
                    _listSelected.add(item)
                    currentList[position].selected = true
                    currentList[position].amount = it.amount
                    item = currentList[position];
                }
            }
        }

        holder.btnMinus.isEnabled = item.selected
        var colorIconMinus = if (item.selected) R.color.green_error else R.color.green_surface_variant

        holder.iconMinus.setColorFilter(ContextCompat.getColor(
            context,
            colorIconMinus
        ))

        try {
            Picasso.get().load("http://192.168.1.6/image-dish/0e61f2dc00aa125284584df1a2c01f13.jpg")
                .resize(50, 50).centerCrop().into(holder.img)
        } catch (networkError: IOException) {
            holder.img.setImageResource(R.drawable.img_image_6)
            Log.d("NoInternet", true.toString())
        }

        holder.title.text = item.dishDb.dishName
        holder.cost.text = item.dishDb.cost.toString()
        holder.amount.text = item.amount.toString()
        holder.description.text = item.dishDb.description


        holder.btnPlus.setOnClickListener {
            currentList[position].amount += 1
            if (!item.selected) {
                _listSelected.add(item)
                currentList[position].selected = true
            }
            holder.amount.text = currentList[position].amount.toString()
            btnBuy.isEnabled = _listSelected.isNotEmpty()
            holder.btnMinus.isEnabled = currentList[position].selected

            val colorBtnBuy = if (btnBuy.isEnabled) R.color.green_primary
                else R.color.green_secondary

            btnBuy.setTextColor(
                ContextCompat.getColor(
                    context,
                    colorBtnBuy
                )
            )

            colorIconMinus = if (currentList[position].selected) R.color.green_error else R.color.green_surface_variant
            holder.iconMinus.setColorFilter(ContextCompat.getColor(
                context,
                colorIconMinus
            ))
        }

        var isBtnEnable = false
        for (it in saveStateViewModel.stateDishesByCategories) {
            if (it.size != 0) {
                isBtnEnable = true
                break
            }
        }

        btnBuy.isEnabled = isBtnEnable

        var colorBtnBuy = if (btnBuy.isEnabled) R.color.green_primary
            else R.color.green_secondary

        btnBuy.setTextColor(
            ContextCompat.getColor(
                context,
                colorBtnBuy
            )
        )

        holder.btnMinus.setOnClickListener {
            if (currentList[position].selected) {
                currentList[position].amount -= 1
                if (currentList[position].amount <= 0) {
                    currentList[position].selected = false
                    currentList[position].amount = 0
                    _listSelected.remove(currentList[position])
                }
            }
            holder.amount.text = currentList[position].amount.toString()
            btnBuy.isEnabled = _listSelected.isNotEmpty() || isBtnEnable

            holder.btnMinus.isEnabled = currentList[position].selected
            colorIconMinus = if (holder.btnMinus.isEnabled) R.color.green_error else R.color.green_surface_variant
            holder.iconMinus.setColorFilter(ContextCompat.getColor(
                context,
                colorIconMinus
            ))

            colorBtnBuy = if (btnBuy.isEnabled) R.color.green_primary
                else R.color.green_secondary

            btnBuy.setTextColor(
                ContextCompat.getColor(
                    context,
                    colorBtnBuy
                )
            )
        }

    }

    override fun getItemCount(): Int {
        return currentList.size
    }
}