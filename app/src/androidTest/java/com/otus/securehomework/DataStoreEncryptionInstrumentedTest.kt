package com.otus.securehomework

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.otus.securehomework.data.encryption.DataStoreEncryption
import com.otus.securehomework.data.encryption.KeyManagerLowerThanM
import com.otus.securehomework.data.encryption.KeyManagerMAndHigher
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DataStoreEncryptionInstrumentedTest {

    private lateinit var dataStoreEncryption: DataStoreEncryption
    private lateinit var keyManagerMAndHigher: KeyManagerMAndHigher
    private lateinit var keyManagerLowerThanM: KeyManagerLowerThanM

    private val testData = "Test Data"

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        keyManagerMAndHigher = KeyManagerMAndHigher()
        keyManagerLowerThanM = KeyManagerLowerThanM(context)
    }

    @Test
    fun testEncryptAndDecryptDataMAndHigher() {
        dataStoreEncryption = DataStoreEncryption(keyManagerMAndHigher)

        val encryptedData = dataStoreEncryption.encryptData(testData)
        val decryptedData = dataStoreEncryption.decryptData(encryptedData)

        assertEquals(testData, decryptedData)
    }

    @Test
    fun testEncryptAndDecryptDataLowerThanM() {
        dataStoreEncryption = DataStoreEncryption(keyManagerLowerThanM)

        val encryptedData = dataStoreEncryption.encryptData(testData)
        val decryptedData = dataStoreEncryption.decryptData(encryptedData)

        assertEquals(testData, decryptedData)
    }
}