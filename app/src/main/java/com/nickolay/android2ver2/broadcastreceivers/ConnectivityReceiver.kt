package com.nickolay.delme

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.core.app.NotificationCompat
import com.nickolay.android2ver2.R


class ConnectivityReceiver: BroadcastReceiver() {
    private var messageId = 5000

    override fun onReceive(context: Context, intent: Intent) {

        val activeNetwork =
            (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                .activeNetworkInfo


        if (activeNetwork == null || !activeNetwork.isConnected){
        val builder = NotificationCompat.Builder(context, "2")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Данные не поступают")
            .setContentText("Отсутствует подключение к сети")
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(messageId, builder.build())
        }
    }
}