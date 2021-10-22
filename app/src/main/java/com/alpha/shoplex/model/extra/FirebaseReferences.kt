package com.alpha.shoplex.model.extra

import android.annotation.SuppressLint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

object FirebaseReferences {
    // -----------------------> Databases <----------------------- //
    @SuppressLint("StaticFieldLeak")
    private val database = Firebase.firestore
    private val imagesDatabase = FirebaseStorage.getInstance().reference

    // -----------------------> Firestore <----------------------- //
    // Products
    val productsRef = database.collection("Products")
    val storesRef = database.collection("Sellers")
    val ordersRef = database.collection("Orders")
    val usersRef = database.collection("Users")
    val recentVisits = database.collection("Recent Visits")
    val chatRef= database.collection("Chats")
    val ReportRef=database.collection("Reports")
    // -----------------------> Storage <----------------------- //
    // Products
    val imagesProductsRef = imagesDatabase.child("Products")
    val imagesUserRef = imagesDatabase.child("Users")

    //----------------------> Notifications <---------------------------//
    val notificationTokensRef = database.collection("Notification Center")
}