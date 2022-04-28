package com.oxygen.ktx_ui.context

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * @author Iamushev Igor
 * @since  24.4.2022
 */
private fun Context.getDataDirPath(): String? =
    try {
        packageManager.getPackageInfo(packageName, 0).applicationInfo.dataDir
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        null
    }

fun Context.saveBitmapToInternalStorage(
    fileName: String,
    directoryName: String,
    bitmapImage: Bitmap
) {
    val directory = getDir(directoryName, Context.MODE_PRIVATE)
    // Create imageDir
    val filePath = File(directory, fileName)

    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(filePath)
        // Use the compress method on the Bitmap object to write image to the OutputStream
        bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            fos?.close()
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }
    }
}
