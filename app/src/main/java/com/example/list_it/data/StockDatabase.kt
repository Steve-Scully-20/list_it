package com.example.list_it.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class StockDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var instance: StockDatabase? = null

        fun getDatabase(context: Context): StockDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    StockDatabase::class.java,
                    "stock_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()
                instance = newInstance
                newInstance
            }
        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Insert initial data when the database is created
                CoroutineScope(Dispatchers.IO).launch {
                    instance?.let { database ->
                        val dao = database.productDao()
                        dao.insertProduct(Product(1, "Centra 3L Light Milk", "0", "Milk", true))
                    }
                }
            }
        }
    }
}