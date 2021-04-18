package com.kazakago.storeflowable

import com.kazakago.storeflowable.core.FlowableState
import com.kazakago.storeflowable.core.StateContent
import kotlinx.coroutines.flow.*

internal class StoreFlowableImpl<KEY, DATA>(private val storeFlowableCallback: StoreFlowableCallback<KEY, DATA>) : StoreFlowable<KEY, DATA> {

    private val dataSelector = DataSelector(
        key = storeFlowableCallback.key,
        dataStateManager = storeFlowableCallback.flowableDataStateManager,
        cacheDataManager = storeFlowableCallback,
        originDataManager = storeFlowableCallback,
        needRefresh = { storeFlowableCallback.needRefresh(it) }
    )

    override fun publish(forceRefresh: Boolean): FlowableState<DATA> {
        return storeFlowableCallback.flowableDataStateManager.getFlow(storeFlowableCallback.key)
            .onStart {
                dataSelector.doStateAction(forceRefresh = forceRefresh, clearCacheBeforeFetching = true, clearCacheWhenFetchFails = true, continueWhenError = true, awaitFetching = false)
            }
            .map {
                val data = dataSelector.load()
                val content = StateContent.wrap(data)
                it.mapState(content)
            }
    }

    override suspend fun getData(type: AsDataType): DATA? {
        return prepareData(type).transform {
            val data = dataSelector.load()
            when (it) {
                is DataState.Fixed -> if (data != null && !storeFlowableCallback.needRefresh(data)) emit(data) else emit(null)
                is DataState.Loading -> Unit // do nothing.
                is DataState.Error -> if (data != null && !storeFlowableCallback.needRefresh(data)) emit(data) else emit(null)
            }
        }.first()
    }

    override suspend fun requireData(type: AsDataType): DATA {
        return prepareData(type).transform {
            val data = dataSelector.load()
            when (it) {
                is DataState.Fixed -> if (data != null && !storeFlowableCallback.needRefresh(data)) emit(data) else throw NoSuchElementException()
                is DataState.Loading -> Unit // do nothing.
                is DataState.Error -> if (data != null && !storeFlowableCallback.needRefresh(data)) emit(data) else throw it.exception
            }
        }.first()
    }

    private suspend fun prepareData(type: AsDataType): Flow<DataState> {
        return storeFlowableCallback.flowableDataStateManager.getFlow(storeFlowableCallback.key)
            .onStart {
                when (type) {
                    AsDataType.Mix -> dataSelector.doStateAction(forceRefresh = false, clearCacheBeforeFetching = true, clearCacheWhenFetchFails = true, continueWhenError = true, awaitFetching = true)
                    AsDataType.FromOrigin -> dataSelector.doStateAction(forceRefresh = true, clearCacheBeforeFetching = true, clearCacheWhenFetchFails = true, continueWhenError = true, awaitFetching = true)
                    AsDataType.FromCache -> Unit // do nothing.
                }
            }
    }

    override suspend fun validate() {
        dataSelector.doStateAction(forceRefresh = false, clearCacheBeforeFetching = true, clearCacheWhenFetchFails = true, continueWhenError = true, awaitFetching = true)
    }

    override suspend fun refresh(clearCacheWhenFetchFails: Boolean, continueWhenError: Boolean) {
        dataSelector.doStateAction(forceRefresh = true, clearCacheBeforeFetching = false, clearCacheWhenFetchFails = clearCacheWhenFetchFails, continueWhenError = continueWhenError, awaitFetching = true)
    }

    override suspend fun update(newData: DATA?) {
        dataSelector.update(newData)
    }
}
