package com.nickolay.delme

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nickolay.android2ver2.R


class MyFireBaseMessaging : FirebaseMessagingService() {
    private var messageId = 0

    override fun onMessageReceived(remoteMessage: RemoteMessage) {


        val title = if (remoteMessage.notification == null)
                "Push Message"
            else {
                Log.d("myLOG", remoteMessage.notification!!.body!!)
                remoteMessage.notification!!.title }

        val text = remoteMessage.notification!!.body
        // создать нотификацию
        val builder = NotificationCompat.Builder(this, "2")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(messageId++, builder.build())
    }

    override fun onNewToken(token: String) {
        // Если надо посылать сообщения этому экземпляру приложения
        // или управлять подписками приложения на стороне сервера,
        // сохраните этот токен в базе данных. отправьте этот токен вашему
        Log.d("myLOG", "Token " + token)
        //sendRegistrationToServer(token)
    }

    //private fun sendRegistrationToServer(token: String) {}

}
