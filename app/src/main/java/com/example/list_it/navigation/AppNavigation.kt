package com.example.list_it.navigation

import BottomNavigationBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.list_it.data.Product
import com.example.list_it.data.ProductDao
import com.example.list_it.ui.screen.AlcoholScreen
import com.example.list_it.ui.screen.LargeMineralsScreen
import com.example.list_it.ui.screen.MilkScreen
import com.example.list_it.ui.screen.SmallMineralsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


object Screens {
    const val MILK = "milk"
    const val SMALL_MINERALS = "small_minerals"
    const val LARGE_MINERALS = "large_minerals"
    const val ALCOHOL = "alcohol"
    const val PRODUCT_ENTRY = "product_entry"
    // Define other screen constants here
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(productDao: ProductDao) {
    val navController = rememberNavController()

    // Coroutine scope
    val coroutineScope = rememberCoroutineScope()

    // Define your bottom navigation items
    listOf(
        Screens.MILK to "Milk",
        Screens.SMALL_MINERALS to "Small Minerals",
        Screens.LARGE_MINERALS to "Large Minerals",
        Screens.ALCOHOL to "Alcohol",
        Screens.PRODUCT_ENTRY to "Product Entry"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Top app bar
        Surface(color = Color.Blue) {
            TopAppBar(
                title = {
                    Text(
                        text = "List Maker",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            productDao.resetAllQuantities()
                        }
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                modifier = Modifier
                    .height(56.dp) // Set the height of the app bar
                    .fillMaxWidth(), // Fill the width of the parent
            )

        }

        // NavHost for screen navigation
        Box(modifier = Modifier.weight(1f)) {
            NavHost(navController = navController, startDestination = Screens.MILK) {
                composable(Screens.MILK) {
                    MilkScreen(productDao)
                }
                composable(Screens.SMALL_MINERALS) {
                    SmallMineralsScreen(productDao)
                }
                composable(Screens.LARGE_MINERALS) {
                    LargeMineralsScreen(productDao)
                }
                composable(Screens.ALCOHOL) {
                    AlcoholScreen(productDao)
                }
                composable(Screens.PRODUCT_ENTRY) {
                    ProductEntryScreen(
                        productDao = productDao,
                        navController = navController,
                        coroutineScope = coroutineScope
                    )
                }
            }

            // Bottom navigation bar
            BottomNavigationBar(navController = navController)
        }
    }
}

@Composable
private fun ProductEntryScreen(
    productDao: ProductDao,
    navController: NavController,
    coroutineScope: CoroutineScope
) {
    var productName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Milk") }
    var instock by remember { mutableStateOf(true) }


    var isProductNameValid by remember { mutableStateOf(false) }
    var isQuantityValid by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        // Product name input
        OutlinedTextField(
            value = productName,
            onValueChange = { productName = it
                            isProductNameValid = it.isNotBlank()},
            label = { Text("Product Name") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        // Quantity input
        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it
                isQuantityValid = it.isNotBlank()},
            label = { Text("Quantity") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
        )

        // Category selection
        Column {
            Text("Category")
            RadioButtonGroup(
                options = listOf("Milk", "Small Minerals", "Large Minerals", "Alcohol"),
                selectedOption = category,
                onOptionSelected = { category = it }
            )
        }

        // In stock switch
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("In Stock:")
            Switch(
                checked = instock,
                onCheckedChange = { instock = it },
                modifier = Modifier.padding(start = 30.dp)
            )
        }


        // Save button
        Button(
            onClick = {
                coroutineScope.launch {
                    // Find the first available slot
                    val id = productDao.findFirstAvailableSlot() ?: -1
                    val slot = id+1
                    if (id != -1) {
                        // Insert the product into the first available slot
                        val product = Product(slot, productName, quantity, category, instock)
                        productDao.insertProduct(product)

                        navController.navigate("product_entry")
                    } else {
                        navController.navigate("small_minerals")
                        // Handle the case when no slot is available
                        // Optionally, show a message to the user or take any other action
                    }
                }
            }, enabled = isProductNameValid && isQuantityValid
        ) {
            Text("Save")
        }
    }
}

@Composable
fun RadioButtonGroup(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column {
        options.forEach { text ->
            Row(
                modifier = Modifier
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) }
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = { onOptionSelected(text) }
                )
                Text(
                    text = text,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}