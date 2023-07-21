package com.example.kotlincrudwithimage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity(), ProductAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var firestore: FirebaseFirestore

    private val productList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        productAdapter = ProductAdapter(productList, this)
        recyclerView.adapter = productAdapter



        // Load products from Firestore
        loadProducts()

    }

    private fun loadProducts() {
        firestore.collection("products")
            .get()
            .addOnSuccessListener { querySnapshot ->
                productList.clear()
                for (documentSnapshot in querySnapshot) {
                    val product = documentSnapshot.toObject(Product::class.java)
                    productList.add(product)
                }
                productAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                // Handle the failure case
            }
    }

//    override fun onItemClick(position: Int) {
//        // Open ProductDetailsActivity with the selected product ID
//        val product = productList[position]
//        val productId = product.id
//        println("MainActivity: Product ID = $productId") // Debugging statement
//        val intent = Intent(this, ProductDetailsActivity::class.java)
//        intent.putExtra("productId", product.id)
//        startActivity(intent)
//    }

    override fun onItemClick(product: Product) {
        // Open ProductDetailsActivity with the selected product ID
        val productId = product.id
        println("MainActivity: Product ID = $productId") // Debugging statement
        val intent = Intent(this, ProductDetailsActivity::class.java)
        intent.putExtra("productId", product.id)
        startActivity(intent)
    }


}