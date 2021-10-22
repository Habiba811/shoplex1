package com.alpha.shoplex.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.alpha.shoplex.model.interfaces.PaymentListener
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class PaymentMethodVM(val context: Context, val fragment: Fragment, val listener: PaymentListener) : ViewModel() {

    private lateinit var paymentSheet: PaymentSheet
    private lateinit var paymentIntentClientSecret: String

    init {
        configPaymentMethod()
    }

    private fun configPaymentMethod() {
        PaymentConfiguration.init(context, STRIPE_PUBLISHABLE_KEY)

        paymentSheet = PaymentSheet(fragment) { result ->
            onPaymentSheetResult(result)
        }
    }

    // Payment
    internal fun pay(price: Float) {
        val amount: Double = String.format("%.2f", price * 100).toDouble()

        if(amount < 10){
            listener.onMinimumPrice()
            return
        }

        val payMap: MutableMap<String, Any> = HashMap()
        val itemMap: MutableMap<String, Any> = HashMap()
        val itemList: MutableList<Map<String, Any>> = ArrayList()
        payMap["currency"] = "egp"
        itemMap["amount"] = amount
        itemList.add(itemMap)
        payMap["items"] = itemList

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val json = Gson().toJson(payMap)
        val body = json.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(BACKEND_URL + "checkout")
            .post(body)
            .build()

        OkHttpClient().newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    (context as AppCompatActivity).runOnUiThread {
                        listener.onPaymentFailedToLoad()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        (context as AppCompatActivity).runOnUiThread {
                            listener.onPaymentFailedToLoad()
                        }
                    } else {
                        val responseData = response.body?.string()
                        val responseJson =
                            responseData?.let { JSONObject(it) } ?: JSONObject()

                        paymentIntentClientSecret = responseJson.getString("clientSecret")

                        (context as AppCompatActivity).runOnUiThread {
                            presentPaymentSheet()
                        }
                    }
                }
            }
            )
    }

    private fun presentPaymentSheet() {
        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret
        )
    }

    private fun onPaymentSheetResult(
        paymentSheetResult: PaymentSheetResult
    ) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                Toast.makeText(context, "Payment Canceled", Toast.LENGTH_LONG).show()
            }
            is PaymentSheetResult.Failed -> {
                Toast.makeText(
                    context,
                    "Payment Failed.",
                    Toast.LENGTH_LONG
                ).show()
            }
            is PaymentSheetResult.Completed -> {
                Toast.makeText(context, "Payment Complete", Toast.LENGTH_LONG).show()
                listener.onPaymentComplete()
            }
        }
    }

    companion object {
        internal const val BACKEND_URL = "https://evening-sands-34009.herokuapp.com/"
        private const val STRIPE_PUBLISHABLE_KEY =
            "STRIPE_KEY"
    }
}