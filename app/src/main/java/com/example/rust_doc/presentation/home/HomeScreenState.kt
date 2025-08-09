package com.example.rust_doc.presentation.home

data class HomeScreenState(
  val currentDocPath: String? = null,
  val homePath: String? = null,
  val language: String?=null,
  val allDocsPath: List<String> = emptyList(),
  val allFavoritePath : List<String> = emptyList(),
  val isThisFavorite: Boolean = false,
  val historyOfVisitedPath: List<String> = emptyList(),
  val showMenu : Boolean = false,
  val searchQuery: String? = null,
  val searchResult: List<String> = emptyList(),
  val isSearchTyping : Boolean = false,
)