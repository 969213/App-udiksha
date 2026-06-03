package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShopRepository(
    private val productDao: ProductDao,
    private val orderDao: OrderDao,
    private val workerDao: WorkerDao,
    private val adminConfigDao: AdminConfigDao
) {
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val allOrders: Flow<List<Order>> = orderDao.getAllOrders()
    val allWorkers: Flow<List<Worker>> = workerDao.getAllWorkers()

    suspend fun getProductById(id: Int): Product? = withContext(Dispatchers.IO) {
        productDao.getProductById(id)
    }

    suspend fun insertProduct(product: Product) = withContext(Dispatchers.IO) {
        productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: Product) = withContext(Dispatchers.IO) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(product: Product) = withContext(Dispatchers.IO) {
        productDao.deleteProduct(product)
    }

    suspend fun deleteProductById(id: Int) = withContext(Dispatchers.IO) {
        productDao.deleteById(id)
    }

    suspend fun insertOrder(order: Order) = withContext(Dispatchers.IO) {
        orderDao.insertOrder(order)
    }

    suspend fun updateOrder(order: Order) = withContext(Dispatchers.IO) {
        orderDao.updateOrder(order)
    }

    suspend fun insertWorker(worker: Worker) = withContext(Dispatchers.IO) {
        workerDao.insertWorker(worker)
    }

    suspend fun deleteWorkerById(id: Int) = withContext(Dispatchers.IO) {
        workerDao.deleteById(id)
    }

    suspend fun getUpiId(): String = withContext(Dispatchers.IO) {
        adminConfigDao.getConfig("upi_id")?.value ?: "mbhola099@okaxis" // default upi id
    }

    suspend fun saveUpiId(upi: String) = withContext(Dispatchers.IO) {
        adminConfigDao.saveConfig(AdminConfig("upi_id", upi))
    }

    suspend fun getUpiMerchantName(): String = withContext(Dispatchers.IO) {
        adminConfigDao.getConfig("upi_merchant")?.value ?: "Shivendra Mishra"
    }

    suspend fun saveUpiMerchantName(name: String) = withContext(Dispatchers.IO) {
        adminConfigDao.saveConfig(AdminConfig("upi_merchant", name))
    }

    suspend fun isTeamShowcaseEnabled(): Boolean = withContext(Dispatchers.IO) {
        adminConfigDao.getConfig("team_showcase")?.value?.toBoolean() ?: true
    }

    suspend fun setTeamShowcaseEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        adminConfigDao.saveConfig(AdminConfig("team_showcase", enabled.toString()))
    }

    // Function to populate initial products and workers if empty
    suspend fun prepopulateIfEmpty() = withContext(Dispatchers.IO) {
        // We will call this from our ViewModel or App init
        // Prepopulate Products
        if (productDao.getProductCount() == 0) {
            val defaultProducts = listOf(
                Product(
                    title = "Royal Banarasi Silk Saree",
                    description = "Pure georgette Banarasi saree featuring antique gold zari work borders, intricate floral motifs, and a stunning heavy pallu. Perfect for premium festivals and bridal wear.",
                    price = 4999.0,
                    discountedPrice = 3499.0,
                    category = "Sarees",
                    sizes = "Free Size",
                    colors = "Royal Maroon, Velvet Red, Golden Glow",
                    imageUrl = "saree_banarasi",
                    availableStock = 12
                ),
                Product(
                    title = "Deep Burgundy Chikankari Lehenga",
                    description = "A breathtaking designer lehenga choli layered with exquisite Lucknowi Chikankari embroidery and sequin embellishments on premium soft georgette. Paired with an elegant matching net dupatta.",
                    price = 14999.0,
                    discountedPrice = 9999.0,
                    category = "Lehengas",
                    sizes = "S, M, L, XL",
                    colors = "Burgundy, Cherry Red, Antique Purple",
                    imageUrl = "lehenga_chikankari",
                    availableStock = 5
                ),
                Product(
                    title = "Embroidered Silk Sherwani Set",
                    description = "Premium Indo-Western ivory white sherwani meticulously hand-embroidered with classic zardosi and dori work. Complemented by a matching luxurious churidar pyjama.",
                    price = 12999.0,
                    discountedPrice = 8499.0,
                    category = "Mens Wear",
                    sizes = "M, L, XL, XXL",
                    colors = "Ivory Cream, Champagne Gold",
                    imageUrl = "sherwani_silk",
                    availableStock = 8
                ),
                Product(
                    title = "Lucknowi Chikankari Georgette Kurti",
                    description = "An absolute masterpiece of comfort and craftsmanship. Features fine hand-worked white Lucknowi Chikankari embroidery all over the yoke and body, ideal for stylish semi-formal gatherings.",
                    price = 2499.0,
                    discountedPrice = 1799.0,
                    category = "Kurtis",
                    sizes = "S, M, L, XL, XXL",
                    colors = "Sky Blue, Mint Green, Peach Punch",
                    imageUrl = "kurti_chikankari",
                    availableStock = 25
                ),
                Product(
                    title = "Royal Premium Kurta & Bundi Jacket",
                    description = "Three-piece ethnic jacket kurta pajama set for men. Comprises a pure linen kurta paired with a rich banarasi silk jacquard Nehru bundi jacket with gold patterns.",
                    price = 4499.0,
                    discountedPrice = 2999.0,
                    category = "Mens Wear",
                    sizes = "S, M, L, XL",
                    colors = "Royal Blue, Amber Gold, Forest Green",
                    imageUrl = "kurta_jacket",
                    availableStock = 15
                ),
                Product(
                    title = "Designer Silk Anarkali Suit Set",
                    description = "A grand heavy-gflare Anarkali gown crafted from rich Chanderi silk. Designed with a lavish hand-woven neckline and matched with soft pants and a floral print organza dupatta.",
                    price = 5999.0,
                    discountedPrice = 3999.0,
                    category = "Anarkali Suits",
                    sizes = "S, M, L, XL",
                    colors = "Teal Blue, Emerald Green, Mustard Yellow",
                    imageUrl = "anarkali_silk",
                    availableStock = 9
                ),
                Product(
                    title = "Kids Traditional Dhoti Kurta Set",
                    description = "Extremely soft children's cotton dhoti paired with an attractive jacquard-woven yellow kurta. Non-irritant material ideal for pujas, wedding events, and festivals.",
                    price = 1799.0,
                    discountedPrice = 1199.0,
                    category = "Kids Wear",
                    sizes = "2-3Y, 4-5Y, 6-7Y",
                    colors = "Sun Yellow, Royal Saffron",
                    imageUrl = "kids_dhoti",
                    availableStock = 18
                )
            )
            for (p in defaultProducts) {
                productDao.insertProduct(p)
            }
        }

        // Prepopulate Workers
        if (workerDao.getWorkerCount() == 0) {
            val defaultWorkers = listOf(
                Worker(
                    name = "Shivendra Mishra",
                    role = "Shop Owner & General Manager",
                    age = 38,
                    gender = "Male",
                    avatarIndex = 1,
                    isShowcased = true
                ),
                Worker(
                    name = "Harsh Mishra",
                    role = "Lead App Architect & Tech Advisor",
                    age = 26,
                    gender = "Male",
                    avatarIndex = 2,
                    isShowcased = true
                ),
                Worker(
                    name = "Suresh Tailor",
                    role = "Master Designer & Embroidery Head",
                    age = 45,
                    gender = "Male",
                    avatarIndex = 3,
                    isShowcased = true
                ),
                Worker(
                    name = "Kirti Sharma",
                    role = "Sales & Customer Styling Head",
                    age = 29,
                    gender = "Female",
                    avatarIndex = 4,
                    isShowcased = true
                )
            )
            for (w in defaultWorkers) {
                workerDao.insertWorker(w)
            }
        }
    }

    // Helper completed successfully
}
