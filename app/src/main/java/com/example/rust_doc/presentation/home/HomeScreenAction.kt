package com.example.rust_doc.presentation.home

import android.webkit.WebView

sealed class HomeScreenAction {
  data class ChangeCurrentDoc(val path: String? = null) : HomeScreenAction()
  data class AddFavorite(val path: String) : HomeScreenAction()
  data class RemoveFavorite(val path: String) : HomeScreenAction()
  data class Search(val query: String) : HomeScreenAction()
  data class ShowMenu(val show: Boolean) : HomeScreenAction()
  data class IsSearchTyping(val isTyping: Boolean) : HomeScreenAction()
  object GoForward : HomeScreenAction()
  object GoBack : HomeScreenAction()
  object GoHome : HomeScreenAction()
  data class SetAsHome(val path: String) : HomeScreenAction()
  data class WebViewInstance(val webView: WebView, val initPath: String) : HomeScreenAction()
  class ResetApp(navigateSetupPage: () -> Unit) : HomeScreenAction()
}
