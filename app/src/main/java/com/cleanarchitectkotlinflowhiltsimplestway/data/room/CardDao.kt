package com.cleanarchitectkotlinflowhiltsimplestway.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.CardTemplate

@Dao
interface CardDao {

  @Query("select * from CardTemplate where time = :time")
  fun getCard(time: String): CardTemplate?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun updateCard(cardTemplate: CardTemplate)

  @Query("delete from CardTemplate")
  fun deleteAll(): Int
}