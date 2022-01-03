package android.mohamed.worldwidenews.ui

import android.mohamed.worldwidenews.adapters.ItemCallBacks
import android.mohamed.worldwidenews.adapters.NewsListAdapter
import android.mohamed.worldwidenews.dataModels.Article
import android.mohamed.worldwidenews.databinding.FragmentSavedNewsBinding
import android.mohamed.worldwidenews.viewModels.NewsViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class SavedNewsFragment : Fragment(), ItemCallBacks {
    private lateinit var binding: FragmentSavedNewsBinding
    private val viewModel: NewsViewModel by viewModel()
    private lateinit var newsAdapter: NewsListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedNewsBinding.inflate(inflater, container, false)
        setupRecyclerView()
        return binding.root
    }
    //for swiping items to delete
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(binding.root, "article deleted", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.insertArticle(article)
                    }
                }.show()

            }
        }
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.savedNewsList)
    }

    override fun onStart() {
        super.onStart()
        //get the data from the database
        viewModel.getSavedNews {
            newsAdapter.differ.submitList(it)
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsListAdapter(this)
        binding.savedNewsList.apply {
            adapter = newsAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    override fun onItemClicked(article: Article) {
        val action = SavedNewsFragmentDirections.actionSavedNewsFragmentToArticleFragment(article)
        findNavController().navigate(action)
    }
}