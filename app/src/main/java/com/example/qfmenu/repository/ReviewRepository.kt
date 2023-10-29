package com.example.qfmenu.repository

import com.example.qfmenu.database.dao.ReviewDao
import com.example.qfmenu.database.entity.ReviewCustomerDb
import com.example.qfmenu.database.entity.ReviewDb
import com.example.qfmenu.database.entity.ReviewDishDb
import com.example.qfmenu.network.NetworkRetrofit
import retrofit2.http.Field

class ReviewRepository(
    private val networkRetrofit: NetworkRetrofit,
    private val reviewDao: ReviewDao,
) {
    suspend fun createListDish(reviewDishDbList: List<ReviewDishDb>) {
        reviewDishDbList.forEach { reviewDishDb ->
            val reviewDb = reviewDao.getReview(reviewDishDb.reviewId)
            val reviewNet = networkRetrofit.review().create(
                "1",
                reviewDishDb.dishId.toString(),
                reviewDishDb.customerId.toString(),
                reviewDb.isThumbUp.toString(),
                reviewDb.description
            )
        }
    }

    suspend fun createListCustomer(reviewCustomerDbList: List<ReviewCustomerDb>) {
        reviewCustomerDbList.forEach { reviewCustomerDb ->
            val reviewDb = reviewDao.getReview(reviewCustomerDb.reviewId)
            val reviewNet = networkRetrofit.review().create(
                "0",
                "-1",
                reviewCustomerDb.customerId.toString(),
                reviewDb.isThumbUp.toString(),
                reviewDb.description
            )
        }
    }

    suspend fun updateListDish(reviewDishDbList: List<ReviewDishDb>) {
        reviewDishDbList.forEach { reviewDishDb ->
            val reviewDb = reviewDao.getReview(reviewDishDb.reviewId)
            val reviewNet = networkRetrofit.review().update(
                reviewDishDb.reviewId.toString(),
                reviewDb.description,
                reviewDb.isThumbUp.toString()

            )
        }
    }

    suspend fun updateListCustomer(reviewCustomerDbList: List<ReviewCustomerDb>) {
        reviewCustomerDbList.forEach { reviewCustomerDb ->
            val reviewDb = reviewDao.getReview(reviewCustomerDb.reviewId)
            val reviewNet = networkRetrofit.review().update(
                reviewCustomerDb.reviewId.toString(),
                reviewDb.description,
                reviewDb.isThumbUp.toString()
            )
        }
    }

    suspend fun deleteListDish(reviewDishDbList: List<ReviewDishDb>) {
        reviewDishDbList.forEach { reviewDishDb ->
            val reviewDb = reviewDao.getReview(reviewDishDb.reviewId)
            val reviewNet = networkRetrofit.review().delete(reviewDishDb.reviewId.toString())
        }
    }

    suspend fun deleteListCustomer(reviewCustomerDbList: List<ReviewCustomerDb>) {
        reviewCustomerDbList.forEach { reviewCustomerDb ->
            val reviewDb = reviewDao.getReview(reviewCustomerDb.reviewId)
            val reviewNet = networkRetrofit.review().delete(reviewCustomerDb.reviewId.toString())
        }
    }

    suspend fun fetchReviewDish() {
        val reviewNet = networkRetrofit.review().findALlDish()
        if (reviewNet.isSuccessful) {
            reviewNet.body()?.let {
                reviewDao.insertAllReviewDishCrossRef(it.map { review ->
                    ReviewDishDb(
                        dishId = review.dish.dishId,
                        reviewId = review.review.reviewId.toLong(),
                        customerId = review.customerId
                    )
                })
            }
        }
    }

    suspend fun fetchReviewCustomer() {
        val reviewNet = networkRetrofit.review().findALlCustomer()
        if (reviewNet.isSuccessful) {
            reviewNet.body()?.let {
                reviewDao.insertAll(it.map { review ->
                    ReviewDb(
                        reviewId = review.review.reviewId.toLong(),
                        description = review.review.description,
                        isThumbUp = review.review.star,
                    )
                })
                reviewDao.insertAllReviewCustomerCrossRef(it.map { review ->
                    ReviewCustomerDb(
                        customerId = review.customer.customerId,
                        reviewId = review.review.reviewId.toLong(),
                    )
                })
            }
        }
    }
}