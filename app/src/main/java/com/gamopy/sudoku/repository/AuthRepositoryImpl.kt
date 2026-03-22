package com.gamopy.sudoku.repository

import android.net.Uri
import com.gamopy.sudoku.data.Resource
import com.gamopy.sudoku.dto.Signup
import com.gamopy.sudoku.dto.UserProfile
import com.gamopy.sudoku.firebase.await
import com.gamopy.sudoku.firebase.awaitSuccess
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private var firebaseAuth: FirebaseAuth,
    private var firebaseFirestore: FirebaseFirestore,
    private var storageReference: StorageReference
) : AuthRepository {
    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    /**
     * @param email
     * email of user
     * @param password
     * password of user
     * @return
     * Resource<FirebaseUser> ,return the Firebase user of Success type
     *
     */
    override suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result.user!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    /**
     * @param name
     * name of user
     * @param email
     * email of user
     * @param password
     * password of user
     * @return Resource
     * return firebase user of success type from the Resource class
     * @see Resource
     */
    override suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Resource<FirebaseUser> {

        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result?.user?.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(name).build()
            )?.await()

            val user = Signup(profileImage = null).apply {
                this.name = name
                this.email = email
                this.user = result.user?.uid!!
            }
            firebaseFirestore.collection("Users").document(currentUser!!.uid).set(user)
                .awaitSuccess()

            Resource.Success(result.user!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    /**
     * Fetches the profile of user from firebase
     */
    override suspend fun fetchProfileData(): Resource<UserProfile> {
        return try {
            val result = firebaseFirestore.collection("Users").whereEqualTo(
                "user",
                currentUser?.uid
            ).get().awaitSuccess()
            val user = result.toObjects(UserProfile::class.java)
            Resource.Success(user[0])
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun updateProfileImage(uri: Uri): Resource<Uri> {
        return try {
            val fileName = uri.pathSegments?.last()
            val ref = storageReference.child("file/$fileName")
            //val uploadTask = ref.putFile(uri).awaitSuccess()
            val url = ref.downloadUrl.awaitSuccess()
            val docRef = firebaseFirestore.collection("Users").document(currentUser!!.uid)
            docRef.update("profileImage", url.toString()).awaitSuccess()
            Resource.Success(url)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun resetPassword(email: String): Resource<Nothing> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Resource.Empty
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }


    override fun logout() {
        firebaseAuth.signOut()
    }
}