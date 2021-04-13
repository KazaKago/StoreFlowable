package com.kazakago.storeflowable

import com.kazakago.storeflowable.core.FlowableState

interface StoreFlowable<KEY, DATA> {

    fun publish(forceRefresh: Boolean = false): FlowableState<DATA>

    suspend fun get(type: AsDataType = AsDataType.Mix): DATA

    suspend fun validate()

    suspend fun refresh(clearCacheWhenFetchFails: Boolean = true, continueWhenError: Boolean = true)

    suspend fun update(newData: DATA?)
}
