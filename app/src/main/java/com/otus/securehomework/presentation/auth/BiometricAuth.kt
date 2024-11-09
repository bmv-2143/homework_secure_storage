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
import com.otus.securehomework.R
import com.otus.securehomework.biometrics.BiometricCipher
import com.otus.securehomework.biometrics.authenticate2
import javax.inject.Inject

class BiometricAuth @Inject constructor(private val authActivity: FragmentActivity) {

    private val tag = BiometricAuth::class.simpleName

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
            logBiometryError(e)
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

