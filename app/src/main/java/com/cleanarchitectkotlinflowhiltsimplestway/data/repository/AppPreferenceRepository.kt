package com.cleanarchitectkotlinflowhiltsimplestway.data.repository

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

interface AppPreferenceRepository {
  fun isPostTutorialShown(): Boolean
  fun setPostTutorialShown()
}

class AppPreferenceRepositoryImpl @Inject constructor(private val context: Context): AppPreferenceRepository {

  private val pref: SharedPreferences = context.getSharedPreferences("CardDiary", Context.MODE_PRIVATE)

  override fun isPostTutorialShown() = pref.getBoolean("postTutorial", false)

  override fun setPostTutorialShown() = pref.edit().putBoolean("postTutorial", true).apply()

}