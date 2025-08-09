package com.example.rust_doc.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.rust_doc.AppNavHost
import com.example.rust_doc.HomeScreenNav
import com.example.rust_doc.SelectLanguageOfBookNav
import com.example.rust_doc.presentation.home.HomeScreen
import com.example.rust_doc.presentation.setup.SetupViewModel
import com.example.rust_doc.ui.theme.RustDocumentationTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val setupViewModel = koinViewModel<SetupViewModel>()
      val setupModelSate by setupViewModel.state.collectAsState()
      RustDocumentationTheme {
        AppNavHost(
          startDestination = if (setupModelSate.isDownloaded == true) {
            HomeScreenNav(
              setupModelSate.initPath!!
            )
          } else {
            SelectLanguageOfBookNav
          }
        )
      }
    }
  }
}