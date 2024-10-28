package com.otus.securehomework.data.encryption

import javax.crypto.SecretKey

interface KeyManager {
    fun removeKeys(keyAlias: String)
    fun getAesSecretKey(): SecretKey
}