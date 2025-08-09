package com.rust_book.example.data.remote

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class SetupApp {
  suspend fun downloadFile(
    application: Application,
    fileURL: String,
  ): String = withContext(Dispatchers.IO) {
    val url = URL(fileURL)
    val connection = url.openConnection()
    connection.connect()

    val fileName = fileURL.substring(fileURL.lastIndexOf('/') + 1)
    val outputFile = File(application.cacheDir, fileName)

    connection.getInputStream().use { input ->
      FileOutputStream(outputFile).use { output ->
        val buffer = ByteArray(4 * 1024)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
          output.write(buffer, 0, bytesRead)
        }
      }
    }
    return@withContext outputFile.absolutePath
  }

}
