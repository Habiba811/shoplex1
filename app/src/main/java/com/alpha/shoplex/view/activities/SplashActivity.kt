package com.alpha.shoplex.view.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.ActivitySplashBinding
import com.alpha.shoplex.model.extra.UserInfo
import com.alpha.shoplex.model.interfaces.ProductsListener
import com.alpha.shoplex.viewmodel.AuthVM
import java.util.*

class SplashActivity : AppCompatActivity(), ProductsListener {
    private val splashDuration = 4000L
    private lateinit var binding: ActivitySplashBinding
    private lateinit var topAnimation: Animation
    private lateinit var bottomAnimation: Animation
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UserInfo.readUserInfo(applicationContext)
        if(UserInfo.lang != "en")
            setLocale(UserInfo.lang)
        currentUser = Firebase.auth.currentUser
        if(currentUser != null){
            currentUser!!.reload()
        }

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)
        binding.imgSplash.animation = topAnimation
        binding.tvShoplexSplash.animation = bottomAnimation

        Handler().postDelayed({
            currentUser = Firebase.auth.currentUser

            if (currentUser == null) {
                AuthVM.logout(this)
            } else if(!UserInfo.userID.isNullOrEmpty()){
                UserInfo.saveToRecentVisits()
            }

            val intent = if (UserInfo.isFirstTime(applicationContext))Intent(this, DescriptionActivity::class.java)
            else Intent(this, HomeActivity::class.java)

            startActivity(intent)
            finish()

        }, splashDuration)
    }

    fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.createConfigurationContext(config)
        baseContext.resources.updateConfiguration(
            config,
            baseContext.resources.displayMetrics
        )
    }
}