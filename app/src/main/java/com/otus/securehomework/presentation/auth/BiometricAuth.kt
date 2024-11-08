package com.otus.securehomework.presentation.auth

import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt
import androidx.biometric.auth.AuthPromptErrorException
import androidx.biometric.auth.AuthPromptHost
import androidx.biometric.auth.Class2BiometricAuthPrompt
import androidx.biometric.auth.Class3BiometricAuthPrompt
import androidx.fragment.app.FragmentActivity
import com.otus.myapplication.biometrics.BiometricCipher
import com.otus.securehomework.biometrics.authenticate2
import javax.inject.Inject

class BiometricAuth @Inject constructor(private val authActivity: FragmentActivity) {

    internal fun isBiometryStrongAvailable(): Boolean {
        return BiometricManager
            .from(authActivity)
            .canAuthenticate(BIOMETRIC_STRONG) == BIOMETRIC_SUCCESS
    }

    internal fun isBiometryWeakAvailable(): Boolean {
        return BiometricManager
            .from(authActivity)
            .canAuthenticate(BIOMETRIC_WEAK) == BIOMETRIC_SUCCESS
    }

    internal suspend fun authenticateStrongBiometry(
        onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
        onError: (AuthPromptErrorException) -> Unit,
        onFailed: () -> Unit
    ) {
        val authPrompt = makeStrongBiometricPrompt()

        val biometricCipher = BiometricCipher(authActivity)
        val cryptoObject: BiometricPrompt.CryptoObject = biometricCipher.getEncryptor()

        try {
            authPrompt.authenticate2(
                AuthPromptHost(authActivity),
                crypto = cryptoObject,
                onSuccess = onSuccess,
                onError = onError,
                onFailed = onFailed
            )
        } catch (e: AuthPromptErrorException) {
            Log.e("Biometric", "!!! Authentication ERROR: ${e.message}")
        }
    }

    private fun makeStrongBiometricPrompt() = Class3BiometricAuthPrompt.Builder(
        "Strong biometry", "dismiss"
    ).apply {
        setSubtitle("Input your biometry")
        setDescription("We need your finger")
        setConfirmationRequired(true)
    }.build()

    internal suspend fun authenticateWeakBiometry(
        onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
        onError: (AuthPromptErrorException) -> Unit,
        onFailed: () -> Unit
    ) {
        val authPrompt = makeWeakBiometricPrompt()

        try {
            authPrompt.authenticate2(
                AuthPromptHost(authActivity),
                onSuccess = onSuccess,
                onError = onError,
                onFailed = onFailed
            )
        } catch (e: AuthPromptErrorException) {
            Log.e("Biometric", "!!! Authentication ERROR: ${e.message}")
        }
    }

    private fun makeWeakBiometricPrompt() =
        Class2BiometricAuthPrompt.Builder("Weak biometry", "dismiss").apply {
            setSubtitle("Input your biometry")
            setDescription("We need your finger")
            setConfirmationRequired(true)
        }.build()
}

