package com.example.rust_doc.presentation.setup

sealed class SetupAction {
  data class SelectBook(val bookURL: String) : SetupAction()
  data class DownloadZip(val bookURL: String) : SetupAction()
  data class ExtractZip(val zipPath: String) : SetupAction()
  data class NavigateToHome(val initPath: String) : SetupAction()
}