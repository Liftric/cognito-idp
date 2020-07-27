package com.liftric

import android.content.Context
import android.content.Context.MODE_PRIVATE

actual class SettingsStore(context: Context) {
    private val sharedPrefs = context.getSharedPreferences("tokens", MODE_PRIVATE)

    actual fun set(key: String, value: String) {
        sharedPrefs
            .edit()
            .putString(key, value)
            .apply()
    }

    actual fun set(key: String, value: Double) {
        sharedPrefs
            .edit()
            .putLong(key, value.toRawBits())
            .apply()
    }

    actual fun string(forKey: String): String? {
        if (existsObject(forKey)) {
            return sharedPrefs.getString(forKey, null)
        }

        return null
    }

    actual fun double(forKey: String): Double? {
        if (existsObject(forKey)) {
            return Double.fromBits(sharedPrefs.getLong(forKey, Long.MIN_VALUE))
        }

        return null
    }

    actual fun deleteObject(forKey: String) {
        sharedPrefs.edit().remove(forKey).apply()
    }

    private fun existsObject(forKey: String): Boolean {
        return sharedPrefs.contains(forKey)
    }
}