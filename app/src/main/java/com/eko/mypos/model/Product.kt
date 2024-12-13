package com.eko.mypos.model

data class Product(
    val barcode: String,
    val name: String,
    val price: Double,
    val dateAdded: Long = System.currentTimeMillis()
) 