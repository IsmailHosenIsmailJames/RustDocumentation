package com.example.rust_doc.presentation.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rust_doc.DownloadBookFilesNav
import com.example.rust_doc.presentation.setup.models.Books
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectLanguageOfBook(
  navController: NavController,
  setupViewModel: SetupViewModel = koinViewModel()
) {
  val setupModelSate: SetupModelSate by setupViewModel.state.collectAsState()
  val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

  Scaffold(
    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
      TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
          Text(
            text = "Select Language",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold
          )
        },
      )
    },
    floatingActionButton = {
      FloatingActionButton(onClick = {
        navController.navigate(DownloadBookFilesNav(downloadUrl = setupModelSate.selectedBookUrl))
      }) {
        Row(modifier = Modifier.padding(start = 7.dp, end=7.dp)) {
          Text(text = "Next", fontSize = 16.sp)
          Spacer(modifier = Modifier.width(10.dp))
          Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Arrow Forward"
          )
        }
      }
    }

  ) { paddingValues ->
    LazyColumn(modifier = Modifier.padding(paddingValues)) {
      items(
        count = setupModelSate.listOfBooks.size,
        itemContent = { index ->
          BookView(
            setupModelSate.listOfBooks[index],
            setupViewModel,
            setupModelSate
          )
        }
      )
    }
  }
}

@Composable
fun BookView(
  book: Books,
  setupViewModel: SetupViewModel = koinViewModel(),
  setupModelSate: SetupModelSate = SetupModelSate()
) {
  val isSelected = book.link == setupModelSate.selectedBookUrl;
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(5.dp)
      .background(
        color = Color(0xFFF0F0F0),
        shape = RoundedCornerShape(10.dp)
      )
      .clickable(
        onClick = {
          setupViewModel.onAction(SetupAction.SelectBook(book.link))
        }
      )
      .padding(10.dp),

    verticalAlignment = Alignment.CenterVertically,
  ) {
    Spacer(modifier = Modifier.width(30.dp))
    Icon(
      if (isSelected) {
        Icons.Filled.CheckCircle
      } else {
        Icons.Outlined.Circle
      },
      tint = if (isSelected) {
        Color(0xFF009688)
      } else {
        Color.Gray
      },
      contentDescription = null
    )
    Spacer(modifier = Modifier.width(10.dp))
    Text(book.language, fontSize = 18.sp)
  }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BookViewPreview() {
  Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(top = 50.dp)) {
    BookView(
      book = Books(
        link = "https://ismailhosenismailjames.github.io/rust_book_multi_language/book/Danske.zip",
        language = "Danske",
        isComplete = true
      )
    )
  }
}
