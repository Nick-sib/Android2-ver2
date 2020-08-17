package com.nickolay.delme

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.nickolay.android2ver2.R


class BatteryReceiver: BroadcastReceiver() {
    private var messageId = 1000

    override fun onReceive(context: Context, intent: Intent) {


        val builder = NotificationCompat.Builder(context, "2")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Данные не поступают")
            .setContentText("Низкий заряд батареи")
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(messageId, builder.build())
//логично здесь отключить сервисы и получение данных
    }


}