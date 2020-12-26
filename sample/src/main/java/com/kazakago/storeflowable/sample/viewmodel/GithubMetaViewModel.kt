package com.kazakago.storeflowable.sample.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kazakago.storeflowable.sample.model.GithubMeta
import com.kazakago.storeflowable.sample.repository.GithubRepository
import com.kazakago.storeflowable.sample.viewmodel.livedata.MutableLiveEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GithubMetaViewModel(application: Application) : AndroidViewModel(application) {

    val githubMeta = MutableLiveData<GithubMeta?>()
    val isLoading = MutableLiveData(false)
    val isRefreshing = MutableLiveData(false)
    val error = MutableLiveData<Exception?>()
    val refreshingError = MutableLiveEvent<Exception>()
    private val githubRepository = GithubRepository()

    init {
        subscribe()
    }

    fun request() = viewModelScope.launch {
        isRefreshing.value = true
        githubRepository.requestMeta()
        isRefreshing.value = false
    }

    fun retry() = viewModelScope.launch {
        githubRepository.requestMeta()
    }

    private fun subscribe() = viewModelScope.launch {
        githubRepository.followMeta().collect {
            it.doAction(
                onFixed = {
                    it.content.doAction(
                        onExist = { _githubMeta ->
                            githubMeta.value = _githubMeta
                            isLoading.value = false
                            error.value = null
                        },
                        onNotExist = {
                            githubMeta.value = null
                            isLoading.value = false
                            error.value = null
                        }
                    )
                },
                onLoading = {
                    it.content.doAction(
                        onExist = { _githubMeta ->
                            githubMeta.value = _githubMeta
                            isLoading.value = true
                            error.value = null
                        },
                        onNotExist = {
                            githubMeta.value = null
                            isLoading.value = true
                            error.value = null
                        }
                    )
                },
                onError = { exception ->
                    if (isRefreshing.value == true) refreshingError.call(exception)
                    it.content.doAction(
                        onExist = { _githubMeta ->
                            githubMeta.value = _githubMeta
                            isLoading.value = false
                            error.value = null
                        },
                        onNotExist = {
                            githubMeta.value = null
                            isLoading.value = false
                            error.value = exception
                        }
                    )
                }
            )
        }
    }
}
