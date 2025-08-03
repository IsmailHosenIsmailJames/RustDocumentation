package com.example.rust_doc.utils.models

data class FilesAndFolderInfo(
  var folderName: String,
  var parent: String,
  var files: MutableList<String> = mutableListOf<String>(),
  var children: MutableList<FilesAndFolderInfo> = mutableListOf<FilesAndFolderInfo>(),

  )
