package com.rust_book.example.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.rust_book.example.data.local.appDataStore
import com.rust_book.example.presentation.home.HomeScreenViewModel
import com.rust_book.example.presentation.setup.SetupViewModel
import org.koin.android.ext.koin.androidApplication // Import this for androidApplication()
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
  single<DataStore<Preferences>> {
    androidContext().appDataStore
  }
  viewModel {
    HomeScreenViewModel(
      get<DataStore<Preferences>>(),
      androidApplication(),
    )
  }
  viewModel { // Changed from viewModel() to viewModel { } for consistency
    SetupViewModel(
      androidApplication(), // Pass the Application instance here
      get<DataStore<Preferences>>(),
    )
  }
}
