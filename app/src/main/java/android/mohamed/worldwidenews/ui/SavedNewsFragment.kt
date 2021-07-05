package android.mohamed.worldwidenews.ui

import android.mohamed.worldwidenews.databinding.FragmentSavedNewsBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class SavedNewsFragment : Fragment() {
    private lateinit var binding : FragmentSavedNewsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedNewsBinding.inflate(inflater, container, false)
        return binding.root
    }
}