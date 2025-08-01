package com.example.rust_doc.presentation.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeScreenViewModel: ViewModel() {
  private val _state = MutableStateFlow(HomeScreenState())
  val state = _state.asStateFlow()

  fun onAction(action: HomeScreenAction){
    when (action) {
      is HomeScreenAction.NavigateToDoc -> {
        // TODO: Implement navigation to doc
      }
      is HomeScreenAction.AddFavorite -> {
        // TODO: Implement toggle favorite
      }
      is HomeScreenAction.RemoveFavorite -> {
        // TODO: Implement toggle favorite
      }
      is HomeScreenAction.Search -> {
        // TODO: Implement search
      }
      is HomeScreenAction.ShowMenu -> {
        _state.value = _state.value.copy(showMenu = action.show)
      }
      is HomeScreenAction.IsSearchTyping -> {
        _state.value = _state.value.copy(isSearchTyping = action.isTyping)
      }
    }
  }
}
