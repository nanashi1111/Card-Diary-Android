package com.cleanarchitectkotlinflowhiltsimplestway.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.DiaryPostData

@Dao
interface DiaryDao {

  @Query("select * from DiaryPostData where date between :startDate and :endDate")
  fun getDiaryPosts(startDate: Long, endDate: Long): List<DiaryPostData>

  @Query("select * from DiaryPostData where date = :date")
  fun getDiaryPost(date: Long): DiaryPostData

  @Insert
  fun saveDiaryPost(post: DiaryPostData)

  @Update(entity = DiaryPostData::class)
  fun updateDiaryPost(post: DiaryPostData)

  @Query("select * from DiaryPostData where title like '%' || :query || '%' or content like '%' || :query || '%'")
  fun searchPost(query: String): List<DiaryPostData>

}