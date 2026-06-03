package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Product
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onGoToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var selectedSize by remember { mutableStateOf(product.sizes.split(",").firstOrNull() ?: "M") }
    var selectedColor by remember { mutableStateOf(product.colors.split(",").firstOrNull()?.trim() ?: "Maroon") }

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
                text = "Outfit Detail",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF312E81),
                fontFamily = FontFamily.Serif
            )

            IconButton(
                onClick = onGoToCart,
                modifier = Modifier.background(Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Cart",
                    tint = Color(0xFF4F46E5)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            // Visual Banner Area showing rich gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = getProductColorPalette(product.category)
                        )
                    )
                    .drawBehind {
                        // Drawing textile/floral patterns on detail view background
                        drawCircle(
                            color = Color.White.copy(alpha = 0.05f),
                            radius = size.width / 1.5f
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = getProductIcon(product.category),
                        contentDescription = "Embroidery Emblem",
                        tint = Color(0xFFD4AF37),
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "PREMIUM ${product.category.uppercase()}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }
                }
            }

            // Product Details Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontFamily = FontFamily.Serif
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Category: ${product.category}",
                            color = Color(0xFF4F46E5),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // Stock indication
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (product.availableStock > 0) Color(0xFFE2F0D9) else Color(0xFFFCE4D6)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (product.availableStock > 0) "In Stock (${product.availableStock})" else "Out of Stock",
                            color = if (product.availableStock > 0) Color(0xFF385723) else Color(0xFFC65911),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price display
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "₹${product.discountedPrice.toInt()}",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF4F46E5)
                    )
                    Text(
                        text = "M.R.P. ₹${product.price.toInt()}",
                        fontSize = 16.sp,
                        textDecoration = TextDecoration.LineThrough,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    val save = (product.price - product.discountedPrice).toInt()
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFEC4899).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "SAVE ₹$save",
                            color = Color(0xFFEC4899),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = Color.LightGray.copy(alpha = 0.5f))

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Description",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = product.description,
                    color = Color.DarkGray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Size Selector
                Text(
                    text = "Select Size",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    product.sizes.split(",").forEach { size ->
                        val cleanSize = size.trim()
                        val isSelected = selectedSize == cleanSize
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF4F46E5) else Color.White)
                                .border(1.dp, if (isSelected) Color.Transparent else Color.LightGray, RoundedCornerShape(8.dp))
                                .clickable { selectedSize = cleanSize }
                                .testTag("size_tag_$cleanSize"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cleanSize,
                                color = if (isSelected) Color.White else Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Color Selector
                Text(
                    text = "Select Color Accent",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    product.colors.split(",").forEach { color ->
                        val cleanColor = color.trim()
                        val isSelected = selectedColor == cleanColor
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) Color(0xFF4F46E5) else Color.White)
                                .border(1.dp, if (isSelected) Color.Transparent else Color.LightGray, RoundedCornerShape(16.dp))
                                .clickable { selectedColor = cleanColor }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("color_tag_$cleanColor"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cleanColor,
                                color = if (isSelected) Color.White else Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        // Bottom checkout actions (Adds to cart or places instant checkout)
        Surface(
            tonalElevation = 8.dp,
            shadowElevation = 8.dp,
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cart Button
                OutlinedButton(
                    onClick = {
                        viewModel.addToCart(product, selectedSize, selectedColor)
                        onGoToCart()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("detail_add_cart_button"),
                    border = BorderStroke(1.5.dp, Color(0xFF4F46E5)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4F46E5)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = "Basket")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add To Bag", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                // Buy Now with Mandatory login check
                Button(
                    onClick = {
                        val proceedAction = {
                            viewModel.addToCart(product, selectedSize, selectedColor)
                            onGoToCart()
                        }

                        if (!viewModel.isLoggedIn) {
                            // strictly trigger first-time authentication before checkout
                            viewModel.initiateLogin("", proceedAction)
                            viewModel.showAuthDialog = true
                        } else {
                            proceedAction()
                        }
                    },
                    modifier = Modifier
                        .weight(1.2f)
                        .height(52.dp)
                        .testTag("buy_now_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4F46E5),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.FlashOn, contentDescription = "Instant buy")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("BUY NOW", fontSize = 15.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}
