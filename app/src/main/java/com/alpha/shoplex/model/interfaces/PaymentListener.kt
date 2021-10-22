package com.alpha.shoplex.model.interfaces

interface PaymentListener {
    fun onPaymentComplete()
    fun onPaymentFailedToLoad()
    fun onMinimumPrice()
    fun onPaymentCanceledOrFailed()
}