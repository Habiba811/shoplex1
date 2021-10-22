package com.alpha.shoplex.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.alpha.shoplex.R
import com.alpha.shoplex.model.enumurations.AuthType
import com.alpha.shoplex.model.extra.FirebaseReferences
import com.alpha.shoplex.model.extra.UserInfo
import com.alpha.shoplex.model.firebase.AuthDBModel
import com.alpha.shoplex.model.interfaces.AuthListener
import com.alpha.shoplex.model.pojo.User
import com.alpha.shoplex.room.data.ShopLexDataBase
import com.alpha.shoplex.view.activities.auth.AuthActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AuthVM(val context: Context) : ViewModel(), AuthListener {
    var user: MutableLiveData<User> = MutableLiveData()
    var password: MutableLiveData<String> = MutableLiveData()

    var isLoginBtnPressed: MutableLiveData<Boolean> = MutableLiveData()
    var isSignupBtnPressed: MutableLiveData<Boolean> = MutableLiveData()

    var isLoginValid: MutableLiveData<Boolean> = MutableLiveData()
    var isSignupValid: MutableLiveData<Boolean> = MutableLiveData()

    private var userDBModel: AuthDBModel

    private lateinit var imgTask: StorageTask<UploadTask.TaskSnapshot>
    var userImgUri: Uri? = null

    init {
        this.user.value = User(authType = UserInfo.authType)
        this.password.value = ""
        this.userDBModel = AuthDBModel(this, context)
    }

    fun login(authType: AuthType, accessToken: String? = null) {
        when (authType) {
            AuthType.Email -> userDBModel.loginWithEmail(user.value!!.email, password.value!!)
            AuthType.Facebook -> userDBModel.loginWithFacebook(accessToken!!)
            AuthType.Google -> userDBModel.loginWithGoogle(accessToken!!)
        }
    }

    fun createAccount() {
        val ref: DocumentReference = FirebaseReferences.usersRef.document()
        addImage(Uri.parse(user.value!!.image), ref.id)
        Firebase.auth.fetchSignInMethodsForEmail(user.value!!.email).addOnCompleteListener {
            if (it.isSuccessful && it.result?.signInMethods?.size == 0) {
                userDBModel.createEmailAccount(user.value!!, password.value!!, ref)
            } else {
                imgTask.cancel()
                FirebaseReferences.imagesUserRef.child(ref.id).delete()
                onUserExists()
            }
        }
    }

    private fun addImage(uri: Uri, userId: String) {
        val imgRef: StorageReference = FirebaseReferences.imagesUserRef.child(userId)
        imgTask = imgRef.putFile(uri).addOnSuccessListener { _ ->
            imgRef.downloadUrl.addOnSuccessListener { uri ->
                FirebaseReferences.usersRef.document(userId).get().addOnSuccessListener {
                    if (it.exists()) {
                        FirebaseReferences.usersRef.document(userId).update("image", uri.toString())
                        val profileUpdates = userProfileChangeRequest {
                            photoUri = uri
                        }
                        if(userImgUri != null){
                            UserInfo.image = uri.toString()
                            UserInfo.saveUserInfo(context)
                        }
                        Firebase.auth.currentUser.updateProfile(profileUpdates)
                    }
                }
            }
        }
    }

    fun updateCurrentAccount() {
        if (!user.value?.userID.isNullOrEmpty()) {

            if(userImgUri != null)
                addImage(userImgUri!!, user.value!!.userID)

            Firebase.firestore.collection("Users").document(user.value!!.userID).set(user.value!!)
                .addOnSuccessListener {
                    Toast.makeText(
                        context,
                        context.getString(R.string.UpdateAccount),
                        Toast.LENGTH_SHORT
                    ).show()

                    UserInfo.name = user.value!!.name
                    UserInfo.location = user.value!!.location
                    UserInfo.address = user.value!!.address
                    UserInfo.phone = user.value!!.phone
                    UserInfo.saveUserInfo(context)

                    (context as AppCompatActivity).finish()
                }
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onAddNewUser(context: Context, user: User?) {
        super.onAddNewUser(context, user)
        if (user != null) {
            UserInfo.saveUserInfo(context)
            (context as AppCompatActivity).finish()
        }
    }

    override fun onLoginSuccess(context: Context, user: User) {
        super.onLoginSuccess(context, user)
        Toast.makeText(context, context.getString(R.string.login_success), Toast.LENGTH_SHORT)
            .show()
        (context as AuthActivity).finish()
    }

    override fun onLoginFailed() {
        super.onLoginFailed()
        Toast.makeText(context, context.getString(R.string.login_fail), Toast.LENGTH_SHORT).show()
        UserInfo.clear()
    }

    private fun onUserExists() {
        Toast.makeText(context, context.getString(R.string.emailExist), Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun logout(context: Context) {
            UserInfo.saveNotification(context, false)
            Firebase.auth.signOut()
            FirebaseAuth.getInstance().signOut()
            LoginManager.getInstance().logOut()
            UserInfo.clear()
            UserInfo.clearSharedPref(context)
            GlobalScope.launch {
                ShopLexDataBase.getDatabase(context).clearAllTables()
            }
        }
    }
}