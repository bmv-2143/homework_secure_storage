package com.otus.securehomework.presentation.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt
import androidx.biometric.auth.AuthPromptHost
import androidx.biometric.auth.Class3BiometricAuthPrompt
import androidx.core.content.ContextCompat
import com.otus.myapplication.biometrics.BiometricCipher
import com.otus.securehomework.R
import com.otus.securehomework.biometrics.authenticate2
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executor

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        showBiometricPrompt()
    }

    private fun showBiometricPrompt() {
        if (isBiometryStrongAvailable()) {
            setUpBiometry()
            val biometricCipher = BiometricCipher(applicationContext)
            val encryptor: BiometricPrompt.CryptoObject = biometricCipher.getEncryptor()
            biometricPrompt.authenticate(promptInfo, encryptor)

        } else if (isBiometryWeakAvailable()) {
            setUpBiometry()
            biometricPrompt.authenticate(promptInfo)
        } else {
            Toast.makeText(this, "Biometry not supported", Toast.LENGTH_LONG).show()
        }
    }

    private fun isBiometryStrongAvailable(): Boolean {
        return BiometricManager.from(this).canAuthenticate(BIOMETRIC_STRONG) == BIOMETRIC_SUCCESS
    }

    private fun isBiometryWeakAvailable(): Boolean {
        return BiometricManager.from(this).canAuthenticate(BIOMETRIC_WEAK) == BIOMETRIC_SUCCESS
    }

    private fun setUpBiometry() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = makeBiometricPrompt()
        promptInfo = makeBiometricInfo()
    }

    private fun makeBiometricPrompt() = BiometricPrompt(this, executor,

        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(
                errorCode: Int,
                errString: CharSequence
            ) {
                super.onAuthenticationError(errorCode, errString)
                showToast("Authentication error: $errString")
            }

            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult
            ) {
                super.onAuthenticationSucceeded(result)
                showToast("Authentication succeeded!")
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                showToast("Authentication failed")
            }
        })

    private fun makeBiometricInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle(
                if (isBiometryStrongAvailable()) {
                    "Strong biometry"
                } else {
                    "Weak biometry"
                }
            )
            .setSubtitle("Use you finger")
            .setNegativeButtonText("Close")
            .setConfirmationRequired(true)
            .build()
    }

    private fun showToast(message: String) {
        Toast.makeText(
            applicationContext, message,
            Toast.LENGTH_LONG
        ).show()
    }
}