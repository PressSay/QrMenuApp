package com.example.qfmenu.repository

import com.example.qfmenu.database.dao.ReviewDao
import com.example.qfmenu.database.entity.ReviewCustomerDb
import com.example.qfmenu.database.entity.ReviewDb
import com.example.qfmenu.database.entity.ReviewDishDb
import com.example.qfmenu.network.NetworkRetrofit

class ReviewRepository(
    private val networkRetrofit: NetworkRetrofit,
    private val reviewDao: ReviewDao,
) {

    suspend fun createDishRev(reviewDishDb: ReviewDishDb) {
        val reviewDb = reviewDao.getReview(reviewDishDb.reviewId)!!
        val reviewNet = networkRetrofit.review().create(
            "1",
            reviewDishDb.dishId.toString(),
            reviewDishDb.customerId.toString(),
            reviewDb.isThumbUp.toString(),
            reviewDb.description
        )
    }

    suspend fun createCustomerRev(reviewCustomerDb: ReviewCustomerDb) {
        val reviewDb = reviewDao.getReview(reviewCustomerDb.reviewId)!!
        val reviewNet = networkRetrofit.review().create(
            "0",
            "-1",
            reviewCustomerDb.customerId.toString(),
            reviewDb.isThumbUp.toString(),
            reviewDb.description
        )
    }

    suspend fun fetchReview() {
        val reviewListNet = networkRetrofit.review().findAll()
        val reviewDishNet = networkRetrofit.review().findAllDish()
        val reviewCustomerNet = networkRetrofit.review().findAllCustomer()

        if (!reviewListNet.isSuccessful || !reviewDishNet.isSuccessful || !reviewCustomerNet.isSuccessful) {
            return
        }
        val review = reviewListNet.body()!!
        val reviewDish = reviewDishNet.body()!!
        val reviewCustomer = reviewCustomerNet.body()!!

        if (review.isEmpty() || reviewDish.isEmpty() || reviewCustomer.isEmpty()) {
            return
        }
        reviewDao.deleteAll()
        reviewDao.deleteAllReviewDish()
        reviewDao.deleteAllReviewCustomer()
        reviewDao.resetKey();
        reviewDao.insertAll(review.map {
            ReviewDb(
                reviewId = it.reviewId.toLong(),
                isThumbUp = it.star,
                description = it.description
            )
        })
        for (revDish in reviewDish) {
            reviewDao.insertReviewDishCrossRef(
                ReviewDishDb(
                    dishId = revDish.reviewDish.dishId,
                    reviewId = revDish.review.reviewId.toLong(),
                    customerId = revDish.reviewDish.customerId
                )
            )
        }
        for (revCust in reviewCustomer) {
            reviewDao.insertReviewCustomerCrossRef(
                ReviewCustomerDb(
                    customerId = revCust.reviewCustomer.customerId,
                    reviewId = revCust.review.reviewId.toLong()
                )
            )
        }

    }

}