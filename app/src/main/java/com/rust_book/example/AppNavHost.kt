package com.rust_book.example

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.rust_book.example.presentation.home.HomeScreen
import com.rust_book.example.presentation.setup.DownloadBookFileScreen
import com.rust_book.example.presentation.setup.SelectLanguageOfBook
import kotlinx.serialization.Serializable

@Composable
fun AppNavHost(
  navController: NavHostController = rememberNavController(),
  startDestination: Any,
) {
  NavHost(
    navController = navController,
    startDestination = startDestination
  ) { // Profile is the serializable object
    composable<SelectLanguageOfBookNav> { // Use the type for the route
      SelectLanguageOfBook(navController)
    }
    composable<DownloadBookFilesNav> {
      val downloadUrl = it.toRoute<DownloadBookFilesNav>().downloadUrl
      DownloadBookFileScreen(navController, downloadUrl)
    }
    composable<HomeScreenNav> {
      val initPath = it.toRoute<HomeScreenNav>().initPath
      HomeScreen(navController, initPath)
    }
  }
}


@Serializable
object SelectLanguageOfBookNav

@Serializable
data class DownloadBookFilesNav(val downloadUrl: String)

@Serializable
data class HomeScreenNav(val initPath: String)