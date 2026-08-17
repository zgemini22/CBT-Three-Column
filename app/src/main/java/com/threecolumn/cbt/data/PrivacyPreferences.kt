package com.threecolumn.cbt.data

import android.content.Context

/** Whether the app requires biometric/device-credential unlock before showing content. */
class PrivacyPreferences(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var appLockEnabled: Boolean
        get() = prefs.getBoolean(KEY_APP_LOCK_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_APP_LOCK_ENABLED, value).apply()

    companion object {
        private const val PREFS_NAME = "privacy_prefs"
        private const val KEY_APP_LOCK_ENABLED = "app_lock_enabled"
    }
}
