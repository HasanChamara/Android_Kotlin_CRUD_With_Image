package com.example.kotlincrudwithimage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ProductAdapter(private val productList: List<Product>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(product: Product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]
        println("ProductAdapter: Product ID = ${product.id}") // Debugging statement
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageViewProduct: ImageView = itemView.findViewById(R.id.imageViewProduct)
        private val textViewProductName: TextView = itemView.findViewById(R.id.textViewProductName)
        private val textViewProductPrice: TextView = itemView.findViewById(R.id.textViewProductPrice)

        fun bind(product: Product) {
            textViewProductName.text = product.name
            textViewProductPrice.text = "$" + product.price.toString()

            // Load the product image using Picasso
            Picasso.get().load(product.imageUrl).into(imageViewProduct)

            // Set click listener on the item view
            itemView.setOnClickListener {
                listener.onItemClick(product)
            }
        }
    }
}