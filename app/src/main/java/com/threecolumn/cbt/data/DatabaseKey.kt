package com.threecolumn.cbt.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.security.SecureRandom

/**
 * The SQLCipher passphrase for the local database: 32 random bytes generated once on first run,
 * kept in EncryptedSharedPreferences so it sits behind an AES key held in the Android Keystore
 * rather than in the app's own files.
 *
 * It is stored as hex because the plaintext-to-encrypted migration has to interpolate it into an
 * ATTACH DATABASE statement, where bind parameters aren't available; hex has nothing to quote.
 */
internal object DatabaseKey {

    fun getOrCreate(context: Context): String {
        val prefs = EncryptedSharedPreferences.create(
            PREFS_NAME,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            context.applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        prefs.getString(KEY_PASSPHRASE, null)?.let { return it }

        val passphrase = ByteArray(PASSPHRASE_BYTES)
            .also { SecureRandom().nextBytes(it) }
            .joinToString("") { "%02x".format(it) }
        // commit(), not apply(): if the process died before this reached disk, the database it
        // encrypts would be unreadable.
        prefs.edit().putString(KEY_PASSPHRASE, passphrase).commit()
        return passphrase
    }

    private const val PREFS_NAME = "database_key"
    private const val KEY_PASSPHRASE = "passphrase"
    private const val PASSPHRASE_BYTES = 32
}
