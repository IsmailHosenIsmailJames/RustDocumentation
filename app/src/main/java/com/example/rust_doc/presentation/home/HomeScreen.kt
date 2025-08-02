package com.example.rust_doc.presentation.home

import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddHome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddHome
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(homeViewModel: HomeScreenViewModel = koinViewModel()) {
  val homeState by homeViewModel.state.collectAsState()
  val focusManager = LocalFocusManager.current

  Scaffold(
    modifier = Modifier.clickable(
      interactionSource = remember { MutableInteractionSource() }, indication = null
    ) {
      focusManager.clearFocus()
    }, topBar = {
      HomeTopBar(homeViewModel, homeState)
    }) { paddingValues ->
    RustDocumentationScreen(
      homeViewModel,
      homeState,
      homeViewModel.webViewClient,
      Modifier.padding(paddingValues),
    )
  }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
  homeViewModel: HomeScreenViewModel,
  homeState: HomeScreenState,
) {
  val isFavorite = homeState.allFavoritePath.contains(homeState.currentDocPath)
  val context = LocalContext.current;
  TopAppBar(title = {
     OutlinedTextField(
      modifier = Modifier
        .padding(end = if (homeState.isSearchTyping) 15.dp else 0.dp)
        .fillMaxWidth()
        .onFocusChanged {
          homeViewModel.onAction(HomeScreenAction.IsSearchTyping(it.isFocused))
        },
      value = homeState.searchQuery.split("android_asset/").last(),
      leadingIcon = {
        Icon(
          Icons.Default.Search, contentDescription = "Search"
        )
      },
      onValueChange = {
        homeViewModel.onAction(HomeScreenAction.Search(it))
      },
      placeholder = {
        Text("Search")
      },
       textStyle = TextStyle(fontSize = 14.sp),
      shape = RoundedCornerShape(100),
      singleLine = true,
    )
  }, navigationIcon = {
    AnimatedVisibility(visible = !homeState.isSearchTyping) {
      IconButton(onClick = {}) {
        Icon(
          imageVector = Icons.Default.Menu, contentDescription = "Home"
        )
      }
    }
  }, actions = {
    AnimatedVisibility(visible = !homeState.isSearchTyping) {
      Row {
        IconButton(
          onClick = {
            homeViewModel.onAction(HomeScreenAction.GoBack)
          }) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        IconButton(
          onClick = {
            homeViewModel.onAction(HomeScreenAction.GoForward)
          }) {
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Forward")
        }
        IconButton(onClick = {
          homeViewModel.onAction(HomeScreenAction.ShowMenu(true))
        }) {
          Icon(
            imageVector = Icons.Filled.MoreVert, contentDescription = "More options"
          )
        }
      }
    }

    DropdownMenu(
      expanded = homeState.showMenu, onDismissRequest = {
        homeViewModel.onAction(HomeScreenAction.ShowMenu(false))
      }) {
      DropdownMenuItem(leadingIcon = {
        Icon(Icons.Filled.Home, contentDescription = "Home Icon")
      }, text = {
        Text("Go Home")
      }, onClick = {
        homeViewModel.onAction(HomeScreenAction.GoHome)
        homeViewModel.onAction(HomeScreenAction.ShowMenu(false))
      })
      DropdownMenuItem(leadingIcon = {
        Icon(Icons.Filled.AddHome, contentDescription = "Home Icon Outline")
      }, text = {
        Text("Set as Home")
      }, onClick = {
        homeViewModel.onAction(HomeScreenAction.SetAsHome(homeState.currentDocPath))
        homeViewModel.onAction(HomeScreenAction.ShowMenu(false))
      })
      DropdownMenuItem(leadingIcon = {
        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Favorites")
      }, text = {
        Text("All Favorites")
      }, onClick = {
        // TODO: Implement All Favorites action
        homeViewModel.onAction(HomeScreenAction.ShowMenu(false))
      })
      DropdownMenuItem(leadingIcon = {
        Icon(
          Icons.Filled.Favorite,
          contentDescription = "Favorites",
          tint = if (isFavorite) Color.Red else Color.Gray
        )
      }, text = {
        Text(if (isFavorite) "Remove from Favorites" else "Save to Favorites")
      }, onClick = {
        homeViewModel.onAction(
          if (isFavorite) HomeScreenAction.RemoveFavorite(homeState.currentDocPath)
          else HomeScreenAction.AddFavorite(
            homeState.currentDocPath
          )
        )
        Toast.makeText(
          context,
          if (isFavorite) "Removed from Favorites" else "Successfully Added to Favorites",
          Toast.LENGTH_SHORT
        ).show();
        homeViewModel.onAction(HomeScreenAction.ShowMenu(false))
      })

      DropdownMenuItem(leadingIcon = {
        Icon(
          Icons.Default.History,
          contentDescription = "History",
        )
      }, text = {
        Text("History")
      }, onClick = {
        homeViewModel.onAction(HomeScreenAction.ShowMenu(false))
      })

      DropdownMenuItem(leadingIcon = {
        Icon(
          Icons.Default.Delete,
          contentDescription = "Reset",
        )
      }, text = {
        Text("Reset App")
      }, onClick = {
        homeViewModel.onAction(HomeScreenAction.ResetApp)
        homeViewModel.onAction(HomeScreenAction.ShowMenu(false))
      })
    }
  })
}


@Composable
fun RustDocumentationScreen(
  homeViewModel: HomeScreenViewModel,
  homeState: HomeScreenState,
  webClient: WebViewClient,
  modifier: Modifier

) {
  // The path to your main documentation file within the assets folder
  val docPath = homeState.currentDocPath

  AndroidView(
    modifier = modifier.fillMaxWidth(), factory = { context ->
      WebView(context).apply {
        webViewClient = webClient
        settings.javaScriptEnabled = true
        settings.allowFileAccess = true
        settings.domStorageEnabled = true
        loadUrl(docPath)
        homeViewModel.onAction(HomeScreenAction.WebViewInstance(this))
      }
    })
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
  HomeScreen()
}
