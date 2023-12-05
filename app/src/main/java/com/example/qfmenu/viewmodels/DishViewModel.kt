package com.example.qfmenu.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.qfmenu.database.dao.CategoryDao
import com.example.qfmenu.database.dao.DishDao
import com.example.qfmenu.database.entity.DishDb
import com.example.qfmenu.network.NetworkRetrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class DishViewModel(
    private val dishDao: DishDao,
    private val categoryDao: CategoryDao,
) : ViewModel() {

    private var _selectedDishes: MutableList<DishAmountDb> = mutableListOf()
    val selectedDishes: MutableList<DishAmountDb> = this._selectedDishes

    fun setSelectedDishes(selectedDishes: List<DishAmountDb>) {
        _selectedDishes.addAll(selectedDishes)
    }

    private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }

    fun insertDish(dishDb: DishDb) {
        viewModelScope.launch(Dispatchers.IO) {
            dishDao.insert(dishDb)
        }
    }

    fun updateDishWithImg(
        dishDb: DishDb,
        curUri: Uri?,
        context: Context,
        activityFrag: FragmentActivity,
        networkRetrofit: NetworkRetrofit
    ) {
        viewModelScope.launch {
            if (curUri != null) {
                val filesDir = context.filesDir
                val file = File(filesDir, "image.jpg")
                val inputStream = activityFrag.contentResolver.openInputStream(curUri)
                val outputStream = FileOutputStream(file)
                inputStream!!.copyTo(outputStream)
                inputStream.close()

                val requestBody: RequestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("forWhat", "dish")
                    .addFormDataPart("dishId", dishDb.dishId.toString())
                    .addFormDataPart(
                        "image",
                        file.name,
                        file.asRequestBody("image/*".toMediaTypeOrNull())
                    ).build()
                val response = networkRetrofit.image().create(requestBody)
                if (response.isSuccessful)
                    Log.d("Upload", response.body()?.source ?: "Empty")
                dishDao.update(dishDb.copy(image = response.body()?.source ?: "Empty"))

            }
        }
    }

    fun updateDish(
        dishDb: DishDb,
    ) {
        viewModelScope.launch {
            dishDao.update(dishDb)
        }
    }

    fun deleteDish(dishDb: DishDb) {
        viewModelScope.launch {
            dishDao.delete(dishDb)
        }
    }

    fun getDishesLiveData(categoryId: Long): LiveData<List<DishDb>> {
        return categoryDao.getCategoryWithDishesLiveData(categoryId).map { it.dishesDb }
    }


    suspend fun getDishesAmountDb(categoryId: Long): List<DishAmountDb> {
        return categoryDao.getCategoryWithDishes(categoryId).dishesDb.map {
            DishAmountDb(
                it,
                0,
                false
            )
        }
    }


    fun getDishesAmountDbLiveData(categoryId: Long): LiveData<List<DishAmountDb>> {
        return categoryDao.getCategoryWithDishesLiveData(categoryId).map { dishes ->
            dishes.dishesDb.map { DishAmountDb(it, 0, false) }
        }
    }

}

data class DishAmountDb(
    val dishDb: DishDb,
    var amount: Int,
    var selected: Boolean = false
)

class DishViewModelFactory(
    private val dishDao: DishDao,
    private val categoryDao: CategoryDao,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DishViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DishViewModel(dishDao, categoryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}