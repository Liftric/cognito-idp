package com.liftric

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

expect class SettingsStoreTest: AbstractSettingsStoreTest
abstract class AbstractSettingsStoreTest(private val store: SettingsStore) {
    @Test
    fun testSetGetString() {
        store.set("STRING", "1337")
        assertNotNull(store.string("STRING"))
        assertEquals(store.string("STRING"), "1337")
    }

    @Test
    fun testSetGetDouble() {
        store.set("DOUBLE", 1337.toDouble())
        assertNotNull(store.double("DOUBLE"))
        assertEquals(store.double("DOUBLE"), 1337.toDouble())
    }

    @Test
    fun testUpdateString() {
        store.set("STRING", "1337")
        store.set("STRING", "42")
        assertEquals(store.string("DOUBLE"), "42")
    }

    @Test
    fun testUpdateDouble() {
        store.set("DOUBLE", 1337.toDouble())
        store.set("DOUBLE", 42.toDouble())
        assertEquals(store.double("DOUBLE"), 42.toDouble())
    }

    @Test
    fun testDeleteObject() {
        store.set("STRING", "1337")
        store.set("STRING2", "1337")
        store.deleteObject("STRING2")
        assertNotNull(store.double("STRING"))
        assertNull(store.string("STRING2"))
    }
}