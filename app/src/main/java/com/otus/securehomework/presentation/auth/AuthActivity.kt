package com.otus.securehomework.presentation.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.otus.securehomework.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    @Inject
    lateinit var biometricAuth: BiometricAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        showBiometricPrompt()
    }

    private fun showBiometricPrompt() {
        if (biometricAuth.isBiometryStrongAvailable()) {
            lifecycleScope.launch {
                biometricAuth.authenticateStrongBiometry(
                    onSuccess = {
                        showToast(getString(R.string.biometry_authentication_succeeded))
                    },
                    onError = { error ->
                        showToast(getString(R.string.biometry_authentication_error, error))
                    },
                    onFailed = {
                        showToast(getString(R.string.biometry_authentication_failed))
                    }
                )
            }
        } else if (biometricAuth.isBiometryWeakAvailable()) {
            lifecycleScope.launch {
                biometricAuth.authenticateWeakBiometry(
                    onSuccess = {
                        showToast(getString(R.string.biometry_authentication_succeeded))
                    },
                    onError = { error ->
                        showToast(getString(R.string.biometry_authentication_error, error))
                    },
                    onFailed = {
                        showToast(getString(R.string.biometry_authentication_failed))
                    }
                )
            }
        } else {
            showToast(getString(R.string.biometry_not_supported))
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(
            applicationContext, message,
            Toast.LENGTH_LONG
        ).show()
    }
}