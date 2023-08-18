package com.xilli.testvpn

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.VpnService
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.xilli.testvpn.databinding.ActivityMainBinding
import com.xilli.testvpn.services.MyVpnService
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList
import java.util.Arrays
import java.util.concurrent.Executors
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private var isVpnStarted = false
    private var binding : ActivityMainBinding? = null
    private val VPN_REQUEST_CODE = 123
    private val REQUIRED_SDK_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.INTERNET,
        Manifest.permission.CHANGE_NETWORK_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        //Manifest.permission.ACCESS_COARSE_LOCATION,
        //Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BIND_VPN_SERVICE
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        checkPermissions()
        clicklistener()

    }

    private fun clicklistener() {
        binding?.shapeableImageView?.setOnClickListener {
            toggleVpn()
        }
    }

    private fun toggleVpn() {
        if (isVpnStarted) {
            stopVpn()
        } else {
            startVpn()
        }
    }

    private fun startVpn() {
        val intent = VpnService.prepare(this)
        if (intent != null) {
            startActivityForResult(intent, VPN_REQUEST_CODE)
        } else {
            startVpnService()
        }
    }

    private fun startVpnService() {
        val vpnIntent = Intent(this, MyVpnService::class.java)
        ContextCompat.startForegroundService(this, vpnIntent)
        isVpnStarted = true
    }

    private fun stopVpn() {
        val vpnIntent = Intent(this, MyVpnService::class.java)
        stopService(vpnIntent)
        isVpnStarted = false
    }

    private fun checkPermissions() {
        val REQUEST_CODE_ASK_PERMISSIONS = 1
        val missingPermissions: MutableList<String> = ArrayList()
        // check all required dynamic permissions
        for (permission in REQUIRED_SDK_PERMISSIONS) {
            val result = ContextCompat.checkSelfPermission(this, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
            }
        }
        if (missingPermissions.isNotEmpty()) {
            // request all missing permissions
            val permissions = missingPermissions.toTypedArray()
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS)
        } else {
            val grantResults = IntArray(REQUIRED_SDK_PERMISSIONS.size)
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED)
            onRequestPermissionsResult(
                REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                grantResults
            )
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VPN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (!isVpnStarted) {
                    startVpn()
                }
                isVpnStarted = true
            } else {
                if (isVpnStarted) {
                    stopVpn()
                }
                isVpnStarted = false
            }
        }
    }


}