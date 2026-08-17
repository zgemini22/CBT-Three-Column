package com.threecolumn.cbt

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.threecolumn.cbt.data.PrivacyPreferences
import com.threecolumn.cbt.ui.CbtNavHost
import com.threecolumn.cbt.ui.privacy.AppLockScreen
import com.threecolumn.cbt.ui.theme.ThreeColumnCbtTheme

class MainActivity : AppCompatActivity() {

    private lateinit var privacyPreferences: PrivacyPreferences

    // Compose state, not just a local var, so a toggle flipped in the About screen is
    // immediately reflected here (lock screen, FLAG_SECURE) without recreating the Activity.
    private var appLockEnabledState by mutableStateOf(false)
    private var unlockedState by mutableStateOf(true)

    // Set on onStop, cleared on onStart. Re-locking on every brief onStop (e.g. the system share
    // sheet launched by this app's own Share button) would be annoying, so only re-lock if the
    // app was actually backgrounded for a while.
    private var backgroundedAtMillis: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val application = application as CbtApplication
        privacyPreferences = PrivacyPreferences(this)
        appLockEnabledState = privacyPreferences.appLockEnabled
        unlockedState = !appLockEnabledState
        applySecureFlag(appLockEnabledState)

        setContent {
            ThreeColumnCbtTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (appLockEnabledState && !unlockedState) {
                        AppLockScreen(onUnlockRequested = ::requestUnlock)
                    } else {
                        CbtNavHost(
                            application = application,
                            appLockEnabled = appLockEnabledState,
                            biometricAvailable = isBiometricAvailable(),
                            onToggleAppLock = ::setAppLockEnabled
                        )
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (appLockEnabledState) {
            backgroundedAtMillis = System.currentTimeMillis()
        }
    }

    override fun onStart() {
        super.onStart()
        if (appLockEnabledState && backgroundedAtMillis != 0L) {
            val backgroundedFor = System.currentTimeMillis() - backgroundedAtMillis
            if (backgroundedFor >= LOCK_GRACE_PERIOD_MS) {
                unlockedState = false
            }
            backgroundedAtMillis = 0L
        }
    }

    private fun isBiometricAvailable(): Boolean {
        val allowed = BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        return BiometricManager.from(this).canAuthenticate(allowed) == BiometricManager.BIOMETRIC_SUCCESS
    }

    private fun setAppLockEnabled(enabled: Boolean) {
        appLockEnabledState = enabled
        unlockedState = !enabled
        privacyPreferences.appLockEnabled = enabled
        applySecureFlag(enabled)
    }

    /** Hides content from the recents/app-switcher thumbnail and blocks screenshots while the lock is on. */
    private fun applySecureFlag(secure: Boolean) {
        if (secure) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    private fun requestUnlock() {
        val allowed = BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        if (BiometricManager.from(this).canAuthenticate(allowed) != BiometricManager.BIOMETRIC_SUCCESS) {
            // No usable fingerprint/face/passcode enrolled: don't strand the user behind a lock they can't pass.
            unlockedState = true
            return
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.lock_screen_title))
            .setAllowedAuthenticators(allowed)
            .build()

        val prompt = BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    unlockedState = true
                }
            }
        )
        prompt.authenticate(promptInfo)
    }

    private companion object {
        const val LOCK_GRACE_PERIOD_MS = 10_000L
    }
}
