package com.liftric

import platform.Foundation.NSUserDefaults

actual class SettingsStore {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    actual fun set(key: String, value: String) {
        userDefaults.setObject(value, key)
    }

    actual fun set(key: String, value: Double) {
        userDefaults.setObject(value, key)
    }

    actual fun string(forKey: String): String? {
        return userDefaults.stringForKey(forKey)
    }

    actual fun double(forKey: String): Double? {
        return userDefaults.doubleForKey(forKey)
    }

    actual fun deleteObject(forKey: String) {
        userDefaults.removeObjectForKey(forKey)
    }
}