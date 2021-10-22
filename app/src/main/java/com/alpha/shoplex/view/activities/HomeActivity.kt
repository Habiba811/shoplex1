package com.alpha.shoplex.view.activities

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.droidnet.DroidListener
import com.droidnet.DroidNet
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.ActivityHomeBinding
import com.alpha.shoplex.model.extra.UserInfo

class HomeActivity : AppCompatActivity(), DroidListener {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        if (UserInfo.lang != this.resources.configuration.locale.language)
            UserInfo.setLocale(UserInfo.lang, this)
        super.onCreate(savedInstanceState)
        if(UserInfo.userID.isNullOrEmpty())
            UserInfo.readUserInfo(this)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DroidNet.getInstance().addInternetConnectivityListener(this)

        bottomNavigationView = binding.bottomNavigation
        navController = findNavController(R.id.nav_host_fragment)
        bottomNavigationView.setupWithNavController(navController)
    }

    override fun onBackPressed() {

        if (bottomNavigationView.selectedItemId == R.id.homeFragment2) {
            finishAffinity()
        } else {
            findNavController(R.id.nav_host_fragment).popBackStack()
        }
    }

    override fun onInternetConnectivityChanged(isConnected: Boolean) {
        if (isConnected) {
            //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
        } else {
            //Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
        }
    }
}
