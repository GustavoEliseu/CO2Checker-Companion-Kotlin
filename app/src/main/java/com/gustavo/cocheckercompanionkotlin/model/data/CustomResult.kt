package com.gustavo.cocheckercompanionkotlin.model.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError


sealed class BaseResult;

open class CustomResult<out T>:BaseResult() {
    data class Success<out T>(val data: T) : CustomResult<T>()
    data class Error(val exception: Exception) : CustomResult<Nothing>()
}
sealed class FirebaseResult:BaseResult(){
    data class Changed(val snapshot: DataSnapshot): FirebaseResult()
    data class Cancelled(val error: DatabaseError): FirebaseResult()
}