package com.eko.mypos.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.eko.mypos.model.Product

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ProductDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_PRODUCTS = "products"
        
        private const val COLUMN_BARCODE = "barcode"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_PRICE = "price"
        private const val COLUMN_DATE_ADDED = "date_added"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_PRODUCTS (
                $COLUMN_BARCODE TEXT PRIMARY KEY,
                $COLUMN_NAME TEXT,
                $COLUMN_PRICE REAL,
                $COLUMN_DATE_ADDED INTEGER
            )
        """.trimIndent()
        db.execSQL(createTable)
        
        // Add some sample products
        addInitialProducts(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        onCreate(db)
    }

    private fun addInitialProducts(db: SQLiteDatabase) {
        val sampleProducts = listOf(
            Product("123456789", "Pancit Canton", 15.00),
            Product("987654321", "San Miguel Beer", 75.00),
            Product("456789123", "Skyflakes", 8.50)
        )
        
        sampleProducts.forEach { product ->
            val values = ContentValues().apply {
                put(COLUMN_BARCODE, product.barcode)
                put(COLUMN_NAME, product.name)
                put(COLUMN_PRICE, product.price)
                put(COLUMN_DATE_ADDED, product.dateAdded)
            }
            db.insert(TABLE_PRODUCTS, null, values)
        }
    }

    fun addProduct(product: Product): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_BARCODE, product.barcode)
            put(COLUMN_NAME, product.name)
            put(COLUMN_PRICE, product.price)
            put(COLUMN_DATE_ADDED, product.dateAdded)
        }
        
        val success = db.insert(TABLE_PRODUCTS, null, values) != -1L
        db.close()
        return success
    }

    fun getProduct(barcode: String): Product? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_PRODUCTS,
            null,
            "$COLUMN_BARCODE = ?",
            arrayOf(barcode),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            val product = Product(
                barcode = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BARCODE)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE_ADDED))
            )
            cursor.close()
            product
        } else {
            cursor.close()
            null
        }
    }

    fun getAllProducts(): List<Product> {
        val products = mutableListOf<Product>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_PRODUCTS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_DATE_ADDED DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                products.add(
                    Product(
                        barcode = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BARCODE)),
                        name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                        dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE_ADDED))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return products
    }
} 