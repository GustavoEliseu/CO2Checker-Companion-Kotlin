package com.gustavo.cocheckercompaniomkotlin.utils

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class JsonHelper {
    companion object {
        fun isJSONValid(test: String): Boolean {
            try {
                JSONObject(test)
            } catch (ex: JSONException) {
                try {
                    JSONArray(test)
                } catch (ex1: JSONException) {
                    return false
                }
            }
            return true
        }

        @JvmStatic
        inline fun <reified T> getObjectFromJson(json: String?): T? {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            val adapter: JsonAdapter<T> = moshi.adapter(T::class.java)
            if (json.isNullOrEmpty()) return null
            return try {
                adapter.fromJson(json)
            } catch (jsonException: Exception) {
                return null
            }
        }

        @JvmStatic
        inline fun <reified T> getJsonFromObject(objectToJson: T?): String {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            val adapter: JsonAdapter<T> = moshi.adapter(T::class.java)
            return adapter.toJson(objectToJson)
        }
    }
}