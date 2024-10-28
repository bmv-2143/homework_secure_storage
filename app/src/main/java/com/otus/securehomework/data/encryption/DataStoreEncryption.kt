package com.otus.securehomework.data.encryption

import android.util.Base64
import android.util.Log
import java.security.Key
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec


class DataStoreEncryption(
    private val keyManager: KeyManager
) {

    fun encryptData(data: String?): String {
        val key = keyManager.getAesSecretKey()
        val encrypted = encryptAes(data!!, key)
        Log.e("TAG", "ENCRYPTED: $encrypted")
        return encrypted
    }

    fun decryptData(data: String): String {
        val key = keyManager.getAesSecretKey()
        val decrypted = decryptAes(data, key)
        Log.e("TAG", "DECRYPTED: $decrypted")
        return decrypted
    }

    private fun encryptAes(plainText: String, key: Key): String {
        val cipher = Cipher.getInstance(AES_TRANSFORMATION) // ???
        cipher.init(Cipher.ENCRYPT_MODE, key, getInitializationVector())
        val encodedBytes = cipher.doFinal(plainText.toByteArray())
        return Base64.encodeToString(encodedBytes, Base64.NO_WRAP)
    }

    private fun getInitializationVector(): AlgorithmParameterSpec {
        val iv = ByteArray(GCM_IV_LENGTH)
        /*
However, there is an issue with this line. The startIndex is set to GCM_IV_LENGTH, which is 12, but the
FIXED_IV array is only 16 bytes long. This will cause an ArrayIndexOut0fBoundsException because it tries
to copy elements starting from index 12 to the end of the array, which exceeds the length of the iv array.

To fix this, you should adjust the startIndex and the length of the copy operation to ensure it fits within the
bounds of both arrays.
         */
        FIXED_IV.copyInto(destination = iv, destinationOffset = 0, startIndex = GCM_IV_LENGTH) // original - is it correct?
//        FIXED_IV.copyInto(destination = iv, destinationOffset = 0, startIndex = 0, endIndex = GCM_IV_LENGTH) // fixed by Copilot - should I use this?
        return GCMParameterSpec(128, iv) // Galois/Counter Mode (GCM) of operation for the AES encryption algorithm
    }

    private fun decryptAes(encrypted: String, key: Key): String {
        val cipher = Cipher.getInstance(AES_TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, getInitializationVector())
        val decodedBytes = Base64.decode(encrypted, Base64.NO_WRAP)
        val decoded = cipher.doFinal(decodedBytes)
        return String(decoded, Charsets.UTF_8)
    }

    companion object {
        private const val AES_TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_IV_LENGTH = 12
        private const val IV = "3134003223491201"
        private val FIXED_IV = IV.toByteArray()
    }

}