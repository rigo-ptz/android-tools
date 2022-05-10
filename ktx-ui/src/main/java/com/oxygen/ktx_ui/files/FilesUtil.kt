package com.oxygen.ktx_ui.files

import android.content.Context
import java.io.File

object FilesUtil {

    fun deleteFile(context: Context, directoryName: String, fileName: String) {
        val file = File(context.filesDir.path + directoryName + fileName)
        file.delete()
    }

    fun deleteDirectory(context: Context, directoryName: String) {
        val directory = File(context.filesDir.path + directoryName)
        if (directory.exists()) {
            directory.delete()
        }
    }

    fun clearDirectory(context: Context, directoryName: String, ignoreFiles: List<String>) {
        val directory = File(context.filesDir.path + directoryName)
        val files = directory.listFiles()
        if (files.isNullOrEmpty()) return
        for (file in files) {
            if (!ignoreFiles.any { it == file.name }) {
                file.delete()
            }
        }
    }

    fun createDirectoryIfDoesNotExist(context: Context, directoryName: String) {
        val file = File(context.filesDir.path + directoryName)
        if (!file.exists()) {
            File(context.filesDir.path + directoryName).mkdir()
        }
    }

    fun createLocalFile(context: Context, directoryName: String, fileName: String): File {
        createDirectoryIfDoesNotExist(context, directoryName)
        val file = File(context.filesDir.path + directoryName + fileName.replace(" ", ""))
        file.createNewFile()
        return file
    }

}
