package com.example.rust_doc.presentation.home

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
// import androidx.compose.material.icons.filled.ArrowBack // Duplicate import
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
// import androidx.compose.material.icons.outlined.Home // Duplicate import, use filled version or specific outlined if needed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
// import androidx.compose.material3.SearchBar // Not used in current TopAppBar, but might be for future
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
// import androidx.compose.ui.ExperimentalComposeUiApi // Not explicitly used
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
  val homeViewModel = HomeScreenViewModel()
  val homeState by homeViewModel.state.collectAsState()
  val focusManager = LocalFocusManager.current

  val topAppBarState = rememberTopAppBarState()
  val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)

  Scaffold(
    modifier = Modifier
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      ) {
        focusManager.clearFocus()
      }
      .nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
      HomeTopBar(homeViewModel, homeState, scrollBehavior)
    }
  ) { paddingValues ->
    RustDocumentationScreen(homeViewModel, homeState, Modifier.padding(paddingValues))
  }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
  homeViewModel: HomeScreenViewModel,
  homeState: HomeScreenState,
  scrollBehavior: TopAppBarScrollBehavior // Added scrollBehavior parameter
) {
  TopAppBar(
    scrollBehavior = scrollBehavior, // Passed to TopAppBar
    title = {
      OutlinedTextField(
        modifier = Modifier
          .minimumInteractiveComponentSize()
          .padding(end = 15.dp)
          .fillMaxWidth()
          .onFocusChanged {
            homeViewModel.onAction(HomeScreenAction.IsSearchTyping(it.isFocused))
          },
        value = homeState.searchQuery,
        leadingIcon = {
          Icon(
            Icons.Default.Search,
            contentDescription = "Search"
          )
        },
        onValueChange = {
          homeViewModel.onAction(HomeScreenAction.Search(it))
        },
        placeholder = {
          Text("Search")
        },
        shape = RoundedCornerShape(100),
        singleLine = true,
      )
    },
    navigationIcon = {
      AnimatedVisibility(visible = !homeState.isSearchTyping) {
        IconButton(onClick = {}) {
          Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Home"
          )
        }
      }
    },
    actions = {
      AnimatedVisibility(visible = !homeState.isSearchTyping) {
        Row {
          IconButton(
            onClick = {
              // TODO: Implement back action
            }
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
          IconButton(
            onClick = {
              // TODO: Implement forward action
            }
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Forward")
          }
          IconButton(onClick = {
            homeViewModel.onAction(HomeScreenAction.ShowMenu(true))
          }) {
            Icon(
              imageVector = Icons.Filled.MoreVert,
              contentDescription = "More options"
            )
          }
        }
      }

      DropdownMenu(
        expanded = homeState.showMenu,
        onDismissRequest = {
          homeViewModel.onAction(HomeScreenAction.ShowMenu(false))
        }
      ) {
        DropdownMenuItem(
          leadingIcon = {
            Icon(Icons.Filled.Home, contentDescription = "Home Icon")
          },
          text = {
            Text("Go Home")
          },
          onClick = {
            // TODO: Implement Go Home action
            homeViewModel.onAction(HomeScreenAction.ShowMenu(false))
          }
        )
        DropdownMenuItem(
          leadingIcon = {
            Icon(Icons.Outlined.Home, contentDescription = "Home Icon Outline")
          },
          text = {
            Text("Set as Home")
          },
          onClick = {
            // TODO: Implement Set as Home action
            homeViewModel.onAction(HomeScreenAction.ShowMenu(false))
          }
        )
        DropdownMenuItem(
          leadingIcon = {
            Icon(Icons.Filled.Favorite, contentDescription = "Favorites")
          },
          text = {
            Text("All Favorites")
          },
          onClick = {
            // TODO: Implement All Favorites action
            homeViewModel.onAction(HomeScreenAction.ShowMenu(false))
          }
        )
        DropdownMenuItem(
          leadingIcon = {
            Icon(
              Icons.Outlined.FavoriteBorder,
              contentDescription = "Favorites"
            )
          },
          text = {
            Text("Save to Favorites")
          },
          onClick = {
            // TODO: Implement Save to Favorites action
            homeViewModel.onAction(HomeScreenAction.ShowMenu(false))
          }
        )
      }
    }
  )
}


@Composable
fun RustDocumentationScreen(
  homeViewModel: HomeScreenViewModel,
  homeState: HomeScreenState,
  modifier: Modifier
) {
  // The path to your main documentation file within the assets folder
  val docPath = "file:///android_asset/html/index.html"

  AndroidView(modifier = modifier.fillMaxWidth(), factory = { context -> // Added fillMaxWidth to enable scrolling if content is wide
    WebView(context).apply {
      webViewClient = WebViewClient() // Ensures links open within the WebView
      settings.javaScriptEnabled = true // Enable JavaScript if your docs use it
      settings.allowFileAccess =
        true    // Important for loading local files and their resources (CSS, JS, images)
      settings.domStorageEnabled = true  // May be needed for some documentation features
      loadUrl(docPath)
    }
  })
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
  HomeScreen()
}
