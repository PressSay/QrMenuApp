package com.example.menumanager.menu.dish

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.R
import com.example.qfmenu.viewmodels.models.Dish

class DishMenuAdapter(
    private val context: Context,
    private val dataset: List<Dish>,
) : RecyclerView.Adapter<DishMenuAdapter.ItemViewHolder>() {

    private val _listSelected: MutableList<Dish> = mutableListOf()
    val listSelected get() = _listSelected

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.item_menu_img)
        val title: TextView = view.findViewById(R.id.item_menu_title)
        val cost: TextView = view.findViewById(R.id.item_menu_cost)
        val amount: TextView = view.findViewById(R.id.item_menu_amount)
        val btnPlus: AppCompatButton = view.findViewById(R.id.itemMenuPlusBtn)
        val btnMinus: AppCompatButton = view.findViewById(R.id.itemMenuMinusBtn)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu, parent, false)

        return ItemViewHolder(adapterView)
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        holder.img.setImageResource(item.imgResourceId)
        holder.title.text = item.title
        holder.cost.text = item.cost.toString()
        holder.amount.text = item.amount.toString()

        holder.btnPlus.setOnClickListener {
            dataset[position].amount += 1
            if (!item.selected) {
                _listSelected.add(item)
                dataset[position].selected = true
            }
            holder.amount.text = dataset[position].amount.toString()
        }

        holder.btnMinus.setOnClickListener {
            if (dataset[position].selected) {
                dataset[position].amount -= 1
                if (dataset[position].amount <= 0) {
                    dataset[position].selected = false
                    dataset[position].amount = 0
                }
            }
            holder.amount.text = dataset[position].amount.toString()
        }

    }

    override fun getItemCount(): Int {
        return 3
    }
}