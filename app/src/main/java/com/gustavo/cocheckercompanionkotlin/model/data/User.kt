package com.gustavo.cocheckercompanionkotlin.model.data

import java.io.Serializable

data class RegisterUser(val email: String?)

data class UserWifi(var ssid: String, var password: String?) : Serializable