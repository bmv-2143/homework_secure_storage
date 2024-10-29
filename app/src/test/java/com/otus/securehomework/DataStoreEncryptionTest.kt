package com.otus.securehomework

import com.otus.securehomework.data.encryption.DataStoreEncryption
import com.otus.securehomework.data.encryption.KeyManager
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

@RunWith(RobolectricTestRunner::class)
class DataStoreEncryptionTest {

    private lateinit var dataStoreEncryption: DataStoreEncryption
    private lateinit var keyManager: KeyManager
    private lateinit var secretKey: SecretKey

    @Before
    fun setUp() {
        keyManager = Mockito.mock(KeyManager::class.java)

        generateRealSecretKey()

        Mockito.`when`(keyManager.getAesSecretKey()).thenReturn(secretKey)
        dataStoreEncryption = DataStoreEncryption(keyManager)
    }

    private fun generateRealSecretKey() {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        secretKey = keyGen.generateKey()
    }

    @Test
    fun testDecryptData() {
        val data = "Test Data"

        val encryptedData = dataStoreEncryption.encryptData(data)
        val decryptedData = dataStoreEncryption.decryptData(encryptedData)

        assertEquals(data, decryptedData)
    }
}