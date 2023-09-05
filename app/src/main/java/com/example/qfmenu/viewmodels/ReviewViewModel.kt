package com.example.menumanager.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.menumanager.review.DatasourceReview
import com.example.qfmenu.viewmodels.models.Review

class ReviewViewModel : ViewModel() {
    private val _reviewForCreate = MutableLiveData<Review>()
    val reviewForCreate : LiveData<Review> get() = _reviewForCreate

    private val _storeList = MutableLiveData<List<Review>>()
    val storeList : LiveData<List<Review>> get() = _storeList

    private val _dishList = MutableLiveData<List<Review>>()
    val dishList : LiveData<List<Review>> get() = _dishList

    init {
        _storeList.value = DatasourceReview().loadReviewStore()
        _dishList.value = DatasourceReview().loadReviewDish()
    }

    fun createReview(review: Review) {
        _reviewForCreate.value = review
    }

    fun updateReview(id: Int, review: Review) {}

    fun deleteReview(id: Int, review: Review) {}

    fun getReview(id: Int) {}


}