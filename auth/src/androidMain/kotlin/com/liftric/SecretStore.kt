package com.liftric

import android.content.Context

actual class SecretStore(context: Context) {
    actual val vault = KVault(context)
}