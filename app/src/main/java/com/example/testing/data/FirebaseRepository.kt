package com.example.testing.data

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseRepository {
    private val database by lazy { 
        try { FirebaseDatabase.getInstance() } catch (e: Exception) { null }
    }
    
    private val shopsRef by lazy { database?.getReference("shops") }
    private val travellerRef by lazy { database?.getReference("traveller_location") }

    fun addShop(shop: ChargingShop) {
        val ref = shopsRef ?: return
        val key = ref.push().key
        if (key != null) ref.child(key).setValue(shop)
    }

    fun observeShops(onShopsUpdated: (List<ChargingShop>) -> Unit) {
        val ref = shopsRef ?: return
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val shopsList = mutableListOf<ChargingShop>()
                for (shopSnapshot in snapshot.children) {
                    try {
                        val name = shopSnapshot.child("name").getValue(String::class.java) ?: ""
                        val distance = shopSnapshot.child("distance").getValue(String::class.java) ?: ""
                        val adapterType = shopSnapshot.child("adapterType").getValue(String::class.java) ?: ""
                        val contact = shopSnapshot.child("contact").getValue(String::class.java) ?: ""
                        val price = shopSnapshot.child("price").getValue(String::class.java) ?: ""
                        val rating = shopSnapshot.child("rating").getValue(Float::class.java) ?: 0f
                        val lat = shopSnapshot.child("lat").getValue(Double::class.java) ?: 0.0
                        val lng = shopSnapshot.child("lng").getValue(Double::class.java) ?: 0.0
                        val isAvailable = shopSnapshot.child("isAvailable").getValue(Boolean::class.java) ?: true
                        shopsList.add(ChargingShop(name, distance, adapterType, contact, price, rating, lat, lng, isAvailable))
                    } catch (e: Exception) { e.printStackTrace() }
                }
                onShopsUpdated(shopsList)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun updateTravellerLocation(latLng: LatLng) {
        travellerRef?.setValue(mapOf("lat" to latLng.latitude, "lng" to latLng.longitude))
    }

    fun observeTravellerLocation(onLocationUpdated: (LatLng) -> Unit) {
        travellerRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lat = snapshot.child("lat").getValue(Double::class.java) ?: 47.6128
                val lng = snapshot.child("lng").getValue(Double::class.java) ?: -122.3214
                onLocationUpdated(LatLng(lat, lng))
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
