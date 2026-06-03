package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Order
import com.example.data.Product
import com.example.data.Worker
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf("Inventory") } // Inventory, Staff, Payments, Analytics
    val products by viewModel.productsFlow.collectAsState()
    val workers by viewModel.workersFlow.collectAsState()
    val orders by viewModel.ordersFlow.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFBF8FF))
            .statusBarsPadding()
    ) {
        // App header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF4F46E5))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Boutique Admin Panel",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "Super-Admin: mbhola099@gmail.com",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // Horizontal tabs selectors
        TabRow(
            selectedTabIndex = getTabIndex(activeTab),
            containerColor = Color.White,
            contentColor = Color(0xFF4F46E5),
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(selected = activeTab == "Inventory", onClick = { activeTab = "Inventory" }) {
                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Inventory, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(18.dp))
                    Text("Stock(CMS)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Tab(selected = activeTab == "Staff", onClick = { activeTab = "Staff" }) {
                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Badge, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(18.dp))
                    Text("Staff", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Tab(selected = activeTab == "Payments", onClick = { activeTab = "Payments" }) {
                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(18.dp))
                    Text("Orders", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Tab(selected = activeTab == "Analytics", onClick = { activeTab = "Analytics" }) {
                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Leaderboard, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(18.dp))
                    Text("Analytics", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Content panel
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeTab) {
                "Inventory" -> InventoryCMSPanel(viewModel, products)
                "Staff" -> StaffAdministrationPanel(viewModel, workers)
                "Payments" -> PaymentLedgerVerificationPanel(viewModel, orders)
                "Analytics" -> SalesAnalyticsDashboardPanel(viewModel, orders, products)
            }
        }
    }
}

fun getTabIndex(tab: String): Int {
    return when (tab) {
        "Inventory" -> 0
        "Staff" -> 1
        "Payments" -> 2
        "Analytics" -> 3
        else -> 0
    }
}

// ---------------- INVENTORY PANEL (CMS) ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryCMSPanel(viewModel: MainViewModel, products: List<Product>) {
    var showAddDialog by remember { mutableStateOf(false) }

    // Forms field
    var itemTitle by remember { mutableStateOf("") }
    var itemDesc by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }
    var itemDiscPrice by remember { mutableStateOf("") }
    var itemCategory by remember { mutableStateOf("Sarees") }
    var itemStock by remember { mutableStateOf("10") }
    var itemSizes by remember { mutableStateOf("S, M, L, XL") }
    var itemColors by remember { mutableStateOf("Maroon, Red, Gold") }

    val categoriesList = listOf("Sarees", "Kurtis", "Lehengas", "Anarkali Suits", "Mens Wear", "Kids Wear")
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cloth Store Catalog (${products.size} items)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                        modifier = Modifier.testTag("admin_add_product_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Cloth", fontSize = 12.sp)
                    }
                }
            }

            if (products.isEmpty()) {
                item {
                    Text(
                        text = "Catalog empty. Please add ethnic clothes.",
                        modifier = Modifier.padding(32.dp),
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            } else {
                items(products) { p ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                             Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF4F46E5)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = getProductIcon(p.category), contentDescription = null, tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = p.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "Category: ${p.category}  |  Stock: ${p.availableStock}", fontSize = 12.sp, color = Color.Gray)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(text = "₹${p.discountedPrice.toInt()}", color = Color(0xFF4F46E5), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = "M.R.P: ₹${p.price.toInt()}", color = Color.Gray, textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough, fontSize = 11.sp)
                                }
                            }
                            IconButton(onClick = { viewModel.deleteProduct(p) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete item", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }

        // Add Product Form Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add New Apparel", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif) },
                text = {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = itemTitle,
                            onValueChange = { itemTitle = it },
                            label = { Text("Product Title") },
                            modifier = Modifier.testTag("add_title_field")
                        )
                        OutlinedTextField(
                            value = itemDesc,
                            onValueChange = { itemDesc = it },
                            label = { Text("Detailed Description") }
                        )

                        // Category Dropdown simulated
                        Text("Category Selection:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            categoriesList.take(3).forEach { cat ->
                                FilterChip(
                                    selected = itemCategory == cat,
                                    onClick = { itemCategory = cat },
                                    label = { Text(cat, fontSize = 9.sp) }
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            categoriesList.drop(3).forEach { cat ->
                                FilterChip(
                                    selected = itemCategory == cat,
                                    onClick = { itemCategory = cat },
                                    label = { Text(cat, fontSize = 9.sp) }
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = itemPrice,
                                onValueChange = { itemPrice = it },
                                label = { Text("M.R.P (₹)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = itemDiscPrice,
                                onValueChange = { itemDiscPrice = it },
                                label = { Text("Sale Price (₹)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = itemStock,
                                onValueChange = { itemStock = it },
                                label = { Text("Initial Stock") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = itemSizes,
                                onValueChange = { itemSizes = it },
                                label = { Text("Sizes (comma sep)") },
                                modifier = Modifier.weight(1.5f)
                            )
                        }

                        OutlinedTextField(
                            value = itemColors,
                            onValueChange = { itemColors = it },
                            label = { Text("Color variants (comma sep)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val priceVal = itemPrice.toDoubleOrNull() ?: 0.0
                            val discVal = itemDiscPrice.toDoubleOrNull() ?: priceVal
                            val stockVal = itemStock.toIntOrNull() ?: 10

                            if (itemTitle.trim().isEmpty() || priceVal <= 0.0) {
                                Toast.makeText(context, "Please complete Title and Price.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val newP = Product(
                                title = itemTitle,
                                description = itemDesc.ifEmpty { "High-quality woven ethnic wear apparel." },
                                price = priceVal,
                                discountedPrice = discVal,
                                imageUrl = "custom_draw",
                                category = itemCategory,
                                sizes = itemSizes,
                                colors = itemColors,
                                availableStock = stockVal
                            )
                            viewModel.addProduct(newP)

                            // Clear forms
                            itemTitle = ""
                            itemDesc = ""
                            itemPrice = ""
                            itemDiscPrice = ""
                            itemStock = "10"
                            showAddDialog = false
                            Toast.makeText(context, "Product listed successfully on the store Catalog!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                    ) {
                        Text("Publish")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

// ---------------- STAFF PANEL ----------------
@Composable
fun StaffAdministrationPanel(viewModel: MainViewModel, workers: List<Worker>) {
    var showAddWorkerDialog by remember { mutableStateOf(false) }
    var wName by remember { mutableStateOf("") }
    var wRole by remember { mutableStateOf("") }
    var wAge by remember { mutableStateOf("28") }
    var wGender by remember { mutableStateOf("Male") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Showcase Toggle Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Customer Team Showcase", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "Allow buyers to view 'Our Team' screen highlights.", fontSize = 12.sp, color = Color.Gray)
                }
                Switch(
                    checked = viewModel.isTeamShowcaseEnabled,
                    onCheckedChange = { viewModel.toggleTeamShowcase(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF4F46E5), checkedTrackColor = Color(0xFFEC4899))
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Worker list area
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Active Staff Members (${workers.size})", fontWeight = FontWeight.Bold, color = Color.Black)
            Button(
                onClick = { showAddWorkerDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B8067))
            ) {
                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Hire Staff", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (workers.isEmpty()) {
                item {
                    Text("No staff registered. Press Hire Staff to insert profiles.", modifier = Modifier.padding(24.dp), color = Color.Gray, textAlign = TextAlign.Center)
                }
            } else {
                items(workers) { w ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1B8067)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(w.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(w.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${w.role}  |  Age: ${w.age} (${w.gender})", fontSize = 12.sp, color = Color.Gray)
                            }
                            IconButton(onClick = { viewModel.deleteWorker(w) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Fire", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }

        if (showAddWorkerDialog) {
            AlertDialog(
                onDismissRequest = { showAddWorkerDialog = false },
                title = { Text("Hire Staff Profile", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = wName,
                            onValueChange = { wName = it },
                            label = { Text("Worker Full Name") },
                            modifier = Modifier.testTag("worker_name_field")
                        )
                        OutlinedTextField(
                            value = wRole,
                            onValueChange = { wRole = it },
                            label = { Text("Job Designation / Role") }
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = wAge,
                                onValueChange = { wAge = it },
                                label = { Text("Age") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = wGender,
                                onValueChange = { wGender = it },
                                label = { Text("Gender") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (wName.trim().isEmpty() || wRole.trim().isEmpty()) {
                                Toast.makeText(context, "Complete Name and Role fields.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val workerObj = Worker(
                                name = wName,
                                role = wRole,
                                age = wAge.toIntOrNull() ?: 28,
                                gender = wGender,
                                avatarIndex = (0..5).random(),
                                isShowcased = true
                            )
                            viewModel.addWorker(workerObj)

                            wName = ""
                            wRole = ""
                            showAddWorkerDialog = false
                            Toast.makeText(context, "New worker onboarded successfully!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B8067))
                    ) {
                        Text("Add to Team")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddWorkerDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

// ---------------- PAYMENT VERIFICATION & ORDERS LEDGER ----------------
@Composable
fun PaymentLedgerVerificationPanel(viewModel: MainViewModel, orders: List<Order>) {
    var upiIdVal by remember { mutableStateOf(viewModel.upiId) }
    var merchantVal by remember { mutableStateOf(viewModel.upiMerchantName) }
    var showUpiDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Quick QR Config Area
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Boutique Payment Account Details", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "UPI: $upiIdVal  |  Merchant: $merchantVal", fontSize = 11.sp, color = Color.Gray)
                }
                IconButton(onClick = { showUpiDialog = true }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit UPI info", tint = Color(0xFF4F46E5))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Transactions Ledger Verification Window", fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (orders.isEmpty()) {
                item {
                    Text("No transactions logged in payment tables yet.", modifier = Modifier.padding(24.dp), color = Color.Gray)
                }
            } else {
                items(orders) { order ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Receipt: #${order.orderNumber}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF4F46E5))
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (order.paymentStatus == "SUCCESS") Color(0xFFE2F0D9) else Color(0xFFFFF2CC),
                                            RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = order.paymentStatus,
                                        color = if (order.paymentStatus == "SUCCESS") Color(0xFF385723) else Color(0xFFB25E00),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "Outfit: ${order.productTitle}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Buyer: ${order.buyerName} (${order.buyerPhone})", fontSize = 12.sp, color = Color.DarkGray)
                            Text(text = "Shipping Address: ${order.buyerAddress}", fontSize = 12.sp, color = Color.Gray)
                            order.transactionId?.let { ref ->
                                Text(
                                    text = "Submitted Cash Ref/UTR: $ref",
                                    fontSize = 12.sp,
                                    color = Color.Blue,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = Color.LightGray.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Sum Booked: ₹${order.productPrice.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)

                                if (order.paymentStatus == "PENDING") {
                                    Button(
                                        onClick = {
                                            viewModel.updateOrderStatus(order, "SUCCESS")
                                            Toast.makeText(context, "Order payment approved & successful!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF385723)),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Approve Payment", fontSize = 10.sp)
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            viewModel.updateOrderStatus(order, "PENDING")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Mark Pending", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showUpiDialog) {
            AlertDialog(
                onDismissRequest = { showUpiDialog = false },
                title = { Text("Update Merchant UPI info") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = upiIdVal,
                            onValueChange = { upiIdVal = it },
                            label = { Text("Store Payment UPI ID") }
                        )
                        OutlinedTextField(
                            value = merchantVal,
                            onValueChange = { merchantVal = it },
                            label = { Text("Merchant/Shop Registered Name") }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (upiIdVal.trim().isNotEmpty() && merchantVal.trim().isNotEmpty()) {
                                viewModel.updateUpiSettings(upiIdVal, merchantVal)
                                showUpiDialog = false
                                Toast.makeText(context, "UPI configs saved securely. Checks updated instantly.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                    ) {
                        Text("Save UPISettings")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showUpiDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

// ---------------- SALES ANALYTICS PANEL ----------------
@Composable
fun SalesAnalyticsDashboardPanel(
    viewModel: MainViewModel,
    orders: List<Order>,
    products: List<Product>
) {
    val totalRevenue = orders.filter { it.paymentStatus == "SUCCESS" }.sumOf { it.productPrice }
    val pendingRevenue = orders.filter { it.paymentStatus == "PENDING" }.sumOf { it.productPrice }
    val totalOrdersCount = orders.size
    val totalStockSum = products.sumOf { it.availableStock }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(text = "Real-Time Ledger Analytics", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp, fontFamily = FontFamily.Serif)
        Spacer(modifier = Modifier.height(14.dp))

        // Large highlight total revenue card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "TOTAL REVENUE RECEIVED", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = "₹${totalRevenue.toInt()}", color = Color(0xFFEC4899), fontSize = 32.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Serif)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Pending Claims: ₹${pendingRevenue.toInt()}", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                    Text(text = "$totalOrdersCount orders placed", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Side by side metric blocks
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Total App Listings", color = Color.Gray, fontSize = 11.sp)
                    Text("${products.size} Products", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Active Store Stock", color = Color.Gray, fontSize = 11.sp)
                    Text("$totalStockSum garments", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (totalStockSum > 10) Color(0xFF385723) else Color.Red)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Monthly Clothing Sales Trend Chart", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))

        // Custom canvas line graph
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Draw static chart guide grid lines
                    val gridLines = 4
                    for (i in 0..gridLines) {
                        val y = (height / gridLines) * i
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.4f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Simulated database ledger trend coordinates
                    val dataPoints = listOf(0.1f, 0.25f, 0.18f, 0.45f, 0.35f, 0.72f, 0.55f, 0.90f)
                    val stepX = width / (dataPoints.size - 1)

                    // Draw gradient line
                    val pathOfChart = androidx.compose.ui.graphics.Path()
                    for (index in dataPoints.indices) {
                        val x = stepX * index
                        val y = height - (dataPoints[index] * height)
                        if (index == 0) {
                            pathOfChart.moveTo(x, y)
                        } else {
                            pathOfChart.lineTo(x, y)
                        }
                    }

                    // Stroke line
                    drawPath(
                        path = pathOfChart,
                        color = Color(0xFF4F46E5),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw glowing gold bubbles on vertices
                    for (index in dataPoints.indices) {
                        val x = stepX * index
                        val y = height - (dataPoints[index] * height)
                        drawCircle(
                            color = Color(0xFFEC4899),
                            radius = 4.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }
            }
        }

        // Running ledger table lists
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Unified Sales Ledger Records", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF4F46E5).copy(alpha = 0.1f))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Outfits Booked", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.5f))
                    Text("Recipient", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    Text("Total", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(0.8f))
                }

                if (orders.isEmpty()) {
                    Text("No orders placed yet.", modifier = Modifier.padding(12.dp), color = Color.Gray, fontSize = 12.sp)
                } else {
                    orders.take(5).forEach { ord ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(ord.productTitle, fontSize = 12.sp, modifier = Modifier.weight(1.5f), maxLines = 1)
                            Text(ord.buyerName, fontSize = 12.sp, modifier = Modifier.weight(1f), maxLines = 1)
                            Text("₹${ord.productPrice.toInt()}", fontSize = 12.sp, modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold)
                        }
                        Divider(color = Color.LightGray.copy(alpha = 0.3f))
                    }
                }
            }
        }
    }
}
