package com.oxygen.ktx

import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object HashUtil {

    fun md5(input: String): String? =
        if (input.isEmpty()) {
            input
        } else try {
            val md = MessageDigest.getInstance("MD5")
            val md5Data = BigInteger(1, md.digest(input.toByteArray()))
            String.format("%032x", md5Data)
        } catch (e: NoSuchAlgorithmException) {
            println("Failed to parse input to MD5")
            null
        }

}
