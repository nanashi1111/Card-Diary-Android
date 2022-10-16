package com.cleanarchitectkotlinflowhiltsimplestway.data.room

import androidx.room.TypeConverter

private const val LIST_IMAGE_SEPERATOR = "|..__..|"

class TypesConverter {

  @TypeConverter
  fun listImagesFromString(value: String): List<String> {
    return value.split(LIST_IMAGE_SEPERATOR)
  }

  @TypeConverter
  fun listImagesToString(images: List<String>): String {
    val sb = StringBuilder()
    for (image in images) {
      sb.append(image)
      if (images.indexOf(image) < images.size - 1) {
        sb.append(LIST_IMAGE_SEPERATOR)
      }
    }
    return sb.toString()
  }

}
