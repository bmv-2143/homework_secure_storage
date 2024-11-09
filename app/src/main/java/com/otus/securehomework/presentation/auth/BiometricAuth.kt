package com.otus.securehomework.presentation.auth

import android.os.Build
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
import com.otus.securehomework.R
import com.otus.securehomework.biometrics.BiometricCipher
import com.otus.securehomework.biometrics.authenticateBiometry
import javax.inject.Inject

class BiometricAuth @Inject constructor(private val authActivity: FragmentActivity) {

    private val tag = BiometricAuth::class.simpleName

    internal suspend fun authenticateBiometry(
        onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
        onError: (AuthPromptErrorException) -> Unit,
        onFailed: () -> Unit,
        onBiometryNotAvailable: () -> Unit
    ) {
        if (isBiometryStrongAvailable()) {
            authenticateStrongBiometry(onSuccess, onError, onFailed)
        } else if (isBiometryWeakAvailable()) {
            authenticateWeakBiometry(onSuccess, onError, onFailed)
        } else {
            onBiometryNotAvailable()
        }
    }

    private fun isBiometryStrongAvailable(): Boolean {
        return BiometricManager
            .from(authActivity)
            .canAuthenticate(BIOMETRIC_STRONG) == BIOMETRIC_SUCCESS
    }

    private fun isBiometryWeakAvailable(): Boolean {
        return BiometricManager
            .from(authActivity)
            .canAuthenticate(BIOMETRIC_WEAK) == BIOMETRIC_SUCCESS
    }

    private suspend fun authenticateStrongBiometry(
        onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
        onError: (AuthPromptErrorException) -> Unit,
        onFailed: () -> Unit
    ) {
        val authPrompt = makeStrongBiometricPrompt()
        val biometricCipher = BiometricCipher(authActivity)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val cryptoObject: BiometricPrompt.CryptoObject = biometricCipher.getEncryptor()
            try {
                authPrompt.authenticateBiometry(
                    AuthPromptHost(authActivity),
                    crypto = cryptoObject,
                    onSuccess = onSuccess,
                    onError = { e ->
                        onError(e)
                        Log.e(tag, "Error: $e")
                    },
                    onFailed = onFailed
                )
            } catch (e: AuthPromptErrorException) {
                logBiometryError(e)
            }
        } else {
            Log.e(tag, "Strong biometry is not available")
        }
    }

    private fun makeStrongBiometricPrompt() = Class3BiometricAuthPrompt.Builder(
        authActivity.getString(R.string.biometry_title_strong_biometry),
        authActivity.getString(R.string.biometry_button_dismiss)
    ).apply {
        setSubtitle(authActivity.getString(R.string.biometry_subtitle_input_your_biometry))
        setDescription(authActivity.getString(R.string.biometry_description_we_need_your_finger))
        setConfirmationRequired(true)
    }.build()

    private suspend fun authenticateWeakBiometry(
        onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
        onError: (AuthPromptErrorException) -> Unit,
        onFailed: () -> Unit
    ) {
        val authPrompt = makeWeakBiometricPrompt()

        try {
            authPrompt.authenticateBiometry(
                AuthPromptHost(authActivity),
                onSuccess = onSuccess,
                onError = onError,
                onFailed = onFailed
            )
        } catch (e: AuthPromptErrorException) {
            logBiometryError(e)
        }
    }

    private fun logBiometryError(e: AuthPromptErrorException) {
        Log.e(tag, "Authentication ERROR: ${e.message}")
    }

    private fun makeWeakBiometricPrompt() =
        Class2BiometricAuthPrompt.Builder(
            authActivity.getString(R.string.biometry_title_weak_biometry),
            authActivity.getString(R.string.biometry_button_dismiss)
        ).apply {
            setSubtitle(authActivity.getString(R.string.biometry_subtitle_input_your_biometry))
            setDescription(authActivity.getString(R.string.biometry_description_we_need_your_finger))
            setConfirmationRequired(true)
        }.build()
}

