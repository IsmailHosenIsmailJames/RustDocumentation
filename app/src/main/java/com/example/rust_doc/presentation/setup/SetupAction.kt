package com.example.rust_doc.presentation.setup

sealed class SetupAction {
  data class SelectBook(val bookURL: String, val language: String) : SetupAction()
  data class DownloadZip(val bookURL: String) : SetupAction()
}