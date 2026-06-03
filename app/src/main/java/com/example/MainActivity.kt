package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ui.MainViewModel
import com.example.ui.components.AuthDialog
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.TeamScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels {
        MainViewModel.Factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                var currentScreen by remember { mutableStateOf("explore") }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = androidx.compose.ui.graphics.Color(0xFFFBF8FF)
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                            },
                            label = "screen_navigation"
                        ) { screen ->
                            when (screen) {
                                "explore" -> {
                                    ExploreScreen(
                                        viewModel = viewModel,
                                        onProductClick = { product ->
                                            viewModel.currentProductDetail = product
                                            currentScreen = "detail"
                                        },
                                        onGoToCart = { currentScreen = "cart" },
                                        onGoToAdmin = { currentScreen = "admin" },
                                        onGoToTeam = { currentScreen = "team" }
                                    )
                                }
                                "detail" -> {
                                    viewModel.currentProductDetail?.let { product ->
                                        ProductDetailScreen(
                                            product = product,
                                            viewModel = viewModel,
                                            onBack = { currentScreen = "explore" },
                                            onGoToCart = { currentScreen = "cart" }
                                        )
                                    } ?: run {
                                        currentScreen = "explore"
                                    }
                                }
                                "cart" -> {
                                    CartScreen(
                                        viewModel = viewModel,
                                        onBack = { currentScreen = "explore" }
                                    )
                                }
                                "admin" -> {
                                    AdminScreen(
                                        viewModel = viewModel,
                                        onBack = { currentScreen = "explore" }
                                    )
                                }
                                "team" -> {
                                    TeamScreen(
                                        viewModel = viewModel,
                                        onBack = { currentScreen = "explore" }
                                    )
                                }
                            }
                        }

                        // Global Login / Auth Overlay Dialog
                        if (viewModel.showAuthDialog) {
                            AuthDialog(
                                viewModel = viewModel,
                                onDismiss = { viewModel.showAuthDialog = false }
                            )
                        }
                    }
                }
            }
        }
    }
}
