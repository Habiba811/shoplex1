package com.alpha.shoplex.room.data


import androidx.lifecycle.LiveData
import androidx.room.*
import com.alpha.shoplex.model.pojo.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopLexDao {

    // Favourite
    @Query("SELECT * FROM Favourite")
    fun readFavourite(): LiveData<List<ProductFavourite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavourite(favourite: ProductFavourite)

    @Query("DELETE FROM Favourite WHERE productID = :productID")
    suspend fun deleteFavourite(productID: String)

    // Cart
    @Query("SELECT * FROM Cart")
    fun readCart(): LiveData<List<ProductCart>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCart(cart: ProductCart)

    @Query("DELETE FROM Cart WHERE productID = :productID")
    suspend fun deleteCart(productID: String)

    @Query("UPDATE Cart SET cartQuantity = :quantity WHERE productID = :productID")
    suspend fun updateCart(productID: String, quantity: Int)

    // Message
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMessage(message: Message)

    @Query("SELECT * FROM messages where chatID = :chatID order by messageID")
    fun readAllMessage(chatID: String): LiveData<List<Message>>

    @Query("UPDATE messages SET isSent = 1 where messageID = :messageID")
    fun setSent(messageID: String)

    @Query("UPDATE messages SET isRead = 1 where messageID = :messageID")
    fun setReadMessage(messageID: String)

    @Query("SELECT * FROM Favourite WHERE productID = :productId LIMIT 1")
    fun searchFav(productId: String): Flow<ProductFavourite>

    @Query("SELECT * FROM Cart WHERE productID = :productId LIMIT 1")
    fun searchCart(productId: String): Flow<ProductCart>

    // Store Location
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addLocation(location: StoreLocationInfo)

    @Query("SELECT * FROM StoresLocation WHERE storeID = :storeID AND location = :location LIMIT 1")
    fun getLocation(storeID: String, location: Location): Flow<StoreLocationInfo>

    @Query("SELECT * FROM StoresLocation WHERE storeID IN (:storeIDs)")
    fun getLocations(storeIDs: ArrayList<String>): Flow<Array<StoreLocationInfo>>
}