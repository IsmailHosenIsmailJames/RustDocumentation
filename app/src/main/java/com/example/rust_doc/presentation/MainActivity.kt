package com.example.rust_doc.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.rust_doc.AppNavHost
import com.example.rust_doc.HomeScreenNav
import com.example.rust_doc.SelectLanguageOfBookNav
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
        if (setupModelSate.isDownloaded == null &&
          (setupModelSate.initPath ?: "").isEmpty()
        ) {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
          }
        } else {
          println("justChangeCurrentDoc1 -> ${setupModelSate.initPath}")
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
}