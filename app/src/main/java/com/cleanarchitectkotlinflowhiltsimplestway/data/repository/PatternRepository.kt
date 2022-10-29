package com.cleanarchitectkotlinflowhiltsimplestway.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

interface PatternRepository {

  fun isPatternSetup(): Boolean

  fun savePattern(data: List<Int>)

  fun getPattern(): List<Int>

  fun isPatternCorrect(dots: List<Int>): Boolean
}

class PatternRepositoryImpl(val context: Context, val gson: Gson): PatternRepository {

  override fun isPatternSetup() = getPattern().isNotEmpty()

  private val pref: SharedPreferences = context.getSharedPreferences("PRIVACY", Context.MODE_PRIVATE)


  override fun savePattern(data: List<Int>) {
    val json = gson.toJson(data)
    pref.edit().putString("pattern", json).apply()
  }

  override fun getPattern(): List<Int> {
    val json = pref.getString("pattern", "") ?: ""
    if (json.isEmpty()) {
      return emptyList()
    }
    return gson.fromJson(json, Array<Int>::class.java).toList()
  }

  override fun isPatternCorrect(dots: List<Int>): Boolean {
    if (dots.isEmpty()) {
      return false
    }
    return com.cleanarchitectkotlinflowhiltsimplestway.utils.pattern.equals(dots, getPattern())
  }

}