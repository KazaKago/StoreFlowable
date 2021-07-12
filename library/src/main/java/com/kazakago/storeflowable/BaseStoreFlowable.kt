package com.kazakago.storeflowable

import com.kazakago.storeflowable.origin.GettingFrom

/**
 * Provides input / output methods that abstract the data acquisition destination.
 *
 * This class is generated from [StoreFlowableFactory.create].
 *
 * @param KEY Specify the type that is the key to retrieve the data. If there is only one data to handle, specify the [Unit] type.
 * @param DATA Specify the type of data to be handled.
 */
interface BaseStoreFlowable<KEY, DATA> {

    /**
     * Returns valid data only once.
     *
     * If the data could not be retrieved, it returns null instead.
     * and this method itself does not throw an [Exception].
     *
     * Use [publish] if the state of your data is likely to change.
     *
     * @param from Specifies where to get the data. Default value is [GettingFrom.Both].
     * @return Returns the entity of the data.
     * @see GettingFrom
     * @see requireData
     */
    suspend fun getData(from: GettingFrom = GettingFrom.Both): DATA?

    /**
     * Returns valid data only once.
     *
     * If the data cannot be acquired, an [Exception] will be thrown.
     *
     * Use [publish] if the state of your data is likely to change.
     *
     * @param from Specifies where to get the data. Default value is [GettingFrom.Both].
     * @return Returns the entity of the data.
     * @see GettingFrom
     * @see getData
     */
    suspend fun requireData(from: GettingFrom = GettingFrom.Both): DATA

    /**
     * Checks if the published data is valid.
     *
     * If it is invalid, it will be reacquired from origin.
     * and the new data will be notified.
     */
    suspend fun validate()

    /**
     * Forces a data refresh.
     * and the new data will be notified.
     */
    suspend fun refresh()

    /**
     * Treat the passed data as the latest acquired data.
     * and the new data will be notified.
     *
     * Use when new data is created or acquired externally.
     *
     * @param newData Latest data.
     */
    suspend fun update(newData: DATA?)
}
