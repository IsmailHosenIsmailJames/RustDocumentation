package com.example.rust_doc.presentation.home

import org.json.JSONObject

data class HomeScreenState(
  val currentDocPath: String = "file:///android_asset/index.html",
  val homePath: String = "file:///android_asset/index.html",
  val allDocsPath: List<String> = emptyList(),
  val allFavoritePath : List<String> = emptyList(),
  val isThisFavorite: Boolean = false,
  val historyOfVisitedPath: List<String> = emptyList(),
  val showMenu : Boolean = false,
  val searchQuery: String = "file:///android_asset/index.html",
  val searchResult: List<String> = emptyList(),
  val isSearchTyping : Boolean = false,
)