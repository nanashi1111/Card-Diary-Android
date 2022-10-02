package com.cleanarchitectkotlinflowhiltsimplestway.domain.exception

const val TITLE_EMPTY = 1
const val CONTENT_EMPTY = 2

class InvalidDiaryPostException(val errorCode: Int): Exception() {

}