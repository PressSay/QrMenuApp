package com.example.qfmenu.network.entity

data class Image(
    val image: ImageX,
    val imageDish: ImageDishX,
    val source: String
)

data class ImageX(
    val imageId: Int,
    val source: String
)

data class ImageDishX(
    val dishId: String,
    val imageId: Int
)

data class ImageDish(
    val imageId: Int,
    val source: String
)