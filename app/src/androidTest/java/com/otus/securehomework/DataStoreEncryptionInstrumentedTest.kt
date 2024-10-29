package com.otus.securehomework

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.otus.securehomework.data.encryption.DataStoreEncryption
import com.otus.securehomework.data.encryption.KeyManagerMAndHigher
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DataStoreEncryptionInstrumentedTest {

    private lateinit var dataStoreEncryption: DataStoreEncryption
    private lateinit var keyManager: KeyManagerMAndHigher

    @Before
    fun setUp() {
        keyManager = KeyManagerMAndHigher()
        dataStoreEncryption = DataStoreEncryption(keyManager)
    }

    @Test
    fun testEncryptAndDecryptData() {
        val data = "Test Data"

        val encryptedData = dataStoreEncryption.encryptData(data)
        val decryptedData = dataStoreEncryption.decryptData(encryptedData)

        assertEquals(data, decryptedData)
    }
}