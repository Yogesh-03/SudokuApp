package com.gamopy.sudoku.firebase

import android.annotation.SuppressLint
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseSingleton {
    companion object{
        @SuppressLint("StaticFieldLeak")
        private  var db:FirebaseFirestore? = null

        fun getDb(context: android.content.Context):FirebaseFirestore{
            if (db == null){
                db = FirebaseFirestore.getInstance()
            }
            return db as FirebaseFirestore
        }
    }
}