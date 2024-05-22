package woowacourse.shopping.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import woowacourse.shopping.data.model.CartItem
import woowacourse.shopping.data.model.Product
import kotlin.concurrent.thread

@Database(
    entities = [
        Product::class,
        CartItem::class,
    ],
    version = 3,
)
abstract class ShoppingDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    abstract fun cartDao(): CartDao

    companion object {
        @Volatile
        private var databaseInstance: ShoppingDatabase? = null

        fun getDatabase(context: Context): ShoppingDatabase {
            return databaseInstance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShoppingDatabase::class.java,
                    "shopping_database.db"
                )
                    .fallbackToDestructiveMigration()
//                    .addCallback(
//                        object : Callback() {
//                            override fun onCreate(db: SupportSQLiteDatabase) {
//                                super.onCreate(db)
//                                val productDao = databaseInstance?.productDao()
//                                thread {
//                                    productDao?.addAll(PRODUCT_DATA)
//                                }
//                            }
//                        }
//                    )
                    .build()

                databaseInstance = instance
                instance
            }
        }
    }
}
