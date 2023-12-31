package com.gustavo.cocheckercompaniomkotlin.model.domain

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun FirebaseAuth.signInWithEmailAndPasswordSuspend(
    email: String,
    password: String
): AuthResult = suspendCoroutine { continuation ->
    signInWithEmailAndPassword(email, password)
        .addOnSuccessListener { authResult ->
            continuation.resume(authResult)
        }
        .addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
}

suspend fun FirebaseAuth.creteUserWithEmailAndPasswordSuspend(
    email: String,
    password: String
): AuthResult = suspendCoroutine { continuation ->
    createUserWithEmailAndPassword(
        email,
        password
    ).addOnSuccessListener { authResult ->
        continuation.resume(authResult)
    }
        .addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
}

suspend fun FirebaseAuth.sendPasswordResetEmailSuspend(email: String) =
    suspendCoroutine { continuation ->
        sendPasswordResetEmail(
            email
        ).addOnSuccessListener { authResult ->
            continuation.resume(authResult)
        }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }