package com.lukyanov.app.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.lukyanov.app.app.ui.RootNavHost
import com.lukyanov.app.common.ui.theme.LukyanovTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LukyanovTheme {
                RootNavHost(navController = rememberNavController())
            }
        }
    }
}