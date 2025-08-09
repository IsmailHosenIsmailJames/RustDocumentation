package com.example.rust_doc.data.local

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

object PreferencesKeys {
    val FAVORITE_PATHS = stringSetPreferencesKey("favorite_paths")
    val HOME_PATH = stringPreferencesKey("home_path")
    val HISTORY_OF_VISITED_PATH = stringSetPreferencesKey("history_of_visited_path")
    val IS_DOWNLOADED = stringPreferencesKey("is_downloaded")
    val BOOK_LANGUAGE = stringPreferencesKey("book_language")
}
