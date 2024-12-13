package com.eko.mypos

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.eko.mypos.databinding.ActivityMainBinding
import com.eko.mypos.model.Product
import com.eko.mypos.utils.DatabaseHelper
import android.content.Intent

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var codeScanner: CodeScanner
    private lateinit var dbHelper: DatabaseHelper
    private val scannedItems = mutableListOf<Product>()
    private var total = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        setupPermissions()
        setupScanner()
        setupViews()
    }

    private fun setupScanner() {
        codeScanner = CodeScanner(this, binding.scannerView).apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback { result ->
                runOnUiThread {
                    handleScannedBarcode(result.text)
                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, 
                        "Scanner error: ${it.message}", 
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupViews() {
        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        binding.btnAddProduct.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }

        binding.btnClear.setOnClickListener {
            scannedItems.clear()
            total = 0.0
            updateUI()
        }
    }

    private fun handleScannedBarcode(barcode: String) {
        val product = dbHelper.getProduct(barcode)
        if (product != null) {
            scannedItems.add(product)
            total += product.price
            updateUI()
            binding.scannerView.postDelayed({
                codeScanner.startPreview()
            }, 2000)
        } else {
            runOnUiThread {
                android.app.AlertDialog.Builder(this)
                    .setTitle("Product Not Found")
                    .setMessage("Would you like to add this product?")
                    .setPositiveButton("Yes") { _, _ ->
                        val intent = Intent(this, AddProductActivity::class.java)
                        intent.putExtra("barcode", barcode)
                        startActivity(intent)
                    }
                    .setNegativeButton("No") { _, _ ->
                        codeScanner.startPreview()
                    }
                    .setOnDismissListener {
                        codeScanner.startPreview()
                    }
                    .show()
            }
        }
    }

    private fun updateUI() {
        val itemsText = scannedItems.joinToString("\n") { 
            "${it.name} - ₱${String.format("%.2f", it.price)}" 
        }
        binding.tvItems.text = itemsText
        binding.tvTotal.text = "Total: ₱${String.format("%.2f", total)}"
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA)
        
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 123
    }
}