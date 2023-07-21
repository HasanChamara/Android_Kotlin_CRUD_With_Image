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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class ProductInsertActivity : AppCompatActivity() {

    private lateinit var productNameEditText: EditText
    private lateinit var productPriceEditText: EditText
    private lateinit var productDescriptionEditText: EditText
    private lateinit var productImageView: ImageView
    private lateinit var insertButton: Button
    private lateinit var buttonProductGrid: Button
    private var selectedImageUri: Uri? = null // Initialize as null

    // Firebase variables
    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_insert)

        productNameEditText = findViewById(R.id.editTextProductName)
        productPriceEditText = findViewById(R.id.editTextProductPrice)
        productDescriptionEditText = findViewById(R.id.editTextProductDescription)
        productImageView = findViewById(R.id.imageViewProduct)
        insertButton = findViewById(R.id.buttonInsert)
        buttonProductGrid = findViewById(R.id.buttonProductGrid)

        // Initialize Firebase Storage and Firestore references
        storageReference = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()

        productImageView.setOnClickListener {
            openImageChooser()
        }

        insertButton.setOnClickListener {
            insertProduct()
        }

        buttonProductGrid.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            productImageView.setImageURI(selectedImageUri) // Display the selected image in the ImageView
        }
    }

    private fun insertProduct() {
        val productName = productNameEditText.text.toString().trim()
        val productPrice = productPriceEditText.text.toString().trim()
        val productDescription = productDescriptionEditText.text.toString().trim()

        if (productName.isEmpty() || productPrice.isEmpty() || productDescription.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select an image.", Toast.LENGTH_SHORT).show()
            return
        }

        val productImageFileName = UUID.randomUUID().toString() + ".jpg"
        val imageRef = storageReference.child("product_images/$productImageFileName")

        imageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL of the uploaded image
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val productId = UUID.randomUUID().toString() // Generate a unique ID
                    val product = Product(
                        id = productId,
                        name = productName,
                        price = productPrice.toDouble(),
                        description = productDescription,
                        imageUrl = uri.toString()
                    )

                    // Save the product to Firestore with the generated ID
                    firestore.collection("products")
                        .document(productId)
                        .set(product)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Product inserted successfully.", Toast.LENGTH_SHORT).show()
//                            clearFields()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to insert product.", Toast.LENGTH_SHORT).show()
                            clearFields()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image.", Toast.LENGTH_SHORT).show()
            }
    }


    private fun clearFields() {
        productNameEditText.text.clear()
        productPriceEditText.text.clear()
        productDescriptionEditText.text.clear()
        productImageView.setImageResource(R.drawable.placeholder_image)
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

}