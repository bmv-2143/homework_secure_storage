package com.otus.securehomework.biometrics

import androidx.biometric.BiometricPrompt
import androidx.biometric.auth.AuthPromptErrorException
import androidx.biometric.auth.AuthPromptHost
import androidx.biometric.auth.Class2BiometricAuthPrompt
import androidx.biometric.auth.Class3BiometricAuthPrompt
import kotlinx.coroutines.suspendCancellableCoroutine

suspend fun Class3BiometricAuthPrompt.authenticate2(
    host: AuthPromptHost,
    crypto: BiometricPrompt.CryptoObject?,
    onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
    onError: (AuthPromptErrorException) -> Unit,
    onFailed: () -> Unit
): BiometricPrompt.AuthenticationResult {
    return suspendCancellableCoroutine { continuation ->
        val authPrompt = startAuthentication(
            host,
            crypto,
            Runnable::run,
            CoroutineAuthPromptCallback(continuation, onSuccess, onError, onFailed)
        )

        continuation.invokeOnCancellation {
            authPrompt.cancelAuthentication()
        }
    }
}

suspend fun Class2BiometricAuthPrompt.authenticate2(
    host: AuthPromptHost,
    onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
    onError: (AuthPromptErrorException) -> Unit,
    onFailed: () -> Unit
): BiometricPrompt.AuthenticationResult {
    return suspendCancellableCoroutine { continuation ->
        val authPrompt = startAuthentication(
            host,
            Runnable::run,
            CoroutineAuthPromptCallback(continuation, onSuccess, onError, onFailed)
        )

        continuation.invokeOnCancellation {
            authPrompt.cancelAuthentication()
        }
    }
}