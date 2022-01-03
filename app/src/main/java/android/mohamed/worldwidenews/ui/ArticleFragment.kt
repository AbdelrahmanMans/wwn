package android.mohamed.worldwidenews.ui

import android.mohamed.worldwidenews.dataModels.Article
import android.mohamed.worldwidenews.databinding.FragmentArticleBinding
import android.mohamed.worldwidenews.databinding.FragmentSavedNewsBinding
import android.mohamed.worldwidenews.viewModels.NewsViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArticleFragment : Fragment() {
    private lateinit var binding: FragmentArticleBinding
    private lateinit var article: Article
    private val viewModel : NewsViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = ArticleFragmentArgs.fromBundle(arguments as Bundle)
        article = args.article
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArticleBinding.inflate(inflater, container, false)
        if (this::article.isInitialized)
            setupUI(article)
        //when the go to link is clicked navigate to the url fragment
        binding.articleLink.setOnClickListener {
            val action = ArticleFragmentDirections.actionArticleFragmentToWebFragment(article.url)
            findNavController().navigate(action)
        }
        //when the save article is clicked save the article to the database
        binding.saveArticle.setOnClickListener {
            viewModel.insertArticle(article)
            Toast.makeText(requireContext(), "article saved", Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }
    //put the data in the views
    private fun setupUI(article: Article) {
        binding.articleTitle.text = article.title
        binding.articleDate.text = article.publishedAt?.substring(0, 10)
        binding.articleDescription.text = article.description
        Glide.with(requireContext()).load(article.urlToImage).into(binding.articleImage)
    }
}