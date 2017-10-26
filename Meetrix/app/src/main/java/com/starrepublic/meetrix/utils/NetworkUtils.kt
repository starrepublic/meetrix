package com.starrepublic.meetrix.utils

import android.content.Context
import android.net.wifi.WifiManager

class NetworkUtils(context: Context) {
    private val wifiManager: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    var isWifiEnabled: Boolean
        get() = wifiManager.isWifiEnabled
        set(value) {
            wifiManager.isWifiEnabled = value
        }
}