package com.example.rust_doc.presentation.setup

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rust_doc.data.local.PreferencesKeys
import com.example.rust_doc.presentation.setup.models.Books
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SetupViewModel(
  private val dataStore: DataStore<Preferences>,
) : ViewModel() {

  private val _state = MutableStateFlow(SetupModelSate())
  val state = _state.asStateFlow()

  init {
    viewModelScope.launch {
      // get Home path
      dataStore.data.collect { preferences ->
        {
          val initPath = preferences[PreferencesKeys.HOME_PATH]
          val isDownloaded = preferences[PreferencesKeys.IS_DOWNLOADED]
          val bookLanguage = preferences[PreferencesKeys.BOOK_LANGUAGE]
          _state.value = _state.value.copy(
            initPath = initPath, isDownloaded = isDownloaded == "true", bookLanguage = bookLanguage
          )
        }
      }
    }
  }

  fun onAction(setupAction: SetupAction){
    when(setupAction){
      is SetupAction.SelectBook -> {
        _state.value = _state.value.copy(
          selectedBookUrl = setupAction.bookURL
        )
      }

      is SetupAction.DownloadZip -> {
        TODO()
      }
      is SetupAction.ExtractZip -> {
        TODO()
      }
      is SetupAction.NavigateToHome -> {
        TODO()
      }
    }
  }
}


data class SetupModelSate(
  val initPath: String? = null,
  val isDownloaded: Boolean? = null,
  val bookLanguage: String? = null,
  val selectedBookUrl : String = "https://ismailhosenismailjames.github.io/rust_book_multi_language/book/English.zip",
  val listOfBooks: List<Books> = listOf<Books>(
    Books(
      link = "https://ismailhosenismailjames.github.io/rust_book_multi_language/book/Danske.zip",
      language = "Danske",
      isComplete = true
    ),
    Books(
      link = "https://ismailhosenismailjames.github.io/rust_book_multi_language/book/Deutsch.zip",
      language = "Deutsch",
      isComplete = true
    ),
    Books(
      link = "https://ismailhosenismailjames.github.io/rust_book_multi_language/book/English.zip",
      language = "English",
      isComplete = true
    ),
    Books(
      link = "https://ismailhosenismailjames.github.io/rust_book_multi_language/book/%E0%A6%AC%E0%A6%BE%E0%A6%82%E0%A6%B2%E0%A6%BE.zip",
      language = "বাংলা",
      isComplete = true
    ),
    Books(
      link = "https://ismailhosenismailjames.github.io/rust_book_multi_language/book/Espa%C3%B1ol.zip",
      language = "Español",
      isComplete = true
    ),
    Books(
      link = "https://ismailhosenismailjames.github.io/rust_book_multi_language/book/Esperanto.zip",
      language = "Esperanto",
      isComplete = true
    ),
    Books(
      link = "https://ismailhosenismailjames.github.io/rust_book_multi_language/book/Farsi.zip",
      language = "Farsi",
      isComplete = false
    ),
    Books(
      link = "https://ismailhosenismailjames.github.io/rust_book_multi_language/book/Fran%C3%A7ais.zip",
      language = "Français",
      isComplete = true
    ),
    Books(
      link = "https://ismailhosenismailjames.github.io/rust_book_multi_language/book/Polski.zip",
      language = "Polski",
      isComplete = true
    ),
    Books(
      link = "https://ismailhosenismailjames.github.io/rust_book_multi_language/book/Portugu%C3%AAs.zip",
      language = "Português",
      isComplete = true
    ),
    Books(
      link = "https://ismailhosenismailjames.github.io/rust_book_multi_language/book/Svenska.zip",
      language = "Svenska",
      isComplete = false
    ),
    Books(
      link = "https://ismailhosenismailjames.github.io/rust_book_multi_language/book/%D0%A0%D1%83%D1%81%D1%81%D0%BA%D0%B8%D0%B9.zip",
      language = "Русский",
      isComplete = true
    ),
    Books(
      link = "https://ismailhosenismailjames.github.io/rust_book_multi_language/book/%D0%A3%D0%BA%D1%80%D0%B0%D1%97%D0%BD%D1%81%D1%8C%D0%BA%D0%B0.zip",
      language = "Українська",
      isComplete = true
    ),
    Books(
      link = "https://ismailhosenismailjames.github.io/rust_book_multi_language/book/%E6%AD%A3%E9%AB%94%E4%B8%AD%E6%96%87.zip",
      language = "正體中文",
      isComplete = true
    ),
    Books(
      link = "https://ismailhosenismailjames.github.io/rust_book_multi_language/book/%E7%AE%80%E4%BD%93%E4%B8%AD%E6%96%87.zip",
      language = "简体中文",
      isComplete = true
    ),
    Books(
      link = "https://ismailhosenismailjames.github.io/rust_book_multi_language/book/%ED%95%9C%EA%B5%AD%EC%96%B4.zip",
      language = "한국어",
      isComplete = true
    ),
    Books(
      link = "https://ismailhosenismailjames.github.io/rust_book_multi_language/book/%E6%97%A5%E6%9C%AC%E8%AA%9E.zip",
      language = "日本語",
      isComplete = true
    ),
  ),
)