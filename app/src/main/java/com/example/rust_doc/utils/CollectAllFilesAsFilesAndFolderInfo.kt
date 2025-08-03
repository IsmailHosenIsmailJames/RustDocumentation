package com.example.rust_doc.utils

import com.example.rust_doc.utils.models.FilesAndFolderInfo

class CollectAllFilesAsFilesAndFolderInfo(val collectFolder: String = "file:///android_asset/") {
  suspend fun collectAllFiles(): List<FilesAndFolderInfo>{
    // TODO: Collect all files as nested map
  }

  suspend fun collectAllHTMLs():List<FilesAndFolderInfo>{
    // TODO: Collect all HTMLs by filtering collectAllFiles
  }

  suspend fun collectAllIndexHTMLFiles():List<FilesAndFolderInfo>{
    // TODO: Collect all HTMLs by filtering collectAllHTMLs
  }
}