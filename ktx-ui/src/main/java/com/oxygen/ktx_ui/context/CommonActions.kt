package com.oxygen.ktx_ui.context

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @author Iamushev Igor
 * @since  24.4.2022
 */
fun Context.launchAppOrToast(intent: Intent?, error: String) {
    if (intent != null && canLaunchIntent(intent))
        this.startActivity(intent)
    else
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
}

@SuppressLint("QueryPermissionsNeeded")
fun Context.canLaunchIntent(intent: Intent): Boolean {
    val res = packageManager.queryIntentActivities(intent, 0)
    return res.size != 0
}

fun Context.shareTextToOtherApp(
    shareBody: String?,
    sendWithTitle: String,
    appsNotFoundText: String
) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareBody ?: "")
    }

    launchAppOrToast(Intent.createChooser(intent, sendWithTitle), appsNotFoundText)
}

fun Context.openAppByPackage(packageName: String, appNotFoundText: String) {
    val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
    launchAppOrToast(launchIntent, appNotFoundText)
}

fun Context.openAppInGooglePlayOrInBrowser(pack: String) {
    val inMarketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$pack"))

    if (canLaunchIntent(inMarketIntent)) {
        startActivity(inMarketIntent)
    } else {
        Intent(Intent.ACTION_VIEW)
            .apply {
                data = Uri.parse("http://play.google.com/store/apps/details?id=$pack")
            }
            .let {
                startActivity(it)
            }
    }
}

fun Context.emailTo(email: String?, mailAgentsNotFoundText: String) {
    val emailIntent = Intent(
        Intent.ACTION_SENDTO,
        Uri.fromParts("mailto", email, null)
    )
    launchAppOrToast(Intent.createChooser(emailIntent, null), mailAgentsNotFoundText)
}

fun Context.callTo(phone: String) {
    val intent = Intent(
        Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)
    )
    startActivity(intent)
}

fun Context.openUrl(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}

/**
 * @param authorityString - "com.foobar.***.storage.provider"
 */
@SuppressLint("ObsoleteSdkInt")
fun <I, O> AppCompatActivity.dispatchToTakePhoto(
    authorityString: String,
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>
) {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

    if (!canLaunchIntent(intent)) return

    var photoFile: File? = null
    try {
        photoFile = createImageFile(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    photoFile?.let {
        val photoURI = FileProvider.getUriForFile(this, authorityString, it)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            else -> {
                val clip = ClipData.newUri(contentResolver, "A photo", photoURI)
                intent.clipData = clip
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
        }

        registerForActivityResult(contract, callback)
    }
}

/**
 * @param authorityString - "ru.trinitydigital.***.storage.provider"
 */
@SuppressLint("ObsoleteSdkInt")
fun <I, O> Fragment.dispatchToTakePhoto(
    authorityString: String,
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>
) {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    if (!requireActivity().canLaunchIntent(intent)) return

    var photoFile: File? = null
    try {
        photoFile = createImageFile(context!!)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    photoFile?.let {
        val photoURI = FileProvider.getUriForFile(context!!, authorityString, it)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            else -> {
                val clip = ClipData.newUri(context!!.contentResolver, "A photo", photoURI)
                intent.clipData = clip
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
        }

        registerForActivityResult(contract, callback)
    }
}

private fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(imageFileName, ".jpg", storageDir)
}


fun Context.color(resId: Int) = ContextCompat.getColor(this, resId)

fun Context.drawable(resId: Int) = ContextCompat.getDrawable(this, resId)

fun Context.toast(message: String, isShort: Boolean = true) =
    Toast.makeText(
        this,
        message,
        if (isShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    ).show()
