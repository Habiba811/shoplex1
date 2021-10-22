package com.alpha.shoplex.view.activities.auth

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.LoginTabFragmentBinding
import com.alpha.shoplex.model.enumurations.AuthType
import com.alpha.shoplex.model.extra.ArchLifecycleApp
import com.alpha.shoplex.viewmodel.AuthVM
import org.json.JSONException
import java.util.regex.Matcher
import java.util.regex.Pattern

class LoginTabFragment : Fragment() {

    private lateinit var binding: LoginTabFragmentBinding
    private lateinit var authVM: AuthVM

    private lateinit var startGoogleLogin: ActivityResultLauncher<Intent>

    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startGoogleLogin =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    val account = task.getResult(ApiException::class.java)
                    if (task.isSuccessful && account != null) {
                        Firebase.auth.fetchSignInMethodsForEmail(account.email!!)
                            .addOnCompleteListener {
                                if (it.isSuccessful && (it.result.signInMethods.isNullOrEmpty() || it.result.signInMethods?.first() == "google.com")) {
                                    authVM.login(AuthType.Google, account.idToken!!)
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        getText(R.string.emailExist),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getText(R.string.failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        authVM = (activity as AuthActivity).authVM
        binding = LoginTabFragmentBinding.inflate(inflater, container, false)
        binding.userData = authVM
        binding.tvForgetPass.setOnClickListener {
            openDialog()
        }

        authVM.isLoginBtnPressed.observe(requireActivity(), {
            if (it) {
                validate()
            }
        })

        binding.btnGoogle.setOnClickListener {
            if (ArchLifecycleApp.isInternetConnected) {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                val signInIntent = GoogleSignIn.getClient(requireActivity(), gso).signInIntent


                startGoogleLogin.launch(signInIntent)
            } else {
                Toast.makeText(
                    requireContext(),
                    getText(R.string.NoInternetConnection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnFace.setOnClickListener {
            if (ArchLifecycleApp.isInternetConnected) {
                loginWithFacebook()
            } else {
                Toast.makeText(
                    requireContext(),
                    getText(R.string.NoInternetConnection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        onEditTextChanged()

        return binding.root
    }

    private fun loginWithFacebook() {
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().logInWithReadPermissions(this, setOf("public_profile", "email"))
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult?) {
                    loginResult?.let {
                        GraphRequest.newMeRequest(it.accessToken) { _, response ->
                            val json = response.jsonObject
                            try {
                                if (json != null) {
                                    val email = json.getString("email")
                                    Firebase.auth.fetchSignInMethodsForEmail(email)
                                        .addOnCompleteListener { signInResponse ->
                                            if (signInResponse.isSuccessful && (signInResponse.result.signInMethods.isNullOrEmpty() || signInResponse.result.signInMethods?.first() == "facebook.com")) {
                                                authVM.login(
                                                    AuthType.Facebook,
                                                    loginResult.accessToken.token
                                                )
                                            } else {
                                                Toast.makeText(
                                                    requireContext(),
                                                    getString(R.string.emailExist),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                }
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.errorAuth),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }.apply {
                            val parameters = Bundle()
                            parameters.putString("fields", "id,name,email")
                            this.parameters = parameters
                            this.executeAsync()
                        }
                    }
                }

                override fun onCancel() {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.cancel),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(error: FacebookException?) {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.errorAuth),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }

    private fun validate() {
        when {
            binding.edEmail.length() == 0 -> binding.tiEmail.error = getString(R.string.Required)
            !(isEmailValid(binding.edEmail.text.toString())) -> binding.tiEmail.error =
                getString(R.string.require_email)
            binding.edPassword.length() == 0 -> binding.tiPassword.error =
                getString(R.string.Required)
            binding.edPassword.length() < 8 -> binding.tiPassword.error =
                getString(R.string.min_password_err)

            else -> authVM.isLoginValid.value = true
        }
        authVM.isLoginBtnPressed.value = false
    }

    //Email Validation
    private fun isEmailValid(email: String?): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun onEditTextChanged() {
        binding.edEmail.addTextChangedListener {
            binding.tiEmail.error = null
        }
        binding.edPassword.addTextChangedListener {
            binding.tiPassword.error = null
        }
    }

    //openDialog
    private fun openDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.forgotPassword))
        val view: View = layoutInflater.inflate(R.layout.dialog_forget_password, null)
        builder.setView(view)
        val email: EditText = view.findViewById(R.id.edEmailDialog)
        builder.setPositiveButton(getString(R.string.reset)) { _, _ ->
            when {
                email.text.toString().isEmpty() -> email.error = getString(R.string.Required)
                else -> forgetPassword(email.text.toString())
            }
        }

        builder.setNegativeButton(getString(R.string.close_dialog)) { _, _ ->

        }
        builder.show()
    }


    //Forget Password
    private fun forgetPassword(email: String) {
        Firebase.auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snackbar = Snackbar.make(
                    binding.root,
                    getString(R.string.email_send),
                    Snackbar.LENGTH_LONG
                )
                val sbView: View = snackbar.view
                sbView.setBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.blueshop
                    )
                )
                snackbar.show()


            } else {
                val snackbar = Snackbar.make(
                    binding.root,
                    getString(R.string.require_email),
                    Snackbar.LENGTH_LONG
                )
                val sbView: View = snackbar.view
                sbView.setBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.blueshop
                    )
                )
                snackbar.show()

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}