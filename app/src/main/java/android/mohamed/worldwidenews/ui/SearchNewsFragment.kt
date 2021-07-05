package android.mohamed.worldwidenews.ui

import android.mohamed.worldwidenews.adapters.ItemCallBacks
import android.mohamed.worldwidenews.adapters.NewsListAdapter
import android.mohamed.worldwidenews.dataModels.Article
import android.mohamed.worldwidenews.databinding.FragmentSearchNewsBinding
import android.mohamed.worldwidenews.utils.Constants.SEARCH_NEWS_TIME_DELAY
import android.mohamed.worldwidenews.utils.NetworkResponse
import android.mohamed.worldwidenews.viewModels.NewsViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchNewsFragment : Fragment(), ItemCallBacks {
    private lateinit var binding: FragmentSearchNewsBinding
    private val viewModel by viewModel<NewsViewModel>()
    private lateinit var newsAdapter: NewsListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        setupAdapter()
        binding.floatingButtonSearchNewsFragment.setOnClickListener {
            viewModel.getSearchNews(binding.inputTextField.text.toString())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModel.searchNews.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        hideProgressBar()
                        if (response.data != null)
                            newsAdapter.differ.submitList(response.data.articles.toList())
                    }
                    is NetworkResponse.Error -> {
                        hideProgressBar()
                        if (response.data != null)
                            newsAdapter.differ.submitList(response.data.articles.toList())
                        val errorMessage = if (response.message?.isEmpty() ?: true)
                            "something went wrong"
                        else
                            response.message.toString()
                        Snackbar.make(
                            binding.root,
                            errorMessage,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is NetworkResponse.Loading -> {
                        showProgressBar()
                    }

                    else -> {
                    }
                }
            }
        }
    }

    private fun showProgressBar() {
        binding.progressBarSearchNewsFragment.isVisible = true
    }

    private fun hideProgressBar() {
        binding.progressBarSearchNewsFragment.isVisible = false
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy > 0)
                binding.floatingButtonSearchNewsFragment.extend()
            else
                binding.floatingButtonSearchNewsFragment.isExtended = false
        }
    }

    override fun onStart() {
        super.onStart()
        var delayJob: Job? = null
        binding.inputTextField.addTextChangedListener {
            delayJob?.cancel()
            delayJob = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                it?.let { text ->
                    if (text.toString().isNotEmpty())
                        viewModel.getSearchNews(text.toString())
                }
            }
        }
    }

    private fun setupAdapter() {
        newsAdapter = NewsListAdapter(this)
        binding.searchNewsList.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = newsAdapter
            addOnScrollListener(scrollListener)
        }
    }

    override fun onItemClicked(article: Article) {
        val action = SearchNewsFragmentDirections.actionSearchNewsFragmentToArticleFragment(article)
        findNavController().navigate(action)
    }
}