package com.example.testing.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseDatabaseHelper {
    private val database = FirebaseDatabase.getInstance()
    private val shopsRef = database.getReference("shops")

    /**
     * Adds or updates a charging shop in the database.
     */
    fun saveShop(shop: ChargingShop) {
        // Use the shop name as a key or generate a unique ID
        val shopId = shop.name.replace(" ", "_").lowercase()
        shopsRef.child(shopId).setValue(shop)
    }

    /**
     * Retrieves all shops as a real-time Flow.
     * This will automatically update whenever data changes in Firebase.
     */
    fun getShopsFlow(): Flow<List<ChargingShop>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val shops = snapshot.children.mapNotNull { it.getValue(ChargingShop::class.java) }
                trySend(shops)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        shopsRef.addValueEventListener(listener)
        awaitClose { shopsRef.removeEventListener(listener) }
    }
}
