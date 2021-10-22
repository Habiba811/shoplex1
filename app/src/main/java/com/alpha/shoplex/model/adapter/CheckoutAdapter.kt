package com.alpha.shoplex.model.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.alpha.shoplex.R
import com.alpha.shoplex.view.fragments.DeliveryFragment
import com.alpha.shoplex.view.fragments.PaymentFragment
import com.alpha.shoplex.view.fragments.SummaryFragment

class CheckoutAdapter(val context: Context, fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> DeliveryFragment()
            1 -> PaymentFragment()
            else -> SummaryFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> context.getString(R.string.Delivery)
            1 -> context.getString(R.string.Payment)
            else -> context.getString(R.string.Summary)
        }
    }
}