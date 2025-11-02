package com.example.rither.screen.studentVerification

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

fun saveStudentInfoToDatabase(studentId: String, name: String) {
    val db = Firebase.firestore
    val student = hashMapOf(
        "studentId" to studentId,
        "name" to name,
    )
    db.collection("students")
        .document(studentId)
        .set(student)
        .addOnSuccessListener { Log.d("Firestore", "Student info saved") }
        .addOnFailureListener { e -> Log.e("Firestore", "Save failed", e) }
}
