package com.ecocollect.app.data.repository

import com.ecocollect.app.data.model.User
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    
    val currentUser: FirebaseUser? get() = firebaseAuth.currentUser
    
    fun isUserLoggedIn(): Boolean = currentUser != null
    
    fun getCurrentUserFlow(): Flow<FirebaseUser?> = flow {
        emit(currentUser)
    }
    
    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                val user = createOrUpdateUser(firebaseUser, account)
                Result.success(user)
            } else {
                Result.failure(Exception("Authentication failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                val user = getUserFromFirestore(firebaseUser.uid)
                    ?: createUserInFirestore(firebaseUser)
                Result.success(user)
            } else {
                Result.failure(Exception("Authentication failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signUpWithEmail(email: String, password: String, name: String): Result<User> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                val user = User(
                    uid = firebaseUser.uid,
                    name = name,
                    email = email,
                    createdAt = Timestamp.now()
                )
                saveUserToFirestore(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Sign up failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signOut() {
        firebaseAuth.signOut()
    }
    
    suspend fun deleteAccount(): Result<Unit> {
        return try {
            val user = currentUser
            if (user != null) {
                // Delete user data from Firestore
                firestore.collection("users").document(user.uid).delete().await()
                // Delete Firebase Auth account
                user.delete().await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("No user logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun createOrUpdateUser(
        firebaseUser: FirebaseUser,
        googleAccount: GoogleSignInAccount
    ): User {
        val existingUser = getUserFromFirestore(firebaseUser.uid)
        
        return if (existingUser != null) {
            // Update existing user
            val updatedUser = existingUser.copy(
                name = googleAccount.displayName ?: existingUser.name,
                email = googleAccount.email ?: existingUser.email,
                profileImageUrl = googleAccount.photoUrl?.toString()
            )
            saveUserToFirestore(updatedUser)
            updatedUser
        } else {
            // Create new user
            val newUser = User(
                uid = firebaseUser.uid,
                name = googleAccount.displayName ?: "",
                email = googleAccount.email ?: "",
                profileImageUrl = googleAccount.photoUrl?.toString(),
                createdAt = Timestamp.now()
            )
            saveUserToFirestore(newUser)
            newUser
        }
    }
    
    private suspend fun createUserInFirestore(firebaseUser: FirebaseUser): User {
        val user = User(
            uid = firebaseUser.uid,
            name = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: "",
            profileImageUrl = firebaseUser.photoUrl?.toString(),
            createdAt = Timestamp.now()
        )
        saveUserToFirestore(user)
        return user
    }
    
    private suspend fun getUserFromFirestore(uid: String): User? {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            document.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    private suspend fun saveUserToFirestore(user: User) {
        firestore.collection("users").document(user.uid).set(user).await()
    }
}
