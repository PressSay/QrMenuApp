package com.example.qfmenu.review

import com.example.qfmenu.viewmodels.models.Review

class DatasourceReview {
    fun loadReviewDish(): List<Review> {
        return listOf(
            Review(true, "Yum", false),
            Review(true, "Good", false),
            Review(true, "Great", false),
        )
    }

    fun loadReviewStore(): List<Review> {
        return listOf(
            Review(true, "Store Yum", false),
            Review(true, "Store Good", false),
            Review(true, "Store Great", false)
        )
    }
}