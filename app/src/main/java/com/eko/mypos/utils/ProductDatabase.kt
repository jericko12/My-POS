package com.eko.mypos.utils

import com.eko.mypos.model.Product

class ProductDatabase {
    private val products = mapOf(
        "123456789" to Product("123456789", "Milk", 3.99),
        "987654321" to Product("987654321", "Bread", 2.49),
        "456789123" to Product("456789123", "Eggs", 4.99)
    )

    fun getProduct(barcode: String): Product? {
        return products[barcode]
    }
} 