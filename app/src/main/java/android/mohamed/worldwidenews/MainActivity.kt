package android.mohamed.worldwidenews

import android.mohamed.worldwidenews.databinding.ActivityMainBinding
import android.mohamed.worldwidenews.viewModels.NewsViewModel
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel : NewsViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.test.setOnClickListener {
            viewModel.getBreakingNews("us")
        }

    }
}