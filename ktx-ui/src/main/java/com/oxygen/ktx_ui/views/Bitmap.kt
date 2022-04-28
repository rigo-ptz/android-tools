package com.oxygen.ktx_ui.views

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Base64
import android.util.Log
import com.oxygen.ktx_ui.LOG_TAG
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * @author Iamushev Igor
 * @since  24.4.2022
 */
object BitmapUtils {

    /**
     * https://developer.android.com/topic/performance/graphics/load-bitmap
     */
    fun decodeSampledBitmap(
        pathName: String,
        reqWidth: Int,
        reqHeight: Int,
        forceRotate: Boolean
    ): Bitmap? {
        // get raw dimensions
        val options = BitmapFactory.Options()

        // First decode with inJustDecodeBounds=true to check dimensions
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(pathName, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false

        // Image may require rotation depend of device manufacture as camera are default set to landscape mode
        val sampled = BitmapFactory.decodeFile(pathName, options)
        return if (forceRotate)
            rotateImageIfRequired(sampled, pathName)
        else
            sampled
    }

    /**
     * https://developer.android.com/topic/performance/graphics/load-bitmap
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }

        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    fun rotateImageIfRequired(bitmap: Bitmap, filePath: String): Bitmap? {
        val ei = ExifInterface(filePath)
        return when (ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
            else -> bitmap
        }
    }

    private fun rotateImage(bitmap: Bitmap, degree: Int): Bitmap? {
        val matrix = Matrix().apply { postRotate(degree.toFloat()) }
        val rotatedImg = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )
        bitmap.recycle()
        return rotatedImg
    }

    fun saveBitmapToFile(file: File, bitmap: Bitmap, recycle: Boolean) {
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos)
        val bytes = bos.toByteArray()

        val fos = FileOutputStream(file)

        try {
            fos.write(bytes)
            fos.flush()
            fos.close()
        } catch (ex: Exception) {
            Log.e(LOG_TAG, ex.localizedMessage)
        } finally {
            if (recycle) bitmap.recycle()
        }
    }

}

suspend fun Bitmap.toBase64(
    compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    quality: Int = 100
): String {
    val baos = ByteArrayOutputStream()
    compress(compressFormat, quality, baos)
    val byteArray = baos.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}
