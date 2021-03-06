package com.kazakago.storeflowable.example.repository

import com.kazakago.storeflowable.core.FlowableState
import com.kazakago.storeflowable.create
import com.kazakago.storeflowable.example.flowable.GithubMetaFlowableFactory
import com.kazakago.storeflowable.example.flowable.GithubOrgsFlowableFactory
import com.kazakago.storeflowable.example.flowable.GithubReposFlowableFactory
import com.kazakago.storeflowable.example.flowable.GithubUserFlowableFactory
import com.kazakago.storeflowable.example.model.GithubMeta
import com.kazakago.storeflowable.example.model.GithubOrg
import com.kazakago.storeflowable.example.model.GithubRepo
import com.kazakago.storeflowable.example.model.GithubUser

class GithubRepository {

    fun followMeta(): FlowableState<GithubMeta> {
        val githubMetaFlowable = GithubMetaFlowableFactory().create()
        return githubMetaFlowable.publish()
    }

    suspend fun refreshMeta() {
        val githubMetaFlowable = GithubMetaFlowableFactory().create()
        githubMetaFlowable.refresh()
    }

    fun followOrgs(): FlowableState<List<GithubOrg>> {
        val githubOrgsFlowable = GithubOrgsFlowableFactory().create()
        return githubOrgsFlowable.publish()
    }

    suspend fun refreshOrgs() {
        val githubOrgsFlowable = GithubOrgsFlowableFactory().create()
        githubOrgsFlowable.refresh()
    }

    suspend fun requestAdditionalOrgs(continueWhenError: Boolean) {
        val githubOrgsFlowable = GithubOrgsFlowableFactory().create()
        githubOrgsFlowable.requestAdditionalData(continueWhenError)
    }

    fun followUser(userName: String): FlowableState<GithubUser> {
        val githubUserFlowable = GithubUserFlowableFactory(userName).create()
        return githubUserFlowable.publish()
    }

    suspend fun refreshUser(userName: String) {
        val githubUserFlowable = GithubUserFlowableFactory(userName).create()
        githubUserFlowable.refresh()
    }

    fun followRepos(userName: String): FlowableState<List<GithubRepo>> {
        val githubReposFlowable = GithubReposFlowableFactory(userName).create()
        return githubReposFlowable.publish()
    }

    suspend fun refreshRepos(userName: String) {
        val githubReposFlowable = GithubReposFlowableFactory(userName).create()
        githubReposFlowable.refresh()
    }

    suspend fun requestAdditionalRepos(userName: String, continueWhenError: Boolean) {
        val githubReposFlowable = GithubReposFlowableFactory(userName).create()
        githubReposFlowable.requestAdditionalData(continueWhenError)
    }
}
