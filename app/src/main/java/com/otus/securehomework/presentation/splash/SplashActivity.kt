package com.otus.securehomework.presentation.splash

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.otus.securehomework.R
import com.otus.securehomework.data.source.local.UserPreferences
import com.otus.securehomework.presentation.auth.AuthActivity
import com.otus.securehomework.presentation.auth.BiometricAuth
import com.otus.securehomework.presentation.home.HomeActivity
import com.otus.securehomework.presentation.startNewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity  : AppCompatActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var biometricAuth: BiometricAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        showBiometricPrompt()
    }

    private fun showBiometricPrompt() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                biometricAuth.authenticateBiometry(
                    onSuccess = {
                        showToast(getString(R.string.biometry_authentication_succeeded))
                        navigateBasedOnAuthData(userPreferences)
                    },
                    onError = { error ->
                        showToast(getString(R.string.biometry_authentication_error, error))
                        finish()
                    },
                    onFailed = {
                        showToast(getString(R.string.biometry_authentication_failed))
                    },
                    onBiometryNotAvailable = {
                        showToast(getString(R.string.biometry_not_supported))
                        navigateBasedOnAuthData(userPreferences)
                    }
                )
            }
        }
    }

    private fun navigateBasedOnAuthData(userPreferences: UserPreferences) {
        userPreferences.accessToken.asLiveData().observe(this) {
            val activity = if (it == null) {
                AuthActivity::class.java
            } else {
                HomeActivity::class.java
            }
            startNewActivity(activity)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(
            applicationContext, message,
            Toast.LENGTH_LONG
        ).show()
    }
}