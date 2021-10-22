package com.alpha.shoplex.model.firebase

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.alpha.shoplex.R
import com.alpha.shoplex.model.enumurations.AuthType
import com.alpha.shoplex.model.extra.FirebaseReferences
import com.alpha.shoplex.model.interfaces.AuthListener
import com.alpha.shoplex.model.pojo.Location
import com.alpha.shoplex.model.pojo.User

class AuthDBModel(val listener: AuthListener, val context: Context) {
    fun loginWithEmail(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    getUserByMail(email, AuthType.Email)
                } else {
                    listener.onLoginFailed()
                }
            }
    }

    fun loginWithFacebook(accessToken: String) {
        val authCredential: AuthCredential = FacebookAuthProvider.getCredential(accessToken)
        Firebase.auth.signInWithCredential(authCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) run {
                val user: FirebaseUser = Firebase.auth.currentUser
                getUserByMail(user.email, AuthType.Facebook)
            } else {
                Toast.makeText(context, context.getString(R.string.errorRegister), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser = Firebase.auth.currentUser
                    getUserByMail(user.email, AuthType.Google)
                } else {
                    Toast.makeText(context, context.getString(R.string.errorRegister), Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    fun createEmailAccount(user: User, password: String, ref: DocumentReference) {
        user.userID = ref.id
        user.image = ""
        Firebase.auth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    addNewUser(ref, user)
                } else {
                    listener.onAddNewUser(context, null)
                    FirebaseReferences.imagesUserRef.child(ref.id).delete()
                    Toast.makeText(context, context.getString(R.string.errorAuth), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addNewUser(ref: DocumentReference, user: User) {
        ref.set(user).addOnSuccessListener {
            listener.onAddNewUser(context, user)

        }.addOnFailureListener {
            FirebaseReferences.imagesUserRef.child(ref.id).delete()
            listener.onAddNewUser(context, null)
        }
    }

    private fun addSocialUser(authType: AuthType) {
        val ref: DocumentReference = FirebaseReferences.usersRef.document()
        val currentUser: FirebaseUser = Firebase.auth.currentUser

        val user = User(
            ref.id,
            currentUser.displayName,
            currentUser.email,
            Location(0.0, 0.0),
            "Address",
            currentUser.phoneNumber ?: "",
            currentUser.photoUrl.toString(),
            authType
        )

        ref.set(user).addOnSuccessListener {
            listener.onAddNewUser(context, user)
            Toast.makeText(context, context.getString(R.string.Success), Toast.LENGTH_SHORT).show()

        }.addOnFailureListener {
            listener.onAddNewUser(context, null)
            Toast.makeText(context, context.getString(R.string.failed), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getUserByMail(userEmail: String, authType: AuthType) {
        FirebaseReferences.usersRef.whereEqualTo("email", userEmail)
            .whereEqualTo("authType", authType.name).get()
            .addOnSuccessListener {
                val user: User?
                if(it.documents.count() > 0){
                    user = it.documents[0].toObject()!!
                    listener.onLoginSuccess(context, user)
                } else if (it.documents.count() == 0 && authType != AuthType.Email){
                    addSocialUser(authType)
                } else {
                    listener.onLoginFailed()
                }
            }.addOnFailureListener {
                listener.onLoginFailed()
            }
    }
}