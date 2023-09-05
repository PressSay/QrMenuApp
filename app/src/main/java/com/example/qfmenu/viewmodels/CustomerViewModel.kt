package com.example.menumanager.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.qfmenu.viewmodels.models.Dish
import com.example.qfmenu.viewmodels.models.Customer
import com.example.qfmenu.viewmodels.models.Table
import com.example.qfmenu.R
import java.util.Date

class CustomerViewModel : ViewModel() {



    private val _customerForCreate = MutableLiveData<Customer>()
    val customerForCreate: LiveData<Customer> get() = _customerForCreate

    private val _customerList = MutableLiveData<List<Customer>>()
    val customerList: LiveData<List<Customer>> get() = _customerList

    init {
        _customerList.value = listOf(
            Customer(
                1,
                Table("Online", "None"),
                Date(2022, 12, 1),
                listOf(
                    Dish(R.drawable.img_image_4, "title1", "something", "18,000$", 1),
                    Dish(R.drawable.img_image_4, "title2", "something", "18,000$", 1)
                ),
                "joaisjdof",
                "jaoisdjf",
                "now",
                "0123456789",
                "address 1",
            ),
            Customer(
                2,
                Table("Online", "None"),
                Date(2022, 12, 1),
                listOf(
                    Dish(R.drawable.img_image_4, "title1", "something", "18,000$", 1),
                    Dish(R.drawable.img_image_4, "title2", "something", "18,000$", 1)
                ),
                "joaisjdof",
                "jaoisdjf",
                "now",
                "0123456789",
                "address 1",
            )
        )
    }

    fun createCustomer(customer: Customer) {
        _customerForCreate.value = customer
    }

    fun updateCustomer(id: Int, customer: Customer) {}

    fun deleteCustomer(id: Int) {}

    fun getCustomer(id: Int) {}


}


