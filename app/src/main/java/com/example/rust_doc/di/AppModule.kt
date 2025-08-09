package com.example.rust_doc.di

import android.app.Application // Ensure this import is present
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.rust_doc.data.local.appDataStore
import com.example.rust_doc.presentation.home.HomeScreenViewModel
import com.example.rust_doc.presentation.setup.SetupViewModel
import org.koin.android.ext.koin.androidApplication // Import this for androidApplication()
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel // Updated import for viewModel
import org.koin.dsl.module

val appModule = module {
  single<DataStore<Preferences>> {
    androidContext().appDataStore
  }
  viewModel {
    HomeScreenViewModel(
      get<DataStore<Preferences>>(),
    )
  }
  viewModel { // Changed from viewModel() to viewModel { } for consistency
    SetupViewModel(
      androidApplication(), // Pass the Application instance here
      get<DataStore<Preferences>>(),
    )
  }
}
