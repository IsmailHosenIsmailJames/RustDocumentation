package com.example.rust_doc.utils

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipInputStream

class AppUtils {
  suspend fun extractZip(
    zipPath: String,
    destinationPath: String,
  ): String = withContext(Dispatchers.IO) {
    val destDir = File(destinationPath)
    if (!destDir.exists()) {
      if (!destDir.mkdirs()) {
        throw IOException("Failed to create destination directory: ${destDir.absolutePath}")
      }
    } else if (!destDir.isDirectory) {
      throw IOException("Destination path exists and is not a directory: $destinationPath")
    }

    val zipFile = File(zipPath)
    if (!zipFile.exists()) {
      throw FileNotFoundException("ZIP file not found: $zipPath")
    }


    var foundEntries = false
    FileInputStream(zipFile).use { fis ->
      ZipInputStream(fis).use { zis ->
        var zipEntry = zis.nextEntry
        val buffer = ByteArray(8192) // 8KB buffer

        while (zipEntry != null) {
          foundEntries = true
          val newFile = File(destDir, zipEntry.name)

          // Zip Slip vulnerability prevention
          val destDirPath = destDir.canonicalPath
          if (!newFile.canonicalPath.startsWith(destDirPath + File.separator)) {
            throw SecurityException("Zip entry is trying to escape destination directory: ${zipEntry.name}")
          }

          if (zipEntry.isDirectory) {
            if (!newFile.mkdirs() && !newFile.isDirectory) {
              throw IOException("Failed to create directory ${newFile.absolutePath}")
            }
          } else {
            val parent = newFile.parentFile
            if (parent != null) {
              if (!parent.mkdirs() && !parent.isDirectory) {
                throw IOException("Failed to create parent directory ${parent.absolutePath}")
              }
            }
            FileOutputStream(newFile).use { fos ->
              BufferedOutputStream(fos).use { bos ->
                var len: Int
                while (zis.read(buffer).also { len = it } > 0) {
                  bos.write(buffer, 0, len)
                }
              }
            }
          }
          zis.closeEntry()
          zipEntry = zis.nextEntry
        }
      }
    }

    // If no entries were found in the zip, it's effectively empty or invalid.
    if (!foundEntries) {
      val emptyZipIndexPath = File(destDir, "book/index.html").absolutePath
      if (File(emptyZipIndexPath).exists()) {
        return@withContext emptyZipIndexPath
      }
      throw FileNotFoundException("book/index.html not found in empty or invalid ZIP at $destinationPath/book/index.html")
    }


    val expectedIndexPath = File(destDir, "book/index.html").absolutePath
    println("SetupApp: Checking for index.html at: $expectedIndexPath")
    if (File(expectedIndexPath).exists()) {
      println("SetupApp: book/index.html found at $expectedIndexPath")
      return@withContext expectedIndexPath
    } else {
      // Fallback: Check for index.html at the root of destinationPath
      val rootIndexPath = File(destDir, "index.html").absolutePath
      println("SetupApp: book/index.html not found. Checking for root index.html at: $rootIndexPath")
      if (File(rootIndexPath).exists()){
        println("SetupApp: root index.html found at $rootIndexPath")
        return@withContext rootIndexPath
      }
      throw FileNotFoundException("Neither book/index.html nor index.html found at $destinationPath after extraction. Checked: $expectedIndexPath and $rootIndexPath")
    }
  }

  fun deleteFolder(language: String, application: Application){
    val folderPath = application.dataDir.path + "/$language"
    val folder = File(folderPath)
    if (folder.exists() && folder.isDirectory) {
      folder.deleteRecursively()
    }
  }
}