package android.mohamed.worldwidenews.ui


import android.mohamed.worldwidenews.adapters.ItemCallBacks
import android.mohamed.worldwidenews.adapters.NewsListAdapter
import android.mohamed.worldwidenews.dataModels.Article
import android.mohamed.worldwidenews.databinding.FragmentBreakingNewsBinding
import android.mohamed.worldwidenews.utils.NetworkResponse
import android.mohamed.worldwidenews.viewModels.NewsViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class BreakingNewsFragment : Fragment(), ItemCallBacks {
    private val viewModel by viewModel<NewsViewModel>()
    private lateinit var binding: FragmentBreakingNewsBinding
    private lateinit var newsAdapter: NewsListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        setupAdapter()
        binding.floatingButtonBreakingNewsFragment.setOnClickListener {
            viewModel.getBreakingNews("us")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModel.breakingNews.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        hideProgressBar()
                        if(response.data != null)
                            newsAdapter.differ.submitList(response.data.articles.toList())
                    }
                    is NetworkResponse.Error -> {
                        if(response.data != null)
                            newsAdapter.differ.submitList(response.data.articles.toList())
                        val errorMessage = if (response.message?.isEmpty() ?: true)
                            "something went wrong"
                        else
                            response.message.toString()
                        hideProgressBar()
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
        binding.progressBarBreakingNewsFragment.isVisible = true
    }

    private fun hideProgressBar() {
        binding.progressBarBreakingNewsFragment.isVisible = false
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy > 0)
                binding.floatingButtonBreakingNewsFragment.extend()
            else
                binding.floatingButtonBreakingNewsFragment.isExtended = false
        }
    }

    private fun setupAdapter() {
        newsAdapter = NewsListAdapter(this)
        binding.breakingNewsList.apply {
            adapter = newsAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            addOnScrollListener(scrollListener)
        }
    }

    override fun onItemClicked(article: Article) {
        val action = BreakingNewsFragmentDirections.actionBreakingNewsFragmentToArticleFragment(article)
        findNavController().navigate(action)
    }

}