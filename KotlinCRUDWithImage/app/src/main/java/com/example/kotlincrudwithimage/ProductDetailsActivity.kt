package com.example.kotlincrudwithimage

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var imageViewProduct: ImageView
    private lateinit var textViewProductName: TextView
    private lateinit var textViewProductPrice: TextView
    private lateinit var textViewProductDescription: TextView
    private lateinit var buttonUpdate: Button
    private lateinit var buttonDelete: Button

    private lateinit var productId: String
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        imageViewProduct = findViewById(R.id.imageViewProduct)
        textViewProductName = findViewById(R.id.textViewProductName)
        textViewProductPrice = findViewById(R.id.textViewProductPrice)
        textViewProductDescription = findViewById(R.id.textViewProductDescription)
        buttonUpdate = findViewById(R.id.buttonUpdate)
        buttonDelete = findViewById(R.id.buttonDelete)


        // Get the product ID from the intent
        productId = intent.getStringExtra("productId") ?: ""

        println("ProductDetailsActivity: Product ID = $productId")

        // Load product details from Firestore and display in the UI
        loadProductDetails()

        buttonUpdate.setOnClickListener {
            // Start the ProductUpdateActivity for updating the product
            val intent = Intent(this, ProductUpdateActivity::class.java)
            intent.putExtra("productId", productId)
            startActivity(intent)
        }

        buttonDelete.setOnClickListener {
            // Delete the product from Firestore
//            deleteProduct()
            showConfirmationDialog()
        }

    }

    private fun loadProductDetails() {
        // Query the product details from Firestore and update the UI
        firestore.collection("products").document(productId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val product = documentSnapshot.toObject(Product::class.java)
                    product?.let {
                        textViewProductName.text = it.name
                        textViewProductPrice.text = "$" + it.price.toString()
                        textViewProductDescription.text = it.description
                        Picasso.get().load(it.imageUrl).into(imageViewProduct)
                    }
                }
            }
            .addOnFailureListener {
                // Handle the failure case
            }
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Product")
        builder.setMessage("Are you sure you want to delete this product?")
        builder.setPositiveButton("Yes") { dialog, which ->
            // User clicked the "Yes" button, proceed with the delete operation
            deleteProduct()
        }
        builder.setNegativeButton("No", null) // Do nothing when "No" is clicked
        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteProduct() {
        // Delete the product from Firestore
        firestore.collection("products").document(productId)
            .delete()
            .addOnSuccessListener {
                // Product deleted successfully
//                finish()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                // Handle the failure case
            }
    }

}