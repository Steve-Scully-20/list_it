package com.example.list_it.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import com.example.list_it.data.ProductDao
import com.example.list_it.viewmodel.ProductCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AlcoholScreen(productDao: ProductDao) {
    // Fetch the list of products in the "Alcohol" category from the database
    val alcoholProductsFlow = productDao.getProductsByCategory("Alcohol")

    // Collect the flow into a state
    val alcoholProductsState = alcoholProductsFlow.collectAsState(initial = emptyList())

    Column {
        // Display the list of large minerals products using LazyColumn
        LazyColumn(contentPadding = PaddingValues(bottom = 70.dp)) {
            itemsIndexed(alcoholProductsState.value) { index, product ->
                ProductCard(
                    product = product,
                    productDao = productDao,
                    onDelete = {
                        CoroutineScope(Dispatchers.IO).launch {
                            productDao.deleteProduct(product.id)
                        }
                    },
                    isLastCard = index == alcoholProductsState.value.lastIndex
                )
            }
        }
    }
}


