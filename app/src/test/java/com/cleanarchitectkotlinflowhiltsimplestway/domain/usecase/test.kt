package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.junit.Test

class test {

  @Test
  fun test() {
    val text1 = MutableSharedFlow<String>()
    val text2 = MutableSharedFlow<String>()
    val number = MutableStateFlow<Int>(1)

    val combine1 = combine(text1, number) {
        text, number ->
      flow<String> {
        emit("Text1 = $text ; number = $number")
      }
    }
    val combine2 = combine(text2, number) {
        text, number ->
      flow<String> {
        emit("Text2 = $text ; number = $number")
      }
    }
    GlobalScope.launch {
      combine1.collect {
        print(it)
        assert(it.equals("A"))
      }
      combine2.collect {
        print(it)
        assert(it.equals("A"))
      }
      text1.emit("Text1")
      text2.emit("Text2")
      delay(500)
      number.emit(4)
    }
  }

}