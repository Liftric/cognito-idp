package com.liftric

expect class SettingsStore {
    fun set(key: String, value: String)
    fun set(key: String, value: Double)

    fun string(forKey: String): String?
    fun double(forKey: String): Double?

    fun deleteObject(forKey: String)
}