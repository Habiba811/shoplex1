package com.alpha.shoplex.view.activities

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.ActivityDetailsBinding
import com.alpha.shoplex.model.adapter.PagerAdapter
import com.alpha.shoplex.model.extra.UserInfo
import com.alpha.shoplex.viewmodel.DetailsVM
import com.alpha.shoplex.viewmodel.ProductsVM

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding

    lateinit var productsVM: ProductsVM
    lateinit var detailsVM: DetailsVM

    override fun onCreate(savedInstanceState: Bundle?) {
        if (UserInfo.lang != this.resources.configuration.locale.language)
            UserInfo.setLocale(UserInfo.lang, this)
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productsVM = ViewModelProvider(this).get(ProductsVM::class.java)
        detailsVM = ViewModelProvider(this).get(DetailsVM::class.java)

        productsVM.productID.value = intent.getStringExtra(getString(R.string.productId))
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.viewPager.adapter = PagerAdapter(supportFragmentManager, this)

        if (intent.hasExtra("isNotification"))
            (this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(100)

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
                title = tab.text
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}