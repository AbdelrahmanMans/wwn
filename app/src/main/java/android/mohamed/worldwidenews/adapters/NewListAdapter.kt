package android.mohamed.worldwidenews.adapters

import android.mohamed.worldwidenews.dataModels.Article
import android.mohamed.worldwidenews.databinding.NewsListItemBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class ItemViewHolder(
    private val binding: NewsListItemBinding,
    private val listener: ItemCallBacks
) :
    RecyclerView.ViewHolder(binding.root) {

    private lateinit var article: Article

    init {
        itemView.setOnClickListener {
            listener.onItemClicked(article)
        }
    }

    fun bind(article: Article) {
        this.article = article
        binding.apply {
            Glide.with(itemView).load(article.urlToImage).into(newsImage)
            newsTitle.text = article.title
            newsDate.text = article.publishedAt?.substring(0, 10)
        }

    }
}

class NewsListAdapter(private val listener: ItemCallBacks) :
    RecyclerView.Adapter<ItemViewHolder>() {
    private lateinit var binding: NewsListItemBinding
    private val differCallBack = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        binding = NewsListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}

interface ItemCallBacks {
    fun onItemClicked(article: Article)
}
