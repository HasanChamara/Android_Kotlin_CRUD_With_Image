package com.example.kotlincrudwithimage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class ProductUpdateActivity : AppCompatActivity() {

    private lateinit var productNameEditText: EditText
    private lateinit var productPriceEditText: EditText
    private lateinit var productDescriptionEditText: EditText
    private lateinit var productImageView: ImageView
    private lateinit var updateButton: Button

    private lateinit var productId: String
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageReference: StorageReference
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_update)

        productNameEditText = findViewById(R.id.editTextProductName)
        productPriceEditText = findViewById(R.id.editTextProductPrice)
        productDescriptionEditText = findViewById(R.id.editTextProductDescription)
        productImageView = findViewById(R.id.imageViewProduct)
        updateButton = findViewById(R.id.buttonUpdate)

        // Get the product ID from the intent
        productId = intent.getStringExtra("productId") ?: ""

        // Initialize Firestore and Storage references
        firestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        // Load product details from Firestore and display in the UI
        loadProductDetails()

        productImageView.setOnClickListener {
            openImageChooser()
        }

        updateButton.setOnClickListener {
            // Update the product details in Firestore
            updateProduct()
        }

    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun loadProductDetails() {
        // Query the product details from Firestore and update the UI
        firestore.collection("products").document(productId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val product = documentSnapshot.toObject(Product::class.java)
                    product?.let {
                        productNameEditText.setText(it.name)
                        productPriceEditText.setText(it.price.toString())
                        productDescriptionEditText.setText(it.description)
                        Picasso.get().load(it.imageUrl).into(productImageView)
                    }
                }
            }
            .addOnFailureListener {
                // Handle the failure case
            }
    }

    private fun updateProduct() {
        val productName = productNameEditText.text.toString().trim()
        val productPrice = productPriceEditText.text.toString().trim()
        val productDescription = productDescriptionEditText.text.toString().trim()

        if (productName.isEmpty() || productPrice.isEmpty() || productDescription.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedProduct = hashMapOf<String, Any>(
            "name" to productName,
            "price" to productPrice.toDouble(),
            "description" to productDescription
        )

        // Check if a new image is selected, and upload it to Firebase Storage
        if (selectedImageUri != null) {
            val productImageFileName = productId + ".jpg"
            val imageRef = storageReference.child("product_images/$productImageFileName")

            imageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    // Get the download URL of the uploaded image
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        updatedProduct["imageUrl"] = uri.toString()
                        // Update the product in Firestore with the new image URL
                        updateProductInFirestore(updatedProduct)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to upload image.", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Update the product in Firestore without changing the image URL
            updateProductInFirestore(updatedProduct)
        }
    }

    private fun updateProductInFirestore(updatedProduct: HashMap<String, Any>) {
        firestore.collection("products").document(productId)
            .update(updatedProduct)
            .addOnSuccessListener {
                Toast.makeText(this, "Product updated successfully.", Toast.LENGTH_SHORT).show()
//                finish()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update product.", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            Picasso.get().load(selectedImageUri).into(productImageView)
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

}