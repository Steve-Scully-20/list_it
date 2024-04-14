package com.example.list_it

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.list_it.data.ProductDao
import com.example.list_it.data.StockDatabase
import com.example.list_it.navigation.AppNavigation
import com.example.list_it.ui.theme.List_itTheme

class MainActivity : ComponentActivity() {
    private lateinit var productDao: ProductDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val stockDatabase = StockDatabase.getDatabase(applicationContext)
        productDao = stockDatabase.productDao()
        setContent {
            List_itTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(productDao = productDao)
                }
            }
        }
    }
}
