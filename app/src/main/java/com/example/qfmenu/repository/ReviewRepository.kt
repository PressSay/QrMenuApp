package com.example.qfmenu.repository

import com.example.qfmenu.database.dao.ReviewDao
import com.example.qfmenu.database.entity.ReviewBillDb
import com.example.qfmenu.database.entity.ReviewDishDb
import com.example.qfmenu.network.NetworkRetrofit
import com.example.qfmenu.network.entity.RevBill
import com.example.qfmenu.network.entity.RevDish

class ReviewRepository(
    private val networkRetrofit: NetworkRetrofit,
    private val reviewDao: ReviewDao,
) {
    private suspend fun updateRevBill(reviewBill: RevBill) {
        reviewDao.updateRevBill(reviewBill.customerId, reviewBill.description, reviewBill.star)
    }

    private suspend fun updateRevBillNet(reviewBillDb: ReviewBillDb) {
        networkRetrofit.review().updateRevBill(
            reviewBillDb.customerId.toString(),
            reviewBillDb.isThumbUp.toString(),
            reviewBillDb.description
        )
    }

    private suspend fun updateRevDish(reviewDish: RevDish) {
        reviewDao.updateRevDish(
            reviewDish.dishId,
            reviewDish.customerId,
            reviewDish.description,
            reviewDish.star,
        )
    }

    private suspend fun updateRevDishNet(reviewDishDb: ReviewDishDb) {
        networkRetrofit.review().updateRevDish(
            reviewDishDb.dishId.toString(),
            reviewDishDb.customerId.toString(),
            reviewDishDb.isThumbUp.toString(),
            reviewDishDb.description!!,
        )
    }

    private suspend fun deleteRevBill(reviewBillDb: ReviewBillDb) {
        reviewDao.deleteRevBill(reviewBillDb)
    }

    suspend fun deleteRevBillNet(reviewBill: RevBill) {
        networkRetrofit.review().deleteRevBill(reviewBill.customerId.toString())
    }

    private  suspend fun deletedRevDish(reviewDishDb: ReviewDishDb) {
        reviewDao.deleteRevDish(reviewDishDb.dishId, reviewDishDb.customerId)
    }

    suspend fun deleteRevDishNet(reviewDish: RevDish) {
        networkRetrofit.review().deleteRevDish(reviewDish.dishId.toString(), reviewDish.customerId.toString())
    }


    private suspend fun fetchAllRevBill() {
        val revBillArrNet = networkRetrofit.review().findAllBill()
        val revBillArrDb = reviewDao.getRevBillArr()
        if (!revBillArrNet.isSuccessful)
            return
        val revBillArrSr = revBillArrNet.body()!!

        for (revBill in revBillArrSr) {
            val revBillDb = reviewDao.getRevBill(revBill.customerId)
            try {
                if (revBillDb == null)
                    reviewDao.insertRevBill(
                        ReviewBillDb(
                            revBill.customerId,
                            revBill.star,
                            revBill.description,
                        )
                    )
                else
                    updateRevBillNet(revBillDb)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        for (revBillDb in revBillArrDb) {
            val revBillNet =
                networkRetrofit.review().findOneBill(revBillDb.customerId.toString())
            if (!revBillNet.isSuccessful)
                this.deleteRevBill(revBillDb)
            else {
                val revBillSr = revBillNet.body()!!
                this.updateRevBill(revBillSr)
            }
        }

    }

    private suspend fun fetchAllRevDish() {
        val revDishArrNet = networkRetrofit.review().findAllDish()
        val revDishArrDb = reviewDao.getRevDishArr()
        if (!revDishArrNet.isSuccessful)
            return
        val revDishArrSr = revDishArrNet.body()!!

        for (revDish in revDishArrSr) {
            val revDishDb = reviewDao.getRevDish(revDish.dishId, revDish.customerId)
            try {
                if (revDishDb == null)
                    reviewDao.insertRevDish(
                        ReviewDishDb(
                            revDish.dishId,
                            revDish.customerId,
                            revDish.star,
                            revDish.description,
                        )
                    )
                else
                    updateRevDishNet(revDishDb)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        for (revDishDb in revDishArrDb) {
            val revDishNet = networkRetrofit.review()
                .findOneDish(revDishDb.dishId.toString(), revDishDb.customerId.toString())
            if (!revDishNet.isSuccessful) {
                this.deletedRevDish(revDishDb)
            } else {
                val revDishSr = revDishNet.body()!!
                this.updateRevDish(revDishSr)
            }
        }
    }

    suspend fun fetchReview() {
        fetchAllRevBill()
        fetchAllRevDish()
    }

}