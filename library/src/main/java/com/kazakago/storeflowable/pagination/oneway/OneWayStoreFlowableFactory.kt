package com.kazakago.storeflowable.pagination.oneway

import com.kazakago.storeflowable.BaseStoreFlowableFactory
import com.kazakago.storeflowable.pagination.FetchingResult

/**
 * Abstract factory class for [OneWayStoreFlowable] class.
 *
 * Create a class that implements origin or cache data Input / Output / One-Way-Pagination according to this interface.
 *
 * @param KEY Specify the type that is the key to retrieve the data. If there is only one data to handle, specify the [Unit] type.
 * @param DATA Specify the type of data to be handled.
 */
interface OneWayStoreFlowableFactory<KEY, DATA> : BaseStoreFlowableFactory<KEY, DATA> {

    /**
     * The appending data saving process to cache.
     * You need to merge cached data & new fetched appending data.
     *
     * @param cachedData Currently cached data.
     * @param newData Data to be saved.
     */
    suspend fun saveAppendingDataToCache(cachedData: DATA?, newData: DATA)

    /**
     * The latest data acquisition process from origin.
     *
     * @return [FetchingResult] class including the acquired data.
     */
    suspend fun fetchDataFromOrigin(): FetchingResult<DATA>

    /**
     * Appending data acquisition process from origin.
     *
     * @return [FetchingResult] class including the acquired data.
     */
    suspend fun fetchAppendingDataFromOrigin(cachedData: DATA?): FetchingResult<DATA>
}
