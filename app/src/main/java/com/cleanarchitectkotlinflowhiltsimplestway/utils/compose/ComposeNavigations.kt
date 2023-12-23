package com.cleanarchitectkotlinflowhiltsimplestway.utils.compose

import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.QuoteCreatorData

sealed class ComposeNavigations(val route: String) {

  companion object {
    const val NAV_QUOTES = "nav_quotes"
    const val NAV_CREATE_QUOTE = "nav_create_quote"
  }
  class QuotesScreen(): ComposeNavigations(NAV_QUOTES)
  class CreateQuoteScreen(val quoteContentView: QuoteCreatorData): ComposeNavigations(NAV_CREATE_QUOTE)
}
