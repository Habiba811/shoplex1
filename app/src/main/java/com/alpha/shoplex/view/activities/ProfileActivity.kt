package com.alpha.shoplex.view.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.ActivityProfileBinding
import com.alpha.shoplex.model.enumurations.LocationAction
import com.alpha.shoplex.model.extra.ArchLifecycleApp
import com.alpha.shoplex.model.extra.UserInfo
import com.alpha.shoplex.model.pojo.Location
import com.alpha.shoplex.model.pojo.User
import com.alpha.shoplex.viewmodel.AuthVM
import com.alpha.shoplex.viewmodel.AuthVMFactory
import java.util.regex.Matcher
import java.util.regex.Pattern

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    private lateinit var authVM: AuthVM

    override fun onCreate(savedInstanceState: Bundle?) {
        if (UserInfo.lang != this.resources.configuration.locale.language)
            UserInfo.setLocale(UserInfo.lang, this)
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authVM = ViewModelProvider(this, AuthVMFactory(this)).get(AuthVM::class.java).apply {
            this.user.value = User(
                UserInfo.userID!!,
                UserInfo.name,
                UserInfo.email,
                UserInfo.location,
                UserInfo.address,
                UserInfo.phone ?: "",
                UserInfo.image ?: "",
                UserInfo.authType
            )
        }

        val startImageChooser =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = it.data
                    if (data != null || data?.data != null) {
                        val uri = data.data
                        if (uri != null) {
                            authVM.userImgUri = uri
                            binding.imgUser.setImageURI(uri)
                        }
                    }
                }
            }

        val startMaps =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = it.data
                    if (data != null || data?.data != null) {
                        val location: Location? = data.getParcelableExtra(MapsActivity.LOCATION)
                        val address: String? = data.getStringExtra(MapsActivity.ADDRESS)
                        if (location != null && address != null) {
                            binding.tvLocation.text = address
                            authVM.user.value!!.address = address
                            authVM.user.value!!.location = location
                        }
                    }
                }
            }

        setSupportActionBar(binding.toolbarProfile)
        supportActionBar?.apply {
            title = getString(R.string.profile)
            // setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }

        Glide.with(this).load(UserInfo.image).error(R.drawable.init_img).into(binding.imgUser)

        binding.userInfo = authVM

        binding.btnSave.setOnClickListener {
            if (checkEditText()) {
                if(ArchLifecycleApp.isInternetConnected){
                    authVM.updateCurrentAccount()
                }else{
                    Snackbar.make(binding.root.rootView,R.string.NoInternetConnection,Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnLocation.setOnClickListener {
            startMaps.launch(Intent(this, MapsActivity::class.java).apply {
                this.putExtra(MapsActivity.LOCATION_ACTION, LocationAction.Add.name)
            })
        }

        binding.imgUser.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            intent.type = "image/*"

            startImageChooser.launch(intent)
        }

        onEditTextChanged()
    }

    private fun onEditTextChanged() {
        binding.edName.addTextChangedListener {
            binding.tiName.error = null
        }
        binding.edEmail.addTextChangedListener {
            binding.tiEmail.error = null
        }
        binding.edPhone.addTextChangedListener {
            binding.tiPhone.error = null
        }
        binding.tvLocation.addTextChangedListener {
            binding.tvLocation.error = null
        }
    }

    private fun checkEditText(): Boolean {
        when {
            binding.edName.length() == 0 -> binding.edName.error = getString(R.string.Required)
            binding.edName.length() < 5 -> binding.edName.error =
                getString(R.string.min_client_name_err)
            binding.edEmail.length() == 0 -> binding.edEmail.error = getString(R.string.Required)
            !(isEmailValid(binding.edEmail.text.toString())) -> binding.edEmail.error =
                getString(
                    R.string.require_email
                )

            binding.edPhone.text.toString().isEmpty() -> binding.tiPhone.error =
                getString(R.string.Required)

            !isValidMobile(binding.edPhone.text.toString()) -> binding.tiPhone.error =
                getString(R.string.enter_mobile)

            authVM.user.value?.address.isNullOrEmpty() || (authVM.user.value?.location?.latitude == 0.0 && authVM.user.value?.location?.longitude == 0.0) -> Toast.makeText(
                this,
                getString(R.string.choose_location),
                Toast.LENGTH_LONG
            ).show()

            authVM.user.value?.image.isNullOrEmpty() -> Toast.makeText(
                this,
                getString(R.string.choose_image),
                Toast.LENGTH_SHORT
            ).show()
            else -> return true
        }
        return false
    }

    private fun isEmailValid(email: String?): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun isValidMobile(phone: String): Boolean {
        return if (!Pattern.matches("[a-zA-Z]+", phone)) {
            phone.length in 12..13
        } else false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()

        return super.onOptionsItemSelected(item)
    }
}