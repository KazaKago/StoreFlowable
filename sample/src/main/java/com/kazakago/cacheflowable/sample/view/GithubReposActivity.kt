package com.kazakago.cacheflowable.sample.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.kazakago.cacheflowable.sample.databinding.ActivityGithubReposBinding
import com.kazakago.cacheflowable.sample.model.GithubRepo
import com.kazakago.cacheflowable.sample.view.items.ErrorItem
import com.kazakago.cacheflowable.sample.view.items.GithubRepoItem
import com.kazakago.cacheflowable.sample.view.items.LoadingItem
import com.kazakago.cacheflowable.sample.viewmodel.GithubReposViewModel
import com.kazakago.cacheflowable.sample.viewmodel.livedata.compositeLiveDataOf
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class GithubReposActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context, userName: String): Intent {
            return Intent(context, GithubReposActivity::class.java).apply {
                putExtra(ParameterName.UserName.name, userName)
            }
        }
    }

    private enum class ParameterName {
        UserName
    }

    private val binding by lazy { ActivityGithubReposBinding.inflate(layoutInflater) }
    private val githubReposGroupAdapter = GroupAdapter<GroupieViewHolder>()
    private val githubReposViewModel by viewModels<GithubReposViewModel> {
        val githubUserName = intent.getStringExtra(ParameterName.UserName.name)!!
        GithubReposViewModel.Factory(application, githubUserName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.githubReposRecyclerView.adapter = githubReposGroupAdapter
        binding.githubReposRecyclerView.addOnBottomReached {
            githubReposViewModel.requestAdditional()
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            githubReposViewModel.request()
        }
        binding.retryButton.setOnClickListener {
            githubReposViewModel.request()
        }
        compositeLiveDataOf(githubReposViewModel.githubRepos, githubReposViewModel.isAdditionalLoading, githubReposViewModel.additionalError).observe(this) {
            val items: List<Group> = mutableListOf<Group>().apply {
                this += createGithubRepoItems(it.first)
                if (it.second) this += createLoadingItem()
                if (it.third != null) this += createErrorItem(it.third!!)
            }
            githubReposGroupAdapter.updateAsync(items)
        }
        githubReposViewModel.isMainLoading.observe(this) {
            binding.progressBar.isVisible = it
        }
        githubReposViewModel.mainError.observe(this) {
            binding.errorGroup.isVisible = (it != null)
            binding.errorTextView.text = it?.toString()
        }
        githubReposViewModel.hideSwipeRefresh.observe(this, "") {
            binding.swipeRefreshLayout.isRefreshing = false
        }
        githubReposViewModel.strongError.observe(this, "") {
            Snackbar.make(binding.root, it.toString(), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun createGithubRepoItems(githubRepos: List<GithubRepo>): List<GithubRepoItem> {
        return githubRepos.map { githubRepo ->
            GithubRepoItem(githubRepo).apply {
                onClick = { githubRepo -> launch(githubRepo.htmlUrl) }
            }
        }
    }

    private fun createLoadingItem(): LoadingItem {
        return LoadingItem()
    }

    private fun createErrorItem(exception: Exception): ErrorItem {
        return ErrorItem(exception).apply {
            onRetry = { githubReposViewModel.retryAdditional() }
        }
    }

    private fun launch(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

}
