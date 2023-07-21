package com.example.kotlincrudwithimage
import com.google.firebase.firestore.IgnoreExtraProperties
//data class Product(
//    val id: String? = null,
//    val name: String? = null,
//    val price: Double? = null,
//    val description: String? = null,
//    val imageUrl: String? = null
//)

@IgnoreExtraProperties
data class Product(
    var id: String = "",
    var name: String = "",
    var price: Double = 0.0,
    var description: String = "",
    var imageUrl: String = ""
)