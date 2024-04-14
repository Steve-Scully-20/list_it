import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(navController: NavController) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Milk", "Small Minerals", "Large Minerals", "Alcohol", "Add Product")

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        NavigationBar {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = {
                        if (item == "Add Product") {
                            // Use plus icon for "Add Product" item
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = item,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            // Use list icon for other items
                            Icon(
                                Icons.Filled.List,
                                contentDescription = item,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    label = { Text(item, fontSize = 8.sp, fontWeight = FontWeight.Bold) },
                    selected = selectedItem == index,
                    onClick = {
                        selectedItem = index
                        // Navigate to the corresponding destination based on the selected index
                        when (index) {
                            0 -> navController.navigate("milk") // Navigate to the "milk" screen
                            1 -> navController.navigate("small_minerals")
                            2 -> navController.navigate("large_minerals") // Navigate to the "milk" screen
                            3 -> navController.navigate("alcohol")
                            else -> navController.navigate("product_entry")// Navigate to the "small_minerals" screen
                            // Add other cases for remaining screens
                        }
                    }
                )
            }
        }
    }
}