package com.otus.securehomework.presentation.auth

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt
import androidx.biometric.auth.AuthPromptErrorException
import androidx.biometric.auth.AuthPromptHost
import androidx.biometric.auth.Class2BiometricAuthPrompt
import androidx.biometric.auth.Class3BiometricAuthPrompt
import androidx.lifecycle.lifecycleScope
import com.otus.myapplication.biometrics.BiometricCipher
import com.otus.securehomework.R
import com.otus.securehomework.biometrics.authenticate2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        showBiometricPrompt()
    }

    private fun showToast(message: String) {
        Toast.makeText(
            applicationContext, message,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showBiometricPrompt() {
        if (isBiometryStrongAvailable()) {
            lifecycleScope.launch {
                authenticateStrongBiometry()
            }
        } else if (isBiometryWeakAvailable()) {
            lifecycleScope.launch {
                authenticateWeakBiometry()
            }
        } else {
            showToast("Biometry not supported")
        }
    }

    private fun isBiometryStrongAvailable(): Boolean {
        return BiometricManager.from(this).canAuthenticate(BIOMETRIC_STRONG) == BIOMETRIC_SUCCESS
    }

    private fun isBiometryWeakAvailable(): Boolean {
        return BiometricManager.from(this).canAuthenticate(BIOMETRIC_WEAK) == BIOMETRIC_SUCCESS
    }

    private suspend fun authenticateStrongBiometry() {
        val authPrompt = makeStrongBiometricPrompt()

        val biometricCipher = BiometricCipher(applicationContext)
        val cryptoObject: BiometricPrompt.CryptoObject = biometricCipher.getEncryptor()

        try {
            authPrompt.authenticate2(
                AuthPromptHost(this),
                crypto = cryptoObject,
                onSuccess = {
                    Toast.makeText(this, "Authentication SUCCEEDED!", Toast.LENGTH_LONG).show()
                },
                onError = { error ->
                    showToast("Authentication ERROR: $error")
                },
                onFailed = {
                    Toast.makeText(this, "Authentication FAILED", Toast.LENGTH_LONG).show()
                }
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

    private suspend fun authenticateWeakBiometry() {
        val authPrompt = makeWeakBiometricPrompt()

        try {
            authPrompt.authenticate2(
                AuthPromptHost(this),
                onSuccess = {
                    showToast("Authentication SUCCEEDED!")
                },
                onError = { error ->
                    showToast("Authentication ERROR: $error")
                },
                onFailed = {
                    showToast("Authentication FAILED")
                }
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