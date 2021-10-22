package com.alpha.shoplex.model.extra

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import com.alpha.shoplex.R


class CheckoutViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    var isPagingEnabled: Boolean = false

    @SuppressLint(R.string.ClickableViewAccessibility.toString())
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return isPagingEnabled && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return isPagingEnabled && super.onInterceptTouchEvent(event)
    }
}