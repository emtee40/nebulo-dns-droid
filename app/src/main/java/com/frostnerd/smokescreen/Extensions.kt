package com.frostnerd.smokescreen

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.frostnerd.smokescreen.util.preferences.AppSettings
import com.frostnerd.smokescreen.util.preferences.AppSettingsSharedPreferences
import com.frostnerd.smokescreen.util.preferences.fromSharedPreferences
import java.util.logging.Level

/*
 * Copyright (C) 2019 Daniel Wolf (Ch4t4r)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You can contact the developer at daniel.wolf@frostnerd.com.
 */

fun Context.registerReceiver(intentFilter: IntentFilter, receiver: (intent: Intent?) -> Unit): BroadcastReceiver {
    val actualReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            receiver(intent)
        }
    }
    this.registerReceiver(actualReceiver, intentFilter)
    return actualReceiver
}

fun Context.startForegroundServiceCompat(intent: Intent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(intent)
    } else startService(intent)
}

fun Context.registerReceiver(filteredActions: List<String>, receiver: (intent: Intent?) -> Unit): BroadcastReceiver {
    val filter = IntentFilter()
    for (filteredAction in filteredActions) {
        filter.addAction(filteredAction)
    }

    val actualReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            receiver(intent)
        }
    }
    this.registerReceiver(actualReceiver, filter)
    return actualReceiver
}

fun Context.registerLocalReceiver(intentFilter: IntentFilter, receiver: (intent: Intent?) -> Unit): BroadcastReceiver {
    val actualReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            receiver(intent)
        }
    }
    LocalBroadcastManager.getInstance(this).registerReceiver(actualReceiver, intentFilter)
    return actualReceiver
}

fun Context.registerLocalReceiver(
    filteredActions: List<String>,
    receiver: (intent: Intent?) -> Unit
): BroadcastReceiver {
    val filter = IntentFilter()
    for (filteredAction in filteredActions) {
        filter.addAction(filteredAction)
    }

    val actualReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            receiver(intent)
        }
    }
    LocalBroadcastManager.getInstance(this).registerReceiver(actualReceiver, filter)
    return actualReceiver
}

fun Context.unregisterLocalReceiver(receiver: BroadcastReceiver) {
    LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
}

fun Context.getPreferences(): AppSettingsSharedPreferences {
    return AppSettings.fromSharedPreferences(this)
}

fun Context.isAppBatteryOptimized(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false
    val pwrm = getSystemService(Context.POWER_SERVICE) as PowerManager
    return !pwrm.isIgnoringBatteryOptimizations(packageName)
}

fun Array<*>.toStringArray(): Array<String> {
    val stringArray = arrayOfNulls<String>(size)
    for ((index, value) in withIndex()) {
        stringArray[index] = value.toString()
    }
    return stringArray as Array<String>
}

fun IntArray.toStringArray(): Array<String> {
    val stringArray = arrayOfNulls<String>(size)
    for ((index, value) in withIndex()) {
        stringArray[index] = value.toString()
    }
    return stringArray as Array<String>
}

fun Activity.restart() {
    val intent = intent
        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
    finish()
    startActivity(intent)
}

fun Context.showEmailChooser(chooserTitle: String, subject: String, recipent: String, text: String) {
    val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", recipent, null))
    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    intent.putExtra(Intent.EXTRA_EMAIL, recipent)
    intent.putExtra(Intent.EXTRA_TEXT, text)
    startActivity(Intent.createChooser(intent, chooserTitle))
}

fun ConnectivityManager.isMobileNetwork(network: Network): Boolean {
    val capabilities = getNetworkCapabilities(network)
    return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
}

fun ConnectivityManager.isWifiNetwork(network: Network): Boolean {
    val capabilities = getNetworkCapabilities(network)
    return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
}

fun ConnectivityManager.isVpnNetwork(network: Network): Boolean {
    val capabilities = getNetworkCapabilities(network)
    return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
}

operator fun Level.compareTo(otherLevel:Level):Int {
    return this.intValue() - otherLevel.intValue()
}