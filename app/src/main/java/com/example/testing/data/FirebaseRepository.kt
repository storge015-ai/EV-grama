package com.example.testing.data

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseRepository {
    private val database by lazy { 
        try {
            FirebaseDatabase.getInstance()
        } catch (e: Exception) {
            null
        }
    }
    
    private val shopsRef by lazy { database?.getReference("shops") }
    private val travellerRef by lazy { database?.getReference("traveller_location") }
    private val hostRef by lazy { database?.getReference("host_location") }

    /**
     * Adds a new charging shop to the database.
     */
    fun addShop(shop: ChargingShop) {
        val ref = shopsRef ?: return
        val key = ref.push().key
        if (key != null) {
            ref.child(key).setValue(shop)
        }
    }

    /**
     * Observes changes in the shops list.
     */
    fun observeShops(onShopsUpdated: (List<ChargingShop>) -> Unit) {
        val ref = shopsRef ?: return
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val shopsList = mutableListOf<ChargingShop>()
                for (shopSnapshot in snapshot.children) {
                    try {
                        val shop = shopSnapshot.getValue(ChargingShop::class.java)
                        if (shop != null) {
                            shopsList.add(shop)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                onShopsUpdated(shopsList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        })
    }

    /**
     * Updates the traveller's current location in the database.
     */
    fun updateTravellerLocation(latLng: LatLng) {
        val ref = travellerRef ?: return
        val locationMap = mapOf(
            "lat" to latLng.latitude,
            "lng" to latLng.longitude
        )
        ref.setValue(locationMap)
    }

    /**
     * Observes the traveller's location for real-time updates.
     */
    fun observeTravellerLocation(onLocationUpdated: (LatLng) -> Unit) {
        val ref = travellerRef ?: return
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lat = snapshot.child("lat").getValue(Double::class.java)
                val lng = snapshot.child("lng").getValue(Double::class.java)
                if (lat != null && lng != null) {
                    onLocationUpdated(LatLng(lat, lng))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        })
    }

    /**
     * Updates the host's current location in the database.
     */
    fun updateHostLocation(latLng: LatLng) {
        val ref = hostRef ?: return
        val locationMap = mapOf(
            "lat" to latLng.latitude,
            "lng" to latLng.longitude
        )
        ref.setValue(locationMap)
    }

    /**
     * Observes the host's location for real-time updates.
     */
    fun observeHostLocation(onLocationUpdated: (LatLng) -> Unit) {
        val ref = hostRef ?: return
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lat = snapshot.child("lat").getValue(Double::class.java)
                val lng = snapshot.child("lng").getValue(Double::class.java)
                if (lat != null && lng != null) {
                    onLocationUpdated(LatLng(lat, lng))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        })
    }
}
