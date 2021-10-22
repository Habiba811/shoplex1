package com.alpha.shoplex.model.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.alpha.shoplex.R
import com.alpha.shoplex.view.activities.DetailsActivity
import com.alpha.shoplex.view.activities.OrderActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {
    var NOTIFICATION_CHANNEL_ID =R.string.notchannelid.toString()
    val NOTIFICATION_ID = 100
    private var productID: String? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data[this.getString(R.string.title)]
            val body = remoteMessage.data[this.getString(R.string.body)]
            if (remoteMessage.data.containsKey("productID"))
                productID = remoteMessage.data["productID"]
            showNotification(applicationContext, title, body)
        } else {
            val title = remoteMessage.notification!!.title
            val body = remoteMessage.notification!!.body
            showNotification(applicationContext, title, body)
        }
    }

    fun showNotification(
        context: Context,
        title: String?,
        message: String?
    ) {
        val notification: Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.shoplex_logo)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setWhen(System.currentTimeMillis())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title).apply {
                    if(title?.contains("Delivered", true) == true) {
                        val ii = Intent(context, OrderActivity::class.java)
                        ii.data =
                            Uri.parse(context.getString(R.string.custom) + System.currentTimeMillis())
                        ii.action =
                            context.getString(R.string.actionstring) + System.currentTimeMillis()
                        ii.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP

                        ii.putExtra("isNotification", true)
                        ii.putExtra("productID", productID)
                        val pi =
                            PendingIntent.getActivity(
                                context,
                                0,
                                ii,
                                PendingIntent.FLAG_UPDATE_CURRENT
                            )
                        this.addAction(R.drawable.ic_star, context.getString(R.string.Review), pi)
                    } else if(title?.contains("Discount", true) == true){
                        val ii = Intent(context, DetailsActivity::class.java)
                        ii.data =
                            Uri.parse(context.getString(R.string.custom) + System.currentTimeMillis())
                        ii.action =
                            context.getString(R.string.actionstring) + System.currentTimeMillis()
                        ii.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP

                        ii.putExtra("isNotification", true)
                        ii.putExtra("productID", productID)
                        val pi =
                            PendingIntent.getActivity(
                                context,
                                0,
                                ii,
                                PendingIntent.FLAG_UPDATE_CURRENT
                            )
                        this.addAction(R.drawable.ic_star, context.getString(R.string.buy), pi)
                    }
                }.build()

            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                title,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
            notificationManager.notify(NOTIFICATION_ID, notification)
        } else {
            notification = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.shoplex_logo)
                .setAutoCancel(true)
                .setContentText(message)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title).apply {
                    if(title?.contains("Delivered", true) == true) {
                        val ii = Intent(context, OrderActivity::class.java)
                        ii.data =
                            Uri.parse(context.getString(R.string.custom) + System.currentTimeMillis())
                        ii.action =
                            context.getString(R.string.actionstring) + System.currentTimeMillis()
                        ii.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP

                        ii.putExtra("isNotification", true)
                        ii.putExtra("productID", productID)
                        val pi =
                            PendingIntent.getActivity(
                                context,
                                0,
                                ii,
                                PendingIntent.FLAG_UPDATE_CURRENT
                            )
                        this.addAction(R.drawable.ic_star, context.getString(R.string.Review), pi)
                    } else if(title?.contains("Discount", true) == true){
                        val ii = Intent(context, DetailsActivity::class.java)
                        ii.data =
                            Uri.parse(context.getString(R.string.custom) + System.currentTimeMillis())
                        ii.action =
                            context.getString(R.string.actionstring) + System.currentTimeMillis()
                        ii.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP

                        ii.putExtra("isNotification", true)
                        ii.putExtra("productID", productID)
                        val pi =
                            PendingIntent.getActivity(
                                context,
                                0,
                                ii,
                                PendingIntent.FLAG_UPDATE_CURRENT
                            )
                        this.addAction(R.drawable.ic_star, context.getString(R.string.buy), pi)
                    }
                }.build()


            val notificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }
}