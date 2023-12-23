package com.cleanarchitectkotlinflowhiltsimplestway.di

import com.cleanarchitectkotlinflowhiltsimplestway.presentation.quotes.QuotesContentViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@EntryPoint
@InstallIn(ActivityComponent::class)
interface ViewModelProviders {
  fun quotesContentViewModelFactory(): QuotesContentViewModel.QuotesContentViewModelFactory
}