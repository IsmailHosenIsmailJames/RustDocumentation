package com.example.rust_doc.data.local

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

object PreferencesKeys {
    val FAVORITE_PATHS = stringSetPreferencesKey("favorite_paths")
    val HOME_PATH = stringPreferencesKey("home_path")
}
