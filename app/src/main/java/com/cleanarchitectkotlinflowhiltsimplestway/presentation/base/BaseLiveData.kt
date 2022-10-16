package com.dtv.starter.presenter.base

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class BaseLiveData<T>(val liveData: LiveData<T> = MutableLiveData<T>()) {

    //List subscriber lifecycle hash, used to check if the lifecycle has been subscribed or not
    private val subcribedLifeCycleList = mutableListOf<Int>()

    fun postValue(value: T) {
        if (liveData is MutableLiveData) {
            value?.let {
                liveData.postValue(it)
            }
        }
    }

    fun getValue(): T? = liveData.value

    fun subscribe(lifecycleOwner: LifecycleOwner, action: (T) -> Unit) {
        if (subcribedLifeCycleList.contains(lifecycleOwner.hashCode())) {
            return
        }

        subcribedLifeCycleList.add(lifecycleOwner.hashCode())

        liveData.observe(lifecycleOwner) {
            action(it)
        }
    }
}