package com.rust_book.example.presentation.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel

@Composable
fun DownloadBookFileScreen(
  navController: NavController,
  downloadUrl: String,
  setupViewModel: SetupViewModel = koinViewModel()
) {
  val state by setupViewModel.state.collectAsState()
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
      CircularProgressIndicator()
      Spacer(modifier = Modifier.height(10.dp))
      Text(if (state.isExtracting) {
        "Extracting..."
      } else {
        "Downloading..."
      })
      Text("Please Wait.")
    }
  }
}