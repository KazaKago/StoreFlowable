package com.kazakago.storeflowable

/**
 * Result of Fetching from origin.
 *
 * @param DATA Specify the type of data to be handled.
 */
data class FetchingResult<DATA>(
    /**
     * Set the acquired raw data.
     */
    val data: DATA,
    /**
     * Set to `true` if you know at Pagination that there is no more additional data.
     *
     * Has no effect without Pagination.
     */
    val noMoreAdditionalData: Boolean = false,
)
