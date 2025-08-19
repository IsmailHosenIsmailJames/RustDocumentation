package com.rust_book.example.presentation.home

import android.app.Activity
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddHome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.rust_book.example.SelectLanguageOfBookNav
import com.rust_book.example.ui.theme.GreenGrey40
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  nav: NavController,
  initPath: String,
  homeViewModel: HomeScreenViewModel = koinViewModel()
) {
  val homeState by homeViewModel.state.collectAsState()
  val focusManager = LocalFocusManager.current
  val isDarkTheme = isSystemInDarkTheme()
  val colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()

  val context = LocalContext.current
  // Intercept back presses
  BackHandler(enabled = true) {
    homeViewModel.onAction(
      HomeScreenAction.HandleSystemBack { webViewHandledBack ->
        if (!webViewHandledBack) {
          if (nav.previousBackStackEntry != null) {
            nav.popBackStack()
          } else {
            (context as? Activity)?.finish()
          }
        }
      })
  }



  Scaffold(
    modifier = Modifier.clickable(
      interactionSource = remember { MutableInteractionSource() }, indication = null
    ) {
      focusManager.clearFocus()
    }, topBar = {
      HomeTopBar(homeViewModel, homeState, navController = nav)
    }) { paddingValues ->
    Box(modifier = Modifier.padding(paddingValues)) {
      RustDocumentationScreen(
        homeViewModel,
        webClient = homeViewModel.webViewClient,
        initPath = initPath
      )
      AnimatedVisibility(visible = homeState.isSearchTyping) {
        Box(
          modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .background(
              color = colorScheme.surface,
              shape = RoundedCornerShape(10.dp)
            )
            .padding(10.dp),

          contentAlignment = Alignment.Center
        ) {
          if(homeState.searchResult.isEmpty())Text("No Result Found") else LazyColumn {
            items(homeState.searchResult.size) {
              Text(
                homeState.searchResult[it].split("book/").last(),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(3.dp)
                  .clickable {
                    homeViewModel.onAction(HomeScreenAction.ChangeCurrentDoc(homeState.searchResult[it]))
                    println(homeState.searchResult[it])
                    focusManager.clearFocus()
                  }
                  .border(
                    width = 1.dp,
                    color = GreenGrey40,
                    shape = RoundedCornerShape(10.dp)
                  )
                  .padding(10.dp)
              )
            }
          }
        }
      }

      // Show Favorites Popup
      if (homeState.showFavoritesPopup) {
        ListPopup(
          title = "All Favorites",
          items = homeState.allFavoritePath,
          onDismiss = { homeViewModel.onAction(HomeScreenAction.ShowFavoritesPopup(false)) },
          onItemClick = { path ->
            homeViewModel.onAction(HomeScreenAction.ChangeCurrentDoc(path))
            homeViewModel.onAction(HomeScreenAction.ShowFavoritesPopup(false))
          }
        )
      }

      // Show History Popup
      if (homeState.showHistoryPopup) {
        ListPopup(
          title = "History",
          items = homeState.historyOfVisitedPath,
          onDismiss = { homeViewModel.onAction(HomeScreenAction.ShowHistoryPopup(false)) },
          onItemClick = { path ->
            homeViewModel.onAction(HomeScreenAction.ChangeCurrentDoc(path))
            homeViewModel.onAction(HomeScreenAction.ShowHistoryPopup(false))
          }
        )
      }
    }
  }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
  homeViewModel: HomeScreenViewModel,
  homeState: HomeScreenState,
  navController: NavController
) {
  val isFavorite = homeState.allFavoritePath.contains(homeState.currentDocPath)
  val context = LocalContext.current
  TopAppBar(title = {
    OutlinedTextField(
      modifier = Modifier
        .padding(end = if (homeState.isSearchTyping) 15.dp else 0.dp)
        .fillMaxWidth()
        .onFocusChanged {
          if (it.isFocused) {
            homeViewModel.onAction(
              HomeScreenAction.Search(
                homeState.searchQuery?.split("book/")?.last() ?: "index.html"
              )
            )
          }
          homeViewModel.onAction(HomeScreenAction.IsSearchTyping(it.isFocused))
        },
      value = if (homeState.searchQuery?.startsWith("http") == true) homeState.searchQuery else homeState.searchQuery?.split(
        "book/"
      )?.last() ?: "index.html",
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
        if (homeState.currentDocPath != null) {
          homeViewModel.onAction(
            HomeScreenAction.SetAsHome(
              homeState.currentDocPath
            )
          )
        }
        // show toast
        Toast.makeText(
          context,
          "Successfully Set as Home",
          Toast.LENGTH_SHORT
        ).show()
        homeViewModel.onAction(HomeScreenAction.ShowMenu(false))
      })
      DropdownMenuItem(leadingIcon = {
        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Favorites")
      }, text = {
        Text("All Favorites")
      }, onClick = {
        homeViewModel.onAction(HomeScreenAction.ShowFavoritesPopup(true))
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
        if (homeState.currentDocPath != null) {
          homeViewModel.onAction(
            if (isFavorite) HomeScreenAction.RemoveFavorite(homeState.currentDocPath)
            else HomeScreenAction.AddFavorite(
              homeState.currentDocPath
            )
          )
        }
        Toast.makeText(
          context,
          if (isFavorite) "Removed from Favorites" else "Successfully Added to Favorites",
          Toast.LENGTH_SHORT
        ).show()
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
        homeViewModel.onAction(HomeScreenAction.ShowHistoryPopup(true))
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
        homeViewModel.onAction(
          HomeScreenAction.ResetApp(
            navigateSetupPage = {
              navController.navigate(SelectLanguageOfBookNav)
            })
        )
        homeViewModel.onAction(HomeScreenAction.ShowMenu(false))
      })
    }
  })
}


@Composable
fun RustDocumentationScreen(
  homeViewModel: HomeScreenViewModel,
  webClient: WebViewClient,
  modifier: Modifier = Modifier,
  initPath: String
) {
  AndroidView(
    modifier = modifier.fillMaxSize(), factory = { context ->
      WebView(context).apply {
        webViewClient = webClient
        settings.javaScriptEnabled = true
        settings.allowFileAccess = true
        settings.domStorageEnabled = true
        loadUrl(initPath)
        homeViewModel.onAction(HomeScreenAction.WebViewInstance(this, initPath))
      }
    })
}

@Composable
fun ListPopup(
    title: String,
    items: List<String>,
    onDismiss: () -> Unit,
    onItemClick: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            LazyColumn {
                items(items.size) { index ->
                    val item = items[index]
                    Text(
                        text = item.split("book/").lastOrNull() ?: item,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onItemClick(item) }
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    )
                }
                if (items.isEmpty()) {
                    item {
                        Text(
                            text = "No items found",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        properties = DialogProperties(dismissOnClickOutside = true)
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
  val previewNavController = NavHostController(LocalContext.current)
  HomeScreen(
    nav = previewNavController,
    initPath = "file:///android_asset/index.html",
  )
}
