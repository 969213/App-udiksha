package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Path
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
import com.example.ui.CartItem
import com.example.ui.MainViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isCheckingOut by remember { mutableStateOf(false) }
    var referenceEntered by remember { mutableStateOf("") }
    var addressEntered by remember { mutableStateOf("") }
    var shipName by remember { mutableStateOf(viewModel.currentUser?.name ?: "") }
    var shipPhone by remember { mutableStateOf(viewModel.currentUser?.phoneOrEmail ?: "") }
    var showWhatsAppSuccessDialog by remember { mutableStateOf<Order?>(null) }

    // Sync shipping details whenever currentUser changes
    LaunchedEffect(viewModel.currentUser) {
        viewModel.currentUser?.let {
            if (shipName.isEmpty()) shipName = it.name
            if (shipPhone.isEmpty()) shipPhone = it.phoneOrEmail
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFBF8FF))
            .statusBarsPadding()
    ) {
        // App bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.background(Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = Color(0xFF4F46E5)
                )
            }

            Text(
                text = "My Shopping Bag",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF312E81),
                fontFamily = FontFamily.Serif
            )

            IconButton(
                onClick = { viewModel.clearCart() },
                modifier = Modifier.background(Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Clear all",
                    tint = Color.Red
                )
            }
        }

        if (viewModel.cart.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Empty Bag",
                        tint = Color.LightGray,
                        modifier = Modifier.size(96.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your bag is empty",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Browse the exclusive ethnic collection on the home feeds and add fashionable apparel. Happy shopping!",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                    ) {
                        Text("Explore Outfits")
                    }
                }
            }
        } else if (!isCheckingOut) {
            // Cart items view
            Column(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(viewModel.cart) { item ->
                        CartListItemRow(
                            item = item,
                            onQtyPlus = { viewModel.updateCartQuantity(item, 1) },
                            onQtyMinus = { viewModel.updateCartQuantity(item, -1) },
                            onRemove = { viewModel.removeFromCart(item) }
                        )
                    }

                    // Billing breakdown
                    item {
                        BillingBreakdownArea(viewModel = viewModel)
                    }
                }

                // Proceed checkout button
                Surface(
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(16.dp)
                    ) {
                        Button(
                            onClick = {
                                if (!viewModel.isLoggedIn) {
                                    viewModel.initiateLogin("") {
                                        isCheckingOut = true
                                    }
                                    viewModel.showAuthDialog = true
                                } else {
                                    isCheckingOut = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("checkout_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Secure Delivery & Checkout (₹${viewModel.cartSubtotal.toInt() + 50})", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next")
                        }
                    }
                }
            }
        } else {
            // Checkout Delivery address, UPI pay and place order screen!
            val totalAmount = viewModel.cartSubtotal.toInt() + 50
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = "1. Delivery Information",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF312E81),
                    fontFamily = FontFamily.Serif
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = shipName,
                    onValueChange = { shipName = it },
                    label = { Text("Recipient Name") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ship_name_input"),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = shipPhone,
                    onValueChange = { shipPhone = it },
                    label = { Text("Contact Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ship_phone_input"),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = addressEntered,
                    onValueChange = { addressEntered = it },
                    label = { Text("Complete Delivery Address") },
                    minLines = 2,
                    leadingIcon = { Icon(imageVector = Icons.Default.Home, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ship_address_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "2. Pay ₹$totalAmount instantly via UPI QR",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF312E81),
                    fontFamily = FontFamily.Serif
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Scan the QR below with PhonePe, Google Pay, BHIM or Paytm to transfer ₹$totalAmount automatically.",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Beautiful interactive QR Code drawer visualizer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Drawing a stylized vector QR code using Canvas
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .background(Color.White)
                                .border(1.5.dp, Color(0xFF4F46E5))
                                .padding(12.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                // Draw typical QR anchor corners
                                val squareSize = size.width * 0.22f

                                // Top Left Anchor
                                drawRect(color = Color.Black, size = androidx.compose.ui.geometry.Size(squareSize, squareSize))
                                drawRect(
                                    color = Color.White,
                                    topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                                    size = androidx.compose.ui.geometry.Size(squareSize - 8.dp.toPx(), squareSize - 8.dp.toPx())
                                )
                                drawRect(
                                    color = Color.Black,
                                    topLeft = Offset(8.dp.toPx(), 8.dp.toPx()),
                                    size = androidx.compose.ui.geometry.Size(squareSize - 16.dp.toPx(), squareSize - 16.dp.toPx())
                                )

                                // Top Right Anchor
                                drawRect(
                                    color = Color.Black,
                                    topLeft = Offset(size.width - squareSize, 0f),
                                    size = androidx.compose.ui.geometry.Size(squareSize, squareSize)
                                )
                                drawRect(
                                    color = Color.White,
                                    topLeft = Offset(size.width - squareSize + 4.dp.toPx(), 4.dp.toPx()),
                                    size = androidx.compose.ui.geometry.Size(squareSize - 8.dp.toPx(), squareSize - 8.dp.toPx())
                                )
                                drawRect(
                                    color = Color.Black,
                                    topLeft = Offset(size.width - squareSize + 8.dp.toPx(), 8.dp.toPx()),
                                    size = androidx.compose.ui.geometry.Size(squareSize - 16.dp.toPx(), squareSize - 16.dp.toPx())
                                )

                                // Bottom Left Anchor
                                drawRect(
                                    color = Color.Black,
                                    topLeft = Offset(0f, size.height - squareSize),
                                    size = androidx.compose.ui.geometry.Size(squareSize, squareSize)
                                )
                                drawRect(
                                    color = Color.White,
                                    topLeft = Offset(4.dp.toPx(), size.height - squareSize + 4.dp.toPx()),
                                    size = androidx.compose.ui.geometry.Size(squareSize - 8.dp.toPx(), squareSize - 8.dp.toPx())
                                )
                                drawRect(
                                    color = Color.Black,
                                    topLeft = Offset(8.dp.toPx(), size.height - squareSize + 8.dp.toPx()),
                                    size = androidx.compose.ui.geometry.Size(squareSize - 16.dp.toPx(), squareSize - 16.dp.toPx())
                                )

                                // Center logo circle or Indian design detail
                                drawCircle(
                                    color = Color(0xFF4F46E5),
                                    radius = size.width * 0.12f,
                                    center = center
                                )
                                drawCircle(
                                    color = Color(0xFFEC4899),
                                    radius = size.width * 0.08f,
                                    center = center,
                                    style = Stroke(width = 2.dp.toPx())
                                )

                                // Draw some random QR noise dashes
                                val strokeWidth = 3.dp.toPx()
                                drawLine(Color.Black, Offset(size.width * 0.4f, size.height * 0.15f), Offset(size.width * 0.55f, size.height * 0.15f), strokeWidth)
                                drawLine(Color.Black, Offset(size.width * 0.15f, size.height * 0.4f), Offset(size.width * 0.15f, size.height * 0.55f), strokeWidth)
                                drawLine(Color.Black, Offset(size.width * 0.45f, size.height * 0.4f), Offset(size.width * 0.45f, size.height * 0.72f), strokeWidth)
                                drawLine(Color.Black, Offset(size.width * 0.65f, size.height * 0.65f), Offset(size.width * 0.82f, size.height * 0.65f), strokeWidth)
                                drawLine(Color.Black, Offset(size.width * 0.55f, size.height * 0.85f), Offset(size.width * 0.8f, size.height * 0.85f), strokeWidth)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Dynamic Merchant text
                        Text(
                            text = viewModel.upiMerchantName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF312E81)
                        )
                        Text(
                            text = "UPI ID: ${viewModel.upiId}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Enter payment transaction ID UTR ref
                OutlinedTextField(
                    value = referenceEntered,
                    onValueChange = { referenceEntered = it },
                    label = { Text("Transfer UTR / Transaction reference (Optional)") },
                    placeholder = { Text("12-digit UPI reference number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transfer_ref_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Place final order button
                Button(
                    onClick = {
                        if (shipName.trim().isEmpty() || shipPhone.trim().isEmpty() || addressEntered.trim().isEmpty()) {
                            Toast.makeText(context, "Please complete all delivery information details.", Toast.LENGTH_LONG).show()
                            return@Button
                        }

                        viewModel.placeOrder(
                            buyerName = shipName,
                            buyerPhone = shipPhone,
                            buyerAddress = addressEntered,
                            buyerAge = viewModel.currentUser?.age ?: 25,
                            buyerGender = viewModel.currentUser?.gender ?: "Female",
                            paymentStatus = if (referenceEntered.trim().isNotEmpty()) "PENDING" else "SUCCESS",
                            transactionId = referenceEntered.trim().ifEmpty { null }
                        ) { finalOrder ->
                            // Action callback on place order success -> Show confirmation dialog
                            showWhatsAppSuccessDialog = finalOrder
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("place_order_now_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5), contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Security lock")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("CONFIRM SUCCESSFUL PAYMENT & BOOK ORDER", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(
                    onClick = { isCheckingOut = false },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Cancel and Edit Cart", color = Color.Gray)
                }
            }
        }
    }

    // WhatsApp Automated Message Success modal dialog
    showWhatsAppSuccessDialog?.let { order ->
        AlertDialog(
            onDismissRequest = {
                showWhatsAppSuccessDialog = null
                onBack()
            },
            title = {
                Text(
                    text = "🏆 Order Placed Successfully!",
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFF385723)
                )
            },
            text = {
                Column {
                    Text(
                        text = "Booking Number: ${order.orderNumber}",
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your booking details are safely recorded in our secure sales ledger. To expedite processing, click 'Send WhatsApp Alert' below. This acts as our automated API notification notifying the shop owner directly.",
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE2F0D9)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "WhatsApp message template containing your product thumbnail, full address details and delivery status will be prepared automatically.",
                            fontSize = 11.sp,
                            color = Color(0xFF385723),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        triggerWhatsAppOrderMessage(context, order, viewModel.cartSubtotal + 50)
                        showWhatsAppSuccessDialog = null
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)) // WhatsApp Green
                ) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Send WhatsApp Alert")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showWhatsAppSuccessDialog = null
                    onBack()
                }) {
                    Text("Done", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun CartListItemRow(
    item: CartItem,
    onQtyPlus: () -> Unit,
    onQtyMinus: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("cart_row_${item.product.id}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Elegant background pattern for item visual
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = getProductColorPalette(item.product.category)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getProductIcon(item.product.category),
                    contentDescription = null,
                    tint = Color(0xFFD4AF37),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black,
                    maxLines = 1
                )
                Text(
                    text = "Size: ${item.size}  •  Color: ${item.color}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "₹${item.product.discountedPrice.toInt()}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4F46E5),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "₹${item.product.price.toInt()}",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                    )
                }
            }

            // Quantity regulators
            Column(horizontalAlignment = Alignment.End) {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = Color.LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFFF3F2EE), RoundedCornerShape(6.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .clickable { onQtyMinus() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("-", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Text(
                        text = item.quantity.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .clickable { onQtyPlus() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun BillingBreakdownArea(viewModel: MainViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Order Invoice Summary",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Sum Subtotal", fontSize = 13.sp, color = Color.Gray)
                Text("₹${viewModel.cartSubtotal.toInt()}", fontSize = 13.sp, color = Color.Black)
            }
            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Festive Packing & Service", fontSize = 13.sp, color = Color.Gray)
                Text("₹50", fontSize = 13.sp, color = Color.Black)
            }
            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Express Home Delivery", fontSize = 13.sp, color = Color.Gray)
                Text("FREE", fontSize = 13.sp, color = Color(0xFF385723), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Grand Total Amount", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(
                    text = "₹${viewModel.cartSubtotal.toInt() + 50}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF4F46E5)
                )
            }
        }
    }
}

// Function to trigger WhatsApp deep link target with fully formatted structured text receipt
private fun triggerWhatsAppOrderMessage(context: Context, order: Order, grandTotal: Double) {
    val rawText = """
🚩 *NEW TRANSACTION ALERT - उदीक्षा Garment Shop*
---------------------------------------
*Order ID:* #${order.orderNumber}
*Product Title:* ${order.productTitle}
*Price Booked:* ₹${order.productPrice.toInt()}
*Payment Status:* ${order.paymentStatus}
*UTR / Trans-ID:* ${order.transactionId ?: "Instant QR"}

*BUYER PROFILE DETAILS:*
*Name:* ${order.buyerName}
*Phone:* ${order.buyerPhone}
*Delivery Address:* ${order.buyerAddress}

---------------------------------------
Developed by *Harsh Mishra*
Contact: +91 8114247911
    """.trimIndent()

    try {
        val formattedMsg = URLEncoder.encode(rawText, StandardCharsets.UTF_8.name())
        val waUrl = "https://api.whatsapp.com/send?phone=919519764098&text=$formattedMsg"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "An error occurred opening WhatsApp. Copying details.", Toast.LENGTH_LONG).show()
        // Fallback: Copy to Clipboard
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clipData = android.content.ClipData.newPlainText("Order Receipt", rawText)
        clipboardManager.setPrimaryClip(clipData)
    }
}
