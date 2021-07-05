package android.mohamed.worldwidenews.ui

import android.mohamed.worldwidenews.databinding.FragmentSearchNewsBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class SearchNewsFragment : Fragment() {
    private lateinit var binding : FragmentSearchNewsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        return binding.root
    }
}