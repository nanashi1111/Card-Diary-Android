package com.cleanarchitectkotlinflowhiltsimplestway.utils.pattern

fun equals(l1: List<Int>, l2: List<Int>): Boolean {
  if (l1.size != l2.size) {
    return false
  }
  for (i in l1.indices) {
    val d1 = l1[i]
    val d2 = l2[i]
    if (d1 != d2) {
      return false
    }
  }
  return true
}