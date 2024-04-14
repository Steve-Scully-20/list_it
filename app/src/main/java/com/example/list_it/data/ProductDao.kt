package com.example.list_it.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


//Table Functions
@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("SELECT * from products WHERE id = :id")
    fun getProduct(id:Int): Flow<Product>

    @Query("SELECT * from products ORDER BY quantity DESC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products LIMIT 1")
    suspend fun getFirstProduct(): Product?

    @Query("SELECT * FROM products WHERE category = :category")
    fun getProductsByCategory(category: String): Flow<List<Product>>

    @Query("UPDATE products SET instock = NOT instock WHERE id = :productId")
    suspend fun toggleInStock(productId: Int)


    @Query("SELECT id FROM products ORDER BY id DESC LIMIT 1")
    suspend fun findFirstAvailableSlot(): Int?


    @Query("UPDATE products SET quantity = ''")
    suspend fun resetAllQuantities()

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProduct(productId: Int)

}