package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Product
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: MainViewModel,
    onProductClick: (Product) -> Unit,
    onGoToCart: () -> Unit,
    onGoToAdmin: () -> Unit,
    onGoToTeam: () -> Unit,
    modifier: Modifier = Modifier
) {
    val products by viewModel.productsFlow.collectAsState()
    val filteredProducts = products.filter {
        val matchesCategory = viewModel.selectedCategory == "All" || it.category.equals(viewModel.selectedCategory, ignoreCase = true)
        val matchesSearch = it.title.contains(viewModel.searchQuery, ignoreCase = true) || it.description.contains(viewModel.searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    val categories = listOf("All", "Sarees", "Kurtis", "Lehengas", "Anarkali Suits", "Mens Wear", "Kids Wear")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFBF8FF)) // Sleek Interface background
    ) {
        // Brand Header Section
        BrandHeader(
            cartCount = viewModel.cart.sumOf { it.quantity },
            onCartClick = onGoToCart,
            onAdminClick = onGoToAdmin,
            onTeamClick = onGoToTeam,
            viewModel = viewModel
        )

        // Heavy search block
        SearchBlock(
            query = viewModel.searchQuery,
            onQueryChange = { viewModel.searchQuery = it }
        )

        // Scrollable area
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Promo Banner Carousel
            PromotionCarousel()

            Spacer(modifier = Modifier.height(12.dp))

            // Category Selector Horizontal Row
            CategorySelector(
                categories = categories,
                selected = viewModel.selectedCategory,
                onSelect = { viewModel.selectedCategory = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Section Heading
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (viewModel.selectedCategory == "All") "Exclusive Festive Collection" else "${viewModel.selectedCategory} Range",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF312E81), // Deep Indigo
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "${filteredProducts.size} active listings",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }

            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = "No products found",
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No outfits match your search criteria.",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                // Main Grid listing
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProducts) { item ->
                        ProductGridItem(
                            product = item,
                            onClick = { onProductClick(item) },
                            onAddToCart = {
                                viewModel.addToCart(item, "M", "Default")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BrandHeader(
    cartCount: Int,
    onCartClick: () -> Unit,
    onAdminClick: () -> Unit,
    onTeamClick: () -> Unit,
    viewModel: MainViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF4F46E5), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "U",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "उदीक्षा ",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF312E81),
                                fontFamily = FontFamily.Serif
                            )
                            Text(
                                text = "Garment",
                                fontSize = 14.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Light,
                                fontFamily = FontFamily.Serif
                            )
                        }
                    }
                }
            }

            // Quick actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Team/Staff display button
                IconButton(
                    onClick = onTeamClick,
                    modifier = Modifier.testTag("team_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = "Our Team",
                        tint = Color(0xFF4F46E5),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Admin dashboard key
                IconButton(
                    onClick = {
                        if (viewModel.isAdminVerified) {
                            onAdminClick()
                        } else {
                            viewModel.initiateLogin("mbhola099@gmail.com") {
                                onAdminClick()
                            }
                            viewModel.showAuthDialog = true
                        }
                    },
                    modifier = Modifier.testTag("admin_portal_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin Panel",
                        tint = if (viewModel.isAdminVerified) Color.Green else Color(0xFF4F46E5),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Shopping Cart Badge
                Box {
                    IconButton(
                        onClick = onCartClick,
                        modifier = Modifier.testTag("cart_nav_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = Color(0xFF4F46E5),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    if (cartCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-2).dp, y = 2.dp)
                                .background(Color(0xFFEF4444), RoundedCornerShape(10.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cartCount.toString(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBlock(query: String, onQueryChange: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-10).dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search icon",
                tint = Color.Gray
            )
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Search elegant sarees, sherwanis, kurtas...", fontSize = 14.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input"),
                singleLine = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromotionCarousel() {
    val banners = listOf(
        PromoBanner("Wedding Season Special", "Exclusive Bridal Sarees & Gowns", Color(0xFF4F46E5), Color(0xFFEC4899)),
        PromoBanner("Bundi Nehru Jackets", "Premium Jodhpuris & Kurtas", Color(0xFF9333EA), Color(0xFFEC4899)),
        PromoBanner("Chikankari Handwork", "40% Festive Discount active", Color(0xFF0F172A), Color(0xFFEC4899))
    )

    val pagerState = rememberPagerState(pageCount = { banners.size })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val b = banners[page]
            val gradientColors = when (page) {
                0 -> listOf(Color(0xFF4338CA), Color(0xFF9333EA), Color(0xFFEC4899))
                1 -> listOf(Color(0xFF4F46E5), Color(0xFF312E81))
                else -> listOf(Color(0xFF701A75), Color(0xFFEC4899))
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = gradientColors
                        )
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "FESTIVE EVENT",
                        color = b.accent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = b.title,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = b.sub,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp
                    )
                }

                // Traditional Mandala Drawing in Canvas code
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .drawBehind {
                            drawCircle(
                                color = b.accent.copy(alpha = 0.15f),
                                radius = size.minDimension / 1.5f,
                                center = center
                            )
                            drawCircle(
                                color = b.accent.copy(alpha = 0.3f),
                                radius = size.minDimension / 2.2f,
                                center = center,
                                style = Stroke(width = 2f)
                            )
                            // Draw flower petals lines on mandala
                            val petals = 12
                            val radius = size.minDimension / 2.2f
                            for (i in 0 until petals) {
                                val angle = (i * 360f / petals) * (Math.PI / 180f)
                                val endX = center.x + radius * Math.cos(angle).toFloat()
                                val endY = center.y + radius * Math.sin(angle).toFloat()
                                drawLine(
                                    color = b.accent.copy(alpha = 0.4f),
                                    start = center,
                                    end = Offset(endX, endY),
                                    strokeWidth = 1.5f
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Star",
                        tint = b.accent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Pager indicator
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(banners.size) { index ->
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            if (pagerState.currentPage == index) Color(0xFFEC4899) else Color.White.copy(alpha = 0.5f)
                        )
                )
            }
        }
    }
}

data class PromoBanner(
    val title: String,
    val sub: String,
    val bgLeft: Color,
    val accent: Color
)

@Composable
fun CategorySelector(
    categories: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { cat ->
            val isSelected = selected == cat
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isSelected) Color(0xFF4F46E5) else Color.White
                    )
                    .clickable { onSelect(cat) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("cat_tab_${cat.replace(" ", "_")}")
            ) {
                Text(
                    text = cat,
                    color = if (isSelected) Color.White else Color.Black,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun ProductGridItem(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Simulated HD Custom Cloth Visualizer utilizing Canvas (Extremely gorgeous and matches background thread)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = getProductColorPalette(product.category)
                        )
                    )
                    .drawBehind {
                        // Draw elegant clothing curves representation
                        val p = Path()
                        p.moveTo(size.width * 0.2f, 0f)
                        p.lineTo(size.width * 0.8f, 0f)
                        p.lineTo(size.width * 0.7f, size.height * 0.9f)
                        p.lineTo(size.width * 0.3f, size.height * 0.9f)
                        p.close()
                        drawPath(
                            path = p,
                            color = Color.White.copy(alpha = 0.15f)
                        )

                        // Gold Zari embroidery line
                        drawLine(
                            color = Color(0xFFD4AF37),
                            start = Offset(0f, size.height * 0.85f),
                            end = Offset(size.width, size.height * 0.85f),
                            strokeWidth = 3f
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = getProductIcon(product.category),
                        contentDescription = "Cloth Design",
                        tint = Color(0xFFD4AF37),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.category.uppercase(),
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.90f)
                    )
                }

                // Discount Stamp Icon Tag
                val discountPercent = (((product.price - product.discountedPrice) / product.price) * 100).toInt()
                if (discountPercent > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .background(Color(0xFFEC4899), RoundedCornerShape(bottomEnd = 8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$discountPercent% OFF",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = product.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = product.colors.split(",").firstOrNull() ?: "Standard",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Price Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "₹${product.discountedPrice.toInt()}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF4F46E5)
                    )
                    Text(
                        text = "₹${product.price.toInt()}",
                        fontSize = 11.sp,
                        textDecoration = TextDecoration.LineThrough,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Add to view button
                Button(
                    onClick = onAddToCart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .testTag("add_to_cart_btn_${product.id}"),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4F46E5),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "Add Basket",
                            modifier = Modifier.size(13.dp)
                        )
                        Text("Add to Cart", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

fun getProductIcon(category: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (category.lowercase()) {
        "sarees", "lehengas" -> Icons.Default.Checkroom
        "kurtis", "anarkali suits" -> Icons.Default.Checkroom
        "mens wear" -> Icons.Default.DryCleaning
        "kids wear" -> Icons.Default.ChildCare
        else -> Icons.Default.Checkroom
    }
}

fun getProductColorPalette(category: String): List<Color> {
    return when (category.lowercase()) {
        "sarees" -> listOf(Color(0xFF5E0B1B), Color(0xFF9E1F30)) // Velvety Crimson
        "lehengas" -> listOf(Color(0xFF8C0E51), Color(0xFFC7247B)) // Royal Pink
        "kurtis" -> listOf(Color(0xFF0F5A47), Color(0xFF268D72)) // Soft Peacock Green
        "anarkali suits" -> listOf(Color(0xFF5E2B96), Color(0xFF864FC4)) // Traditional Purple
        "mens wear" -> listOf(Color(0xFF133E56), Color(0xFF246D92)) // Royal Blue Silk
        "kids wear" -> listOf(Color(0xFF94610F), Color(0xFFC78C23)) // Festive Saffron
        else -> listOf(Color(0xFF5A000A), Color(0xFF760410))
    }
}
