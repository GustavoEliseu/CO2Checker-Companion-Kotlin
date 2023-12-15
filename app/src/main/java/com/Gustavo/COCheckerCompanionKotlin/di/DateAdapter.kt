package com.Gustavo.COCheckerCompanionKotlin.di

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

var customDateAdapter: Any = object : Any() {
    val dateFormat: DateFormat
    val dateFormat2: DateFormat
    val dateFormat3: DateFormat
    val dateFormat4: DateFormat

    init {
        dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        dateFormat2 = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        dateFormat3 = SimpleDateFormat("dd/MM/yyyy")
        dateFormat4 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    }

    @ToJson
    @Synchronized
    fun dateToJson(d: Date): String {
        return dateFormat.format(d)
    }

    @FromJson
    @Synchronized
    @Throws(ParseException::class)
    fun dateToJson(s: String): Date? {
        return try {
            dateFormat.parse(s)
        } catch (parseException: ParseException) {
            try {
                dateFormat2.parse(s)
            } catch (parseException: ParseException) {
                try {
                    dateFormat3.parse(s)
                } catch (parseException: ParseException) {
                    try {
                        dateFormat4.parse(s)
                    } catch (parseException: ParseException) {
                        val dateTimestamp = s.toLongOrNull()
                        if (dateTimestamp == null) return null else Date(dateTimestamp)
                    }
                }
            }
        }
    }
}