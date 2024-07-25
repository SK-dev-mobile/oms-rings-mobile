package skdev.omsrings.mobile.data.utils

import dev.gitlive.firebase.firestore.FirebaseFirestore

class FirestoreCollections(private val firestore: FirebaseFirestore) {
    val userInfo by lazy { firestore.collection("user_info") }
    val folders by lazy { firestore.collection("folders") }
    val orders by lazy { firestore.collection("orders") }
    val daysInfo by lazy { firestore.collection("days_info") }
    val userSettings by lazy { firestore.collection("user_settings") }
}