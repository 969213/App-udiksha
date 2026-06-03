package com.example.ui

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

data class CartItem(
    val product: Product,
    val size: String,
    val color: String,
    val quantity: Int = 1
)

data class UserProfile(
    val name: String,
    val phoneOrEmail: String,
    val age: Int,
    val gender: String,
    val role: String // "BUYER", "ADMIN", "WORKER"
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    val repository = ShopRepository(
        database.productDao(),
        database.orderDao(),
        database.workerDao(),
        database.adminConfigDao()
    )

    // Reactive lists from Database
    val productsFlow: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ordersFlow: StateFlow<List<Order>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workersFlow: StateFlow<List<Worker>> = repository.allWorkers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Session States
    var selectedCategory by mutableStateOf("All")
    var searchQuery by mutableStateOf("")
    var currentProductDetail by mutableStateOf<Product?>(null)
    val cart = mutableStateListOf<CartItem>()

    // Current User Session State
    var currentUser by mutableStateOf<UserProfile?>(null)
    var isLoggedIn by mutableStateOf(false)

    // Configuration Settings
    var upiId by mutableStateOf("mbhola099@okaxis")
    var upiMerchantName by mutableStateOf("Shivendra Mishra")
    var isTeamShowcaseEnabled by mutableStateOf(true)

    // Verification and OTP states
    var verificationPhoneOrEmail by mutableStateOf("")
    var generatedOtp by mutableStateOf("")
    var enteredOtp by mutableStateOf("")
    var isOtpSent by mutableStateOf(false)
    var showAuthDialog by mutableStateOf(false)
    var authActionOnSuccess: (() -> Unit)? = null

    // For Admin / Super Admin Login
    var isAdminVerified by mutableStateOf(false)

    init {
        viewModelScope.launch {
            // First prepopulate initial details
            try {
                repository.prepopulateIfEmpty()
                // Load configurations
                upiId = repository.getUpiId()
                upiMerchantName = repository.getUpiMerchantName()
                isTeamShowcaseEnabled = repository.isTeamShowcaseEnabled()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error prepopulating: ${e.message}")
            }
        }
    }

    // Refresh Configuration constants from DB
    fun refreshConfig() {
        viewModelScope.launch {
            upiId = repository.getUpiId()
            upiMerchantName = repository.getUpiMerchantName()
            isTeamShowcaseEnabled = repository.isTeamShowcaseEnabled()
        }
    }

    // Cart Management
    fun addToCart(product: Product, size: String, color: String, quantity: Int = 1) {
        val existing = cart.find { it.product.id == product.id && it.size == size && it.color == color }
        if (existing != null) {
            val index = cart.indexOf(existing)
            cart[index] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            cart.add(CartItem(product, size, color, quantity))
        }
    }

    fun removeFromCart(item: CartItem) {
        cart.remove(item)
    }

    fun updateCartQuantity(item: CartItem, delta: Int) {
        val index = cart.indexOf(item)
        if (index != -1) {
            val newQty = item.quantity + delta
            if (newQty > 0) {
                cart[index] = item.copy(quantity = newQty)
            } else {
                cart.removeAt(index)
            }
        }
    }

    fun clearCart() {
        cart.clear()
    }

    // Calculations
    val cartSubtotal: Double
        get() = cart.sumOf { it.product.discountedPrice * it.quantity }

    // User Logins and OTPs
    fun initiateLogin(identifier: String, onSuccessAction: (() -> Unit)? = null) {
        val cleaned = identifier.trim()
        if (cleaned.isEmpty()) return

        verificationPhoneOrEmail = cleaned
        authActionOnSuccess = onSuccessAction

        if (cleaned.equals("mbhola099@gmail.com", ignoreCase = true)) {
            // Trigger Admin Verification OTP Flow
            val otpCode = Random.nextInt(100000, 999999).toString()
            generatedOtp = otpCode
            isOtpSent = true
            Log.d("AuthFlow", "Admin Passcode generated: $otpCode sending to $cleaned")
        } else {
            // Standard Customer OTP
            val otpCode = Random.nextInt(100000, 999999).toString()
            generatedOtp = otpCode
            isOtpSent = true
            Log.d("AuthFlow", "Customer OTP generated: $otpCode sending to $cleaned")
        }
    }

    fun verifyOtp(otp: String, name: String, age: String, gender: String): Boolean {
        if (otp == generatedOtp) {
            val role = if (verificationPhoneOrEmail.equals("mbhola099@gmail.com", ignoreCase = true)) {
                isAdminVerified = true
                "ADMIN"
            } else {
                "BUYER"
            }

            currentUser = UserProfile(
                name = name.ifEmpty { "Guest Buyer" },
                phoneOrEmail = verificationPhoneOrEmail,
                age = age.toIntOrNull() ?: 25,
                gender = gender.ifEmpty { "Not Specified" },
                role = role
            )
            isLoggedIn = true
            isOtpSent = false
            showAuthDialog = false

            // Execute post-auth delayed action if any (like proceeding with order)
            authActionOnSuccess?.invoke()
            authActionOnSuccess = null
            return true
        }
        return false
    }

    fun logout() {
        currentUser = null
        isLoggedIn = false
        isAdminVerified = false
        verificationPhoneOrEmail = ""
        generatedOtp = ""
        enteredOtp = ""
        isOtpSent = false
    }

    // Order checkout logic
    fun placeOrder(
        buyerName: String,
        buyerPhone: String,
        buyerAddress: String,
        buyerAge: Int,
        buyerGender: String,
        paymentStatus: String = "PENDING",
        transactionId: String? = null,
        onOrderDone: (Order) -> Unit
    ) {
        if (cart.isEmpty()) return

        viewModelScope.launch {
            // Place one overall order bundle or individual ledger entries
            // For ledger records, we record per product order
            val orderNo = "UD-${System.currentTimeMillis().toString().takeLast(6)}-${Random.nextInt(100, 999)}"
            for (item in cart) {
                val orderRecord = Order(
                    orderNumber = orderNo,
                    productId = item.product.id,
                    productTitle = item.product.title,
                    productPrice = item.product.discountedPrice * item.quantity,
                    buyerName = buyerName,
                    buyerPhone = buyerPhone,
                    buyerAddress = buyerAddress,
                    buyerAge = buyerAge,
                    buyerGender = buyerGender,
                    paymentStatus = paymentStatus,
                    transactionId = transactionId
                )
                repository.insertOrder(orderRecord)

                // Deduct stock of the items
                val updatedProduct = item.product.copy(
                    availableStock = (item.product.availableStock - item.quantity).coerceAtLeast(0)
                )
                repository.updateProduct(updatedProduct)

                // Callback for triggers (e.g. WhatsApp deep link)
                if (item == cart.first()) {
                    onOrderDone(orderRecord)
                }
            }
            clearCart()
        }
    }

    // Admin commands: Toggle Payment order status
    fun updateOrderStatus(order: Order, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrder(order.copy(paymentStatus = newStatus))
        }
    }

    // Admin product control (CMS)
    fun addProduct(product: Product) {
        viewModelScope.launch {
            repository.insertProduct(product)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    // Admin staff addition
    fun addWorker(worker: Worker) {
        viewModelScope.launch {
            repository.insertWorker(worker)
        }
    }

    fun deleteWorker(worker: Worker) {
        viewModelScope.launch {
            repository.deleteWorkerById(worker.id)
        }
    }

    // Configuration controllers
    fun updateUpiSettings(newId: String, newMerchant: String) {
        viewModelScope.launch {
            repository.saveUpiId(newId)
            repository.saveUpiMerchantName(newMerchant)
            refreshConfig()
        }
    }

    fun toggleTeamShowcase(enabled: Boolean) {
        viewModelScope.launch {
            repository.setTeamShowcaseEnabled(enabled)
            refreshConfig()
        }
    }

    // Factory creation
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
