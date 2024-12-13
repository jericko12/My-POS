package com.eko.mypos

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eko.mypos.databinding.ActivityAddProductBinding
import com.eko.mypos.model.Product
import com.eko.mypos.utils.DatabaseHelper

class AddProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProductBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        // Get barcode from intent if passed
        intent.getStringExtra("barcode")?.let { barcode ->
            binding.etBarcode.setText(barcode)
        }

        binding.btnSave.setOnClickListener {
            saveProduct()
        }
    }

    private fun saveProduct() {
        val barcode = binding.etBarcode.text.toString()
        val name = binding.etName.text.toString()
        val priceStr = binding.etPrice.text.toString()

        if (barcode.isEmpty() || name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceStr.toDoubleOrNull()
        if (price == null || price <= 0) {
            Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show()
            return
        }

        val product = Product(barcode, name, price)
        if (dbHelper.addProduct(product)) {
            Toast.makeText(this, "Product saved successfully", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Failed to save product", Toast.LENGTH_SHORT).show()
        }
    }
} 