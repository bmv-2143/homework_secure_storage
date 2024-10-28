package com.otus.securehomework.data.encryption

import java.security.KeyStore


abstract class KeyManagerImpl : KeyManager {

    protected val keyStore: KeyStore by lazy {
        KeyStore.getInstance(KEY_PROVIDER).apply {
            load(null)
        }
    }

    override fun removeKeys(keyAlias: String) {
        keyStore.deleteEntry(keyAlias)
    }

    companion object {
        const val KEY_PROVIDER = "AndroidKeyStore"
        const val AES_ALGORITHM = "AES"
    }

}