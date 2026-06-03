package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val price: Double,
    val discountedPrice: Double,
    val imageUrl: String, // Can be a color hex, a drawable name, or a url
    val category: String,
    val sizes: String = "S,M,L,XL",
    val colors: String = "Maroon,Gold,Red,Pink",
    val availableStock: Int = 10,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderNumber: String,
    val productId: Int,
    val productTitle: String,
    val productPrice: Double,
    val buyerName: String,
    val buyerPhone: String,
    val buyerAddress: String,
    val buyerAge: Int = 25,
    val buyerGender: String = "Female",
    val paymentStatus: String = "PENDING", // PENDING, SUCCESS, FAILED
    val transactionId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "workers")
data class Worker(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val role: String,
    val age: Int,
    val gender: String,
    val avatarIndex: Int = 0, // Maps to a cute emoji or design placeholder
    val isShowcased: Boolean = true
)

@Entity(tableName = "admin_config")
data class AdminConfig(
    @PrimaryKey val key: String,
    val value: String
)
