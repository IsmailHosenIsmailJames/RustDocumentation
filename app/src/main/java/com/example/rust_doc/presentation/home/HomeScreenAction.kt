package com.example.rust_doc.presentation.home

sealed class HomeScreenAction {
  data class NavigateToDoc(val path: String) : HomeScreenAction()
  data class AddFavorite(val path: String) : HomeScreenAction()
  data class RemoveFavorite(val path: String) : HomeScreenAction()
  data class Search(val query: String) : HomeScreenAction()
  data class ShowMenu(val show: Boolean) : HomeScreenAction()
  data class IsSearchTyping(val isTyping: Boolean) : HomeScreenAction()
}
