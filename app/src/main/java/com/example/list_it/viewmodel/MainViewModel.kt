package com.example.list_it.viewmodel


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.list_it.data.Product
import com.example.list_it.data.ProductDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
private fun DeleteConfirmationDialog(
    showDialog: Boolean,
    onConfirmDelete: () -> Unit,
    onDismissDialog: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissDialog,
            title = { Text("Delete Data") },
            text = { Text("Are you sure you want to delete this data?") },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirmDelete()
                        onDismissDialog()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = onDismissDialog) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
fun ProductCard(
    product: Product,
    productDao: ProductDao,
    onDelete: () -> Unit,
    isLastCard: Boolean
)  {
    val quantityState = remember { mutableStateOf(product.quantity) }
    var showDialog by remember { mutableStateOf(false) }
    // Retrieve the initial switch state from the database
    val isInStock = product.instock
    var switchState by remember { mutableStateOf(isInStock) }
    val imeAction = if (isLastCard) ImeAction.Done else ImeAction.Next

    // Toggle the switch state and update the database
    fun toggleSwitch(productId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            productDao.toggleInStock(productId)
        }
    }
    // Reset the quantity to zero
    fun resetQuantity(product: Product, productDao: ProductDao) {
        CoroutineScope(Dispatchers.Main).launch {
            productDao.update(product.copy(quantity = ""))
            quantityState.value = ""
        }
    }

    // Define colors for light green and light red
    val lightGreen = Color(0xFFC8E6C9)
    val lightRed = Color(0xFFFFCDD2)

    // Choose the background color based on the switch state
    val backgroundColor = if (switchState) lightGreen else lightRed

    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(25))
            .background(backgroundColor) // Set the background color
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
        ) {
            Column(modifier =  Modifier,
                horizontalAlignment = AbsoluteAlignment.Left){
                Button(
                    onClick = {
                        resetQuantity(product, productDao)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Done",
                        modifier = Modifier.size(25.dp)
                    )
                }
                Row {

                    Switch(
                        checked = switchState,
                        onCheckedChange = { checked ->
                            switchState = checked
                            // Update the boolean value in the database
                            toggleSwitch(product.id)
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier
                            .width(40.dp)
                            .height(40.dp)
                            .padding(top = 8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete"
                        )
                    }
                }
            }


            DeleteConfirmationDialog(
                showDialog = showDialog,
                onConfirmDelete = {
                    // Call the onDelete lambda function when the user confirms deletion
                    onDelete()
                    showDialog = false // Close the dialog
                },
                onDismissDialog = { showDialog = false }
            )

            Column (modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally){

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineMedium
                )

                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically){

                    Spacer(modifier = Modifier)

                    Text(
                        text = "Quantity: ",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Box(
                        modifier = Modifier.width(100.dp),
                    ) {
                        TextField(
                            value = quantityState.value,
                            onValueChange = { newValue: String ->
                                quantityState.value = newValue // Update the quantityState value
                            },
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.NumberPassword,
                                imeAction = imeAction
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterEnd)
                        )
                    }
                }
            }
        }
    }

    // Update the quantity in the database when quantityState changes
    LaunchedEffect(quantityState.value) {
        productDao.update(product.copy(quantity = quantityState.value))
    }
}



