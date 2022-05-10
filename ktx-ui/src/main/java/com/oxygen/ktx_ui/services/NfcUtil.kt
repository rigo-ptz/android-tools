package com.oxygen.ktx_ui.services

import android.content.Context
import android.nfc.NfcManager

object NfcUtil {

    fun isNFCAvailable(context: Context): Boolean {
        val manager = context.getSystemService(Context.NFC_SERVICE) as? NfcManager
        val adapter = manager?.defaultAdapter
        return adapter != null
    }

}
