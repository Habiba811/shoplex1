package com.alpha.shoplex.model.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.alpha.shoplex.R
import com.alpha.shoplex.view.fragments.DetailsFragment
import com.alpha.shoplex.view.fragments.ReviewFragment

class PagerAdapter(fm: FragmentManager, val context: Context) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> DetailsFragment()
            else -> ReviewFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> this.context.getString(R.string.Products)
            else -> this.context.getString(R.string.Reviews)
        }
    }
}