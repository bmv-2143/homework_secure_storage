package com.otus.securehomework.data.encryption

import android.content.Context
import android.security.KeyPairGeneratorSpec
import com.otus.securehomework.data.encryption.KeyManagerImpl.Companion.KEY_PROVIDER
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.util.Calendar
import javax.security.auth.x500.X500Principal


class RSAKeysLowerThanM(
    private val applicationContext: Context,
    private val keyStore: KeyStore
) {

    internal fun getRsaPublicKey(): PublicKey {
        return keyStore.getCertificate(RSA_KEY_ALIAS)?.publicKey ?: generateRsaSecretKey().public
    }

    internal fun getRsaPrivateKey(): PrivateKey {
        return keyStore.getKey(RSA_KEY_ALIAS, null) as? PrivateKey ?: generateRsaSecretKey().private
    }

    private fun generateRsaSecretKey(): KeyPair {
        val start: Calendar = Calendar.getInstance()
        val end: Calendar = Calendar.getInstance()
        end.add(Calendar.YEAR, CERTIFICATE_VALIDITY_YEARS)

        val spec = KeyPairGeneratorSpec.Builder(applicationContext)
            .setAlias(RSA_KEY_ALIAS)
            .setSubject(X500Principal("CN=$RSA_KEY_ALIAS"))
            .setSerialNumber(BigInteger.TEN)
            .setStartDate(start.time)
            .setEndDate(end.time)
            .setKeySize(KEY_SIZE)
            .build()

        return KeyPairGenerator.getInstance(RSA_ALGORITHM, KEY_PROVIDER).run {
            initialize(spec)
            generateKeyPair()
        }
    }

    companion object {
        private const val RSA_KEY_ALIAS = "RSA_OTUS_DEMO"
        private const val RSA_ALGORITHM = "RSA"
        private const val CERTIFICATE_VALIDITY_YEARS = 30
        private const val KEY_SIZE = 2048
    }
}