package android.mohamed.worldwidenews.ui

import android.mohamed.worldwidenews.dataModels.Article
import android.mohamed.worldwidenews.databinding.FragmentArticleBinding
import android.mohamed.worldwidenews.databinding.FragmentSavedNewsBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide

class ArticleFragment : Fragment() {
    private lateinit var binding: FragmentArticleBinding
    private lateinit var article: Article
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
        binding.articleLink.setOnClickListener {
            val action = ArticleFragmentDirections.actionArticleFragmentToWebFragment(article.url)
            findNavController().navigate(action)
        }
        return binding.root
    }

    private fun setupUI(article: Article) {
        binding.articleTitle.text = article.title
        binding.articleDate.text = article.publishedAt?.substring(0, 10)
        binding.articleDescription.text = article.description
        Glide.with(requireContext()).load(article.urlToImage).into(binding.articleImage)
    }
}