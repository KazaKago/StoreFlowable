package com.kazakago.cacheflowable.sample.viewmodel.livedata

import androidx.annotation.MainThread

class MutableLiveEvent<T> : LiveEvent<T>() {

    @MainThread
    public override fun call(t: T) {
        super.call(t)
    }

}