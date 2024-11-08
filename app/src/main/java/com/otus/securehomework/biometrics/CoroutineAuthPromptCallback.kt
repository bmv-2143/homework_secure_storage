package com.otus.securehomework.biometrics

import androidx.biometric.BiometricPrompt
import androidx.biometric.auth.AuthPromptCallback
import androidx.biometric.auth.AuthPromptErrorException
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CancellableContinuation
import kotlin.coroutines.resumeWithException

internal class CoroutineAuthPromptCallback(
    private val continuation: CancellableContinuation<BiometricPrompt.AuthenticationResult>,
    private val onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
    private val onError: (AuthPromptErrorException) -> Unit,
    private val onFailed: () -> Unit
) : AuthPromptCallback() {

    override fun onAuthenticationError(
        activity: FragmentActivity?,
        errorCode: Int,
        errString: CharSequence
    ) {
        val exception = AuthPromptErrorException(errorCode, errString)
        onError(exception)
        continuation.resumeWithException(exception)
    }

    override fun onAuthenticationSucceeded(
        activity: FragmentActivity?,
        result: BiometricPrompt.AuthenticationResult
    ) {
        onSuccess(result)
        continuation.resumeWith(Result.success(result))
    }

    override fun onAuthenticationFailed(activity: FragmentActivity?) {
        onFailed()
    }
}