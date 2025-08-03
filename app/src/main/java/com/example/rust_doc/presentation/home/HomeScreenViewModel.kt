package com.example.rust_doc.presentation.home

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rust_doc.data.local.PreferencesKeys
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeScreenViewModel(private val dataStore: DataStore<Preferences>) : ViewModel() {
  private val _state = MutableStateFlow(HomeScreenState())
  val state = _state.asStateFlow()

  val webViewClient = object : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
      super.shouldOverrideUrlLoading(view, request)
      if (request?.url?.path != _state.value.currentDocPath) {
        request?.url?.path?.let { onAction(action = HomeScreenAction.ChangeCurrentDoc(it)) }
      }
      return false // Prevent multiple loads for the same URL
    }
  }

  private var _webView: WebView? = null

  init {
    viewModelScope.launch {
      val preferences = dataStore.data.first()
      val favoritePaths = preferences[PreferencesKeys.FAVORITE_PATHS] ?: emptySet()
      val homePath = preferences[PreferencesKeys.HOME_PATH] ?: "file:///android_asset/index.html"
      val history = preferences[PreferencesKeys.HISTORY_OF_VISITED_PATH] ?: emptySet()
      _state.value = _state.value.copy(
        allFavoritePath = favoritePaths.toList(),
        homePath = homePath,
        currentDocPath = homePath, // Start at home path
        searchQuery = homePath,
        historyOfVisitedPath = history.toList()
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

  fun onAction(action: HomeScreenAction) {
    when (action) {
      is HomeScreenAction.ChangeCurrentDoc -> {
        action.path?.let {
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
          _webView?.loadUrl(action.path)
          _state.value = _state.value.copy(
            currentDocPath = it,
            searchQuery = it,
            historyOfVisitedPath = history,
            isThisFavorite = _state.value.allFavoritePath.contains(it)
          )
        }
      }

      is HomeScreenAction.WebViewInstance -> {
        _webView = action.webView
        // Load initial URL after webview is ready
        _webView?.loadUrl("file://${_state.value.currentDocPath}")
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
          val homePath = dataStore.data.first()[PreferencesKeys.HOME_PATH] ?: "file:///android_asset/index.html"
          onAction(HomeScreenAction.ChangeCurrentDoc(homePath)) // This will also update webview
          _webView?.loadUrl(homePath)
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
            it[PreferencesKeys.FAVORITE_PATHS] = emptySet()
            it[PreferencesKeys.HOME_PATH] = "file:///android_asset/index.html"
            it[PreferencesKeys.HISTORY_OF_VISITED_PATH] = emptySet()
          }
        }
      }
    }
  }
}
