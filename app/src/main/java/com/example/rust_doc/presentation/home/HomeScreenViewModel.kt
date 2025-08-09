package com.example.rust_doc.presentation.home

import android.app.Application
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rust_doc.data.local.PreferencesKeys
import com.example.rust_doc.utils.AppUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File

class HomeScreenViewModel(
  private val dataStore: DataStore<Preferences>,
  private val application: Application,
) : ViewModel() {
  private val _state = MutableStateFlow(HomeScreenState())
  val state = _state.asStateFlow()

  val webViewClient = object : WebViewClient() {
    override fun onPageFinished(view: WebView?, url: String?) {
      super.onPageFinished(view, url)
      if (url != null) justChangeCurrentDoc(url)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
      super.shouldOverrideUrlLoading(view, request)
      val requestedUrl = request?.url?.toString()
      println(requestedUrl)
      if (requestedUrl != null && requestedUrl != _state.value.currentDocPath) {
        println("Action = $requestedUrl")
        onAction(action = HomeScreenAction.ChangeCurrentDoc(requestedUrl))
      }
      return false
    }
  }

  private var _webView: WebView? = null

  init {
    viewModelScope.launch {
      val preferences = dataStore.data.first()
      val favoritePaths = preferences[PreferencesKeys.FAVORITE_PATHS] ?: emptySet()
      val homePath = preferences[PreferencesKeys.HOME_PATH]
      val history = preferences[PreferencesKeys.HISTORY_OF_VISITED_PATH] ?: emptySet()
      val language = preferences[PreferencesKeys.BOOK_LANGUAGE] ?: "English"
      _state.value = _state.value.copy(
        allFavoritePath = favoritePaths.toList(),
        homePath = homePath,
        currentDocPath = homePath, // Start at home path
        searchQuery = homePath?.split("book/")?.last(),
        historyOfVisitedPath = history.toList(),
        language = language
      )
      // Update isThisFavorite based on initial currentDocPath
      _state.value = _state.value.copy(isThisFavorite = favoritePaths.contains(homePath))

      // Observe changes to favorites, home path and history
      dataStore.data
        .map { prefs ->
          Triple(
            prefs[PreferencesKeys.FAVORITE_PATHS] ?: emptySet(),
            prefs[PreferencesKeys.HOME_PATH] ?: "file:///android_asset/index.html",
            prefs[PreferencesKeys.HISTORY_OF_VISITED_PATH] ?: emptySet()
          )
        }
        .collect { (favorites, home, history) ->
          _state.value = _state.value.copy(
            allFavoritePath = favorites.toList(),
            homePath = home,
            isThisFavorite = favorites.contains(_state.value.currentDocPath), // Keep isThisFavorite updated
            historyOfVisitedPath = history.toList()
          )
        }

    }
  }

  fun justChangeCurrentDoc(path: String) {
    if (File(path).exists()) {
      _state.value = _state.value.copy(
        currentDocPath = path,
        homePath = path,
        searchQuery = if (path.startsWith("http")) path else path.split("book/").last(),
        isThisFavorite = _state.value.allFavoritePath.contains(path)
      )
    } else {
      return
    }
  }

  fun onAction(action: HomeScreenAction) {
    when (action) {
      is HomeScreenAction.ChangeCurrentDoc -> {
        if(!(action.path!=null&&File(action.path).exists())) return
        action.path.let {
          val history = _state.value.historyOfVisitedPath.toMutableList().take(20)
            .toMutableList() // Limit history size
          if (history.firstOrNull() != it) { // Avoid duplicate entries at the top
            history.add(0, it)
          }
          viewModelScope.launch {
            dataStore.edit { dataBase ->
              dataBase[PreferencesKeys.HISTORY_OF_VISITED_PATH] = history.toSet()
            }
          }
          println("Action Path ${action.path}")
          _webView?.loadUrl(action.path)
          _state.value = _state.value.copy(
            currentDocPath = it,
            searchQuery = if (it.startsWith("http")) it else it.split("book/").last(),
            historyOfVisitedPath = history,
            isThisFavorite = _state.value.allFavoritePath.contains(it)
          )
        }
      }

      is HomeScreenAction.WebViewInstance -> {
        _webView = action.webView
        // Load initial URL after webview is ready
        _webView?.loadUrl(action.initPath)
      }

      is HomeScreenAction.AddFavorite -> {
        viewModelScope.launch {
          dataStore.edit {
            val currentFavorites = it[PreferencesKeys.FAVORITE_PATHS] ?: emptySet()
            it[PreferencesKeys.FAVORITE_PATHS] = currentFavorites + action.path
          }
          // UI update will be handled by the collector in init
        }
      }

      is HomeScreenAction.RemoveFavorite -> {
        viewModelScope.launch {
          dataStore.edit {
            val currentFavorites = it[PreferencesKeys.FAVORITE_PATHS] ?: emptySet()
            it[PreferencesKeys.FAVORITE_PATHS] = currentFavorites - action.path
          }
          // UI update will be handled by the collector in init
        }
      }

      is HomeScreenAction.Search -> {
        _state.value = _state.value.copy(searchQuery = action.query)
        // Simple local search (case-insensitive)
        // For a more robust search, consider indexing or a dedicated search library.
        // allDocsPath needs to be populated for search to work.
        // For now, it will search within already visited paths or favorites as a placeholder.
        val combinedSearchablePaths =
          (_state.value.historyOfVisitedPath + _state.value.allFavoritePath).distinct()
        _state.value = _state.value.copy(
          searchResult = if (action.query.isNotBlank()) {
            combinedSearchablePaths.filter { it.contains(action.query, ignoreCase = true) }
          } else {
            emptyList()
          }
        )
      }

      is HomeScreenAction.ShowMenu -> {
        _state.value = _state.value.copy(showMenu = action.show)
      }

      is HomeScreenAction.IsSearchTyping -> {
        _state.value = _state.value.copy(isSearchTyping = action.isTyping)
      }

      is HomeScreenAction.GoForward -> {
        if (_webView?.canGoForward() == true) {
          _webView?.goForward()
        }
      }

      is HomeScreenAction.GoBack -> {
        if (_webView?.canGoBack() == true) {
          _webView?.goBack()
        }
      }

      is HomeScreenAction.GoHome -> {
        viewModelScope.launch {
          val homePath = dataStore.data.first()[PreferencesKeys.HOME_PATH]
          onAction(HomeScreenAction.ChangeCurrentDoc(homePath)) // This will also update webview
          if (homePath != null) {
            _webView?.loadUrl(homePath)
          }
        }
      }

      is HomeScreenAction.SetAsHome -> {
        viewModelScope.launch {
          dataStore.edit {
            it[PreferencesKeys.HOME_PATH] = action.path
          }
          // UI update for homePath will be handled by the collector in init
          // Optionally, show a confirmation to the user (e.g., via a Toast or Snackbar)
        }
      }

      is HomeScreenAction.ResetApp -> {
        viewModelScope.launch {
          dataStore.edit {
            it.clear()
          }
          // also delete all files in the data folder. For Example : file:///data/user/0/com.example.rust_doc/English/
          if (_state.value.language != null) {
            AppUtils().deleteFolder(_state.value.language!!, application = application)
          }

        }
      }
    }
  }
}
