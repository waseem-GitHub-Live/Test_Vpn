package com.xilli.testvpn.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.xilli.testvpn.R

class MyVpnService: VpnService() {
    private lateinit var vpnInterface: ParcelFileDescriptor
    private val NOTIFICATION_ID = 1
    private  val CHANNEL_ID = "vpn_channel"


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Configure VPN parameters here
        val builder = Builder()
            .setSession("MyVPNService")
            .addAddress("10.0.0.1", 32) // Example IP address and prefix length
            .addDnsServer("8.8.8.8")
        // Add more configurations as needed
        vpnInterface = builder.establish()!!

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            startForeground(NOTIFICATION_ID, createNotification())
        }

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "VPN Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        // Create and return a notification for the foreground service
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("VPN Service")
            .setContentText("VPN is running")
            .setSmallIcon(R.drawable.ic_notifications)
            .build()

        return notification
    }
}