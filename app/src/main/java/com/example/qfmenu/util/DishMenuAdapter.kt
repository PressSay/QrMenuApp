package com.example.qfmenu.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
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
import com.example.qfmenu.network.NetworkRetrofit
import com.example.qfmenu.viewmodels.DishAmountDb
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import java.io.IOException
import java.text.NumberFormat
import java.util.Locale


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

//        if (saveStateViewModel.stateCategoryPosition < saveStateViewModel.stateDishesByCategories.size) {
        val positionId = saveStateViewModel.stateCategoryPosition

//        Log.d(
//            "Adapter",
//            "$positionId ${saveStateViewModel.stateDishesByCategories[positionId]}"
//        )
        saveStateViewModel.stateDishesByCategories[positionId]?.forEach {
            if (it.dishDb.dishId == item.dishDb.dishId) {
                _listSelected.add(item)
                currentList[position].selected = true
                currentList[position].amount = it.amount
                item = currentList[position]
            }
        }


        holder.btnMinus.isEnabled = item.selected
        var colorIconMinus =
            if (item.selected) R.color.green_error else R.color.green_surface_variant

        holder.iconMinus.setColorFilter(
            ContextCompat.getColor(
                context,
                colorIconMinus
            )
        )
        // Image get api here!
        try {
            holder.img.background = null
            Picasso.get().load("${NetworkRetrofit.BASE_URL}/${item.dishDb.image}")
                .transform(RoundedTransformation(48F, 0))
                .fit().centerCrop().into(holder.img)
        } catch (networkError: IOException) {
            Log.d("NoInternet", true.toString())
        }

        val formattedAmount =
            NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(item.dishDb.cost)
        holder.title.text = item.dishDb.name
        holder.cost.text = formattedAmount
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

            colorIconMinus =
                if (currentList[position].selected) R.color.green_error else R.color.green_surface_variant
            holder.iconMinus.setColorFilter(
                ContextCompat.getColor(
                    context,
                    colorIconMinus
                )
            )
        }

        var isBtnEnable = false
        for (it in saveStateViewModel.stateDishesByCategories) {
            if (it.value.size != 0) {
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

                    for (i in 0..<_listSelected.size) {
                        if (_listSelected[i].dishDb.dishId == currentList[position].dishDb.dishId) {
                            _listSelected.removeAt(i)
                            break
                        }
                    }

                }
            }

            if (_listSelected.isEmpty()) {
                isBtnEnable = false
                for (it1 in saveStateViewModel.stateDishesByCategories) {
                    val dishAmountDbList = it1.value
                    if (dishAmountDbList.size != 0 && it1.key != saveStateViewModel.stateCategoryPosition) {
                        isBtnEnable = true
                        break
                    }
                }
            }

            holder.amount.text = currentList[position].amount.toString()
            btnBuy.isEnabled = _listSelected.isNotEmpty() || isBtnEnable

            holder.btnMinus.isEnabled = currentList[position].selected
            colorIconMinus =
                if (holder.btnMinus.isEnabled) R.color.green_error else R.color.green_surface_variant
            holder.iconMinus.setColorFilter(
                ContextCompat.getColor(
                    context,
                    colorIconMinus
                )
            )

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

class RoundedTransformation(
    private val radius: Float, // dp
    private var margin: Int
) : Transformation {

    override fun transform(source: Bitmap): Bitmap {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = BitmapShader(
            source, Shader.TileMode.CLAMP,
            Shader.TileMode.CLAMP
        )
        val output = Bitmap.createBitmap(
            source.width,
            source.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)

        val corners = floatArrayOf(
            0f, 0f,  // Top left radius in px
            radius, radius,  // Top right radius in px
            0f, 0f,  // Bottom right radius in px
            radius, radius // Bottom left radius in px
        )
        val path = Path()
        val rect = RectF(
            margin.toFloat(),
            margin.toFloat(),
            (source.width - margin).toFloat(),
            (source.height - margin).toFloat()
        )
        path.addRoundRect(rect, corners, Path.Direction.CW)
        canvas.drawPath(path, paint)

//        canvas.drawRoundRect(
//            RectF(margin.toFloat(), margin.toFloat(), (source.width - margin).toFloat(), (source.height - margin).toFloat()), radius.toFloat(), radius.toFloat(), paint
//        )
        if (source != output) {
            source.recycle()
        }
        return output
    }

    override fun key(): String {
        return "rounded"
    }
}